import axios, { isAxiosError } from "axios";
import type { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from "axios";
import { getAuthToken, getRefreshToken, logout, setAuthTokens } from "@/lib/auth/auth";

let apiLocale = "en";

export const setApiLocale = (locale: string) => {
    apiLocale = locale;
};

interface ApiErrorBody {
    message?: string;
    errors?: Array<{ field?: string; message?: string }>;
}

export const apiErrorStatus = (error: unknown): number | undefined =>
    isAxiosError(error) ? error.response?.status : undefined;

export const apiFieldErrors = (error: unknown): Record<string, string> => {
    if (!isAxiosError(error)) {
        return {};
    }
    const data = error.response?.data as ApiErrorBody | undefined;
    const result: Record<string, string> = {};
    for (const fieldError of data?.errors ?? []) {
        if (fieldError.field && fieldError.message) {
            result[fieldError.field] = fieldError.message;
        }
    }
    return result;
};

export const apiErrorMessage = (error: unknown, fallback: string): string => {
    if (isAxiosError(error)) {
        const data = error.response?.data as ApiErrorBody | undefined;
        const fieldMessages = data?.errors
            ?.map((fieldError) => fieldError.message)
            .filter(Boolean)
            .join(". ");
        if (fieldMessages) {
            return fieldMessages;
        }
        if (data?.message && !Array.isArray(data.errors)) {
            return data.message;
        }
    }
    return fallback;
};

const DEFAULT_API_BASE_URL = import.meta.env.DEV ? "/webapp_war_exploded/api" : "/paw-2025a-10/api";
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
});

type RetriableRequestConfig = InternalAxiosRequestConfig & {
    _retry?: boolean;
    _useRefreshToken?: boolean;
    _skipAuthStore?: boolean;
};

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
    config.headers = config.headers ?? {};
    if (!config.headers["Accept-Language"]) {
        config.headers["Accept-Language"] = apiLocale;
    }
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
    const hasAuthHeader =
        config.headers &&
        (Object.prototype.hasOwnProperty.call(config.headers, "Authorization") ||
            Object.prototype.hasOwnProperty.call(config.headers, "authorization"));
    if (token && !hasAuthHeader) {
        config.headers = config.headers ?? {};
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

apiClient.interceptors.response.use(
    (response) => {
        const config = response.config as RetriableRequestConfig | undefined;
        if (!config?._skipAuthStore) {
            storeTokensFromHeaders(response.headers);
        }
        return response;
    },
    async (error: AxiosError) => {
        const originalRequest = error.config as RetriableRequestConfig | undefined;
        const status = error.response?.status;
        if (!originalRequest || originalRequest._retry || status !== 401) {
            return Promise.reject(error);
        }

        const refreshToken = getRefreshToken();
        if (!refreshToken) {
            logout();
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
            logout();
            return Promise.reject(retryError);
        }
    }
);
