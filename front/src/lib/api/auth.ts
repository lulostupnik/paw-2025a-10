import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";
import { setAuthTokens, setSession } from "@/lib/auth/auth";

export interface LoginCredentials {
    email: string;
    password: string;
    remember?: boolean;
}

export interface AuthenticatedUser {
    id: number | null;
    username: string;
    email: string;
    isAdmin?: boolean;
}

interface PrivateUserDto {
    id: number;
    username?: string;
    firstname?: string | null;
    lastname?: string | null;
    email?: string;
    isAdmin?: boolean;
}

interface JwtPayload {
    selfUrl?: string;
    role?: string;
}

function encodeBasicCredentials({ email, password }: LoginCredentials) {
    const raw = `${email}:${password}`;
    if (typeof globalThis === "undefined") {
        return raw;
    }
    if (typeof globalThis.TextEncoder !== "undefined" && typeof globalThis.btoa === "function") {
        const encoder = new TextEncoder();
        const bytes = encoder.encode(raw);
        let binary = "";
        bytes.forEach((byte) => {
            binary += String.fromCharCode(byte);
        });
        return globalThis.btoa(binary);
    }
    // Fallback to btoa for environments where TextEncoder is not available
    if (typeof globalThis.btoa === "function") {
        return globalThis.btoa(raw);
    }
    return raw;
}

function decodeJwtPayload(token: string): JwtPayload | null {
    const parts = token.split(".");
    if (parts.length < 2) {
        return null;
    }
    const payload = parts[1].replace(/-/g, "+").replace(/_/g, "/");
    const padded = payload.padEnd(payload.length + ((4 - (payload.length % 4)) % 4), "=");
    if (typeof globalThis?.atob !== "function") {
        return null;
    }
    try {
        const json = globalThis.atob(padded);
        return JSON.parse(json) as JwtPayload;
    } catch {
        return null;
    }
}

function getHeaderValue(headers: Record<string, unknown>, name: string): string | undefined {
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

export async function login(credentials: LoginCredentials): Promise<AuthenticatedUser> {
    const basic = encodeBasicCredentials(credentials);
    const storage = credentials.remember ? "local" : "session";

    const loginResponse = await apiClient.head("/", {
        headers: { Authorization: `Basic ${basic}` },
    });

    const authToken = getHeaderValue(loginResponse.headers, "x-gotogether-authtoken");
    const refreshToken = getHeaderValue(loginResponse.headers, "x-gotogether-refreshtoken");
    if (authToken || refreshToken) {
        setAuthTokens({ authToken, refreshToken, storage });
    }

    const payload = authToken ? decodeJwtPayload(authToken) : null;
    const selfUrl = payload?.selfUrl;
    if (!selfUrl) {
        throw new Error("login-missing-user-context");
    }

    const isAdmin = payload?.role === "ADMIN";

    // Source of truth for profile data (email) is the user resource, not the JWT.
    // At login we are the resource owner, so the private representation is authorized.
    const { data } = await apiClient.get<PrivateUserDto>(normalizeApiPath(selfUrl), { headers: { Accept: ContentTypes.USER_PRIVATE } });
    const email = data.email ?? credentials.email;
    const username = data.username ?? email;
    setSession({ username, email, isAdmin, userId: data.id, storage });
    return { id: data.id, username, email, isAdmin };
}

export interface PasswordResetRequest {
    email: string;
}

export async function requestPasswordReset(body: PasswordResetRequest): Promise<void> {
    await apiClient.post("/users", body, {headers: {'Content-Type': ContentTypes.USER_PASSWORD}});
}

export interface EmailVerificationResponse {
    id?: number;
    username?: string;
    email?: string;
    validatedAt?: string;
}

export async function verifyEmailToken(token: string): Promise<EmailVerificationResponse> {
    const loginResponse = await apiClient.head("/", {
        headers: { Authorization: `Basic ${token}` },
    });
    return loginResponse.data;
}

export interface PasswordResetWithTokenPayload {
    email: string;
    token: string;
    password: string;
    confirmPassword: string;
}

export async function resetPasswordWithToken(payload: PasswordResetWithTokenPayload, signal?: AbortSignal): Promise<void> {
    const email = payload.email.trim();
    if (!email) {
        throw new Error("missing-email");
    }
    if (payload.password !== payload.confirmPassword) {
        throw new Error("password-mismatch");
    }
    const normalizedToken = payload.token.trim().replace(/\s+/g, "+");
    if (!normalizedToken) {
        throw new Error("missing-token");
    }

    const basic = encodeBasicCredentials({ email, password: normalizedToken });
    const loginResponse = await apiClient.head(
        "/",
        {
            signal,
            headers: { Authorization: `Basic ${basic}` },
            _skipAuthStore: true,
        } as unknown as Parameters<typeof apiClient.head>[1]
    );

    const authToken = getHeaderValue(loginResponse.headers, "x-gotogether-authtoken");
    if (!authToken) {
        throw new Error("token-auth-missing-auth-token");
    }
    const jwtPayload = decodeJwtPayload(authToken);
    const selfUrl = jwtPayload?.selfUrl;
    if (!selfUrl) {
        throw new Error("token-auth-missing-self-url");
    }

    await apiClient.put(
        `${normalizeApiPath(selfUrl)}/password`,
        { password: payload.password },
        {
            signal,
            headers: { Authorization: `Bearer ${authToken}`, "Content-Type": ContentTypes.USER_PASSWORD },
            _skipAuthStore: true,
        } as unknown as Parameters<typeof apiClient.put>[2]
    );
}
