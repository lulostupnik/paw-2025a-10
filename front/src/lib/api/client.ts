import axios from "axios";
import type { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from "axios";
import { getAuthToken, getRefreshToken, setAuthTokens } from "@/lib/auth/auth";

const DEFAULT_API_BASE_URL = import.meta.env.DEV ? "/webapp/api" : "/api";
export const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL ?? DEFAULT_API_BASE_URL).replace(/\/$/, "");
const apiBasePath = (() => {
    try {
        return new URL(apiBaseUrl, typeof window !== "undefined" ? window.location.origin : "http://localhost").pathname.replace(/\/$/, "");
    } catch {
        return apiBaseUrl.replace(/\/$/, "");
    }
})();
const apiContextPath = apiBasePath.endsWith("/api") ? apiBasePath.slice(0, -"/api".length) : "";

export const normalizeApiPath = (value: string) => {
    if (!value) {
        return value;
    }

    const strip = (input: string) => {
        let next = input;
        if (next.startsWith(apiBaseUrl)) {
            next = next.slice(apiBaseUrl.length);
        }
        if (apiBasePath && next.startsWith(apiBasePath)) {
            next = next.slice(apiBasePath.length);
        } else if (apiContextPath && next.startsWith(apiContextPath)) {
            next = next.slice(apiContextPath.length);
        }
        return next.length === 0 ? "/" : next;
    };

    try {
        const parsed = new URL(value, typeof window !== "undefined" ? window.location.origin : "http://localhost");
        return strip(`${parsed.pathname}${parsed.search}`);
    } catch {
        return strip(value);
    }
};

export const apiClient = axios.create({
    baseURL: apiBaseUrl,
    headers: {
        Accept: "application/json",
    },
    withCredentials: true,
});

type RetriableRequestConfig = InternalAxiosRequestConfig & { _retry?: boolean; _useRefreshToken?: boolean };

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
    const typedConfig = config as RetriableRequestConfig;
    if (typedConfig._useRefreshToken) {
        const refreshToken = getRefreshToken();
        if (refreshToken) {
            config.headers = config.headers ?? {};
            config.headers.Authorization = `Bearer ${refreshToken}`;
        }
        return config;
    }

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
        originalRequest._useRefreshToken = true;
        originalRequest.headers = originalRequest.headers ?? {};
        originalRequest.headers.Authorization = `Bearer ${refreshToken}`;

        try {
            const response = await apiClient.request(originalRequest);
            return response;
        } catch (retryError) {
            return Promise.reject(retryError);
        }
    }
);
