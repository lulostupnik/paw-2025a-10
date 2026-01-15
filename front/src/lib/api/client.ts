import axios from "axios";
import type { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from "axios";
import { getAuthToken, getRefreshToken, setAuthTokens } from "@/lib/auth/auth";

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? "/api").replace(/\/$/, "");

export const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        Accept: "application/json",
    },
    withCredentials: true,
});

type RetriableRequestConfig = InternalAxiosRequestConfig & { _retry?: boolean };

function getHeaderValue(headers: AxiosResponse["headers"] | undefined, name: string): string | undefined {
    if (!headers) {
        return undefined;
    }
    const direct = headers[name];
    if (typeof direct === "string") {
        return direct;
    }
    if (Array.isArray(direct)) {
        return direct[0];
    }
    const lower = headers[name.toLowerCase()];
    if (typeof lower === "string") {
        return lower;
    }
    if (Array.isArray(lower)) {
        return lower[0];
    }
    return undefined;
}

function storeTokensFromHeaders(headers: AxiosResponse["headers"] | undefined) {
    const authToken = getHeaderValue(headers, "x-gotogether-authtoken");
    const refreshToken = getHeaderValue(headers, "x-gotogether-refreshtoken");
    if (authToken || refreshToken) {
        setAuthTokens({ authToken, refreshToken });
    }
}

apiClient.interceptors.request.use((config) => {
    const token = getAuthToken();
    if (token) {
        config.headers = config.headers ?? {};
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

apiClient.interceptors.response.use(
    (response) => {
        storeTokensFromHeaders(response.headers);
        return response;
    },
    async (error: AxiosError) => {
        const originalRequest = error.config as RetriableRequestConfig | undefined;
        const status = error.response?.status;
        if (!originalRequest || originalRequest._retry || (status !== 401 && status !== 403)) {
            return Promise.reject(error);
        }

        const refreshToken = getRefreshToken();
        if (!refreshToken) {
            return Promise.reject(error);
        }

        originalRequest._retry = true;
        originalRequest.headers = originalRequest.headers ?? {};
        originalRequest.headers["X-GoTogether-RefreshToken"] = refreshToken;

        try {
            const response = await apiClient.request(originalRequest);
            return response;
        } catch (retryError) {
            return Promise.reject(retryError);
        }
    }
);
