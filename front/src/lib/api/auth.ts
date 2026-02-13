import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { setAuthTokens, setSession } from "@/lib/auth/auth";
import { listUsers } from "@/lib/api/users";

export interface LoginCredentials {
    email: string;
    password: string;
    remember?: boolean;
}

export interface AuthenticatedUser {
    id: number | null;
    username: string;
    email: string;
    role?: string | null;
}

interface UserDto {
    id: number;
    username?: string;
    email?: string;
    role?: string;
}

interface JwtPayload {
    selfUrl?: string;
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

function normalizeRole(role?: string): string | undefined {
    if (typeof role !== "string" || role.length === 0) {
        return undefined;
    }
    return role.replace(/^ROLE_/i, "").toUpperCase();
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
    if (selfUrl) {
        const { data } = await apiClient.get<UserDto>(normalizeApiPath(selfUrl));
        const normalizedRole = normalizeRole(data.role);
        const username = data.username ?? data.email ?? credentials.email;
        const email = data.email ?? credentials.email;
        setSession({ username, role: normalizedRole, userId: data.id, storage });
        return { id: data.id, username, email, role: normalizedRole };
    }

    throw new Error("login-missing-user-context");
}

export interface PasswordResetRequest {
    email: string;
}

export async function requestPasswordReset(body: PasswordResetRequest): Promise<void> {
    await apiClient.post("/users", body, {headers: {'Content-Type': 'application/vnd.gotogether.userPassword.v1+json'}});
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

export interface PasswordResetPayload {
    token: string;
    password: string;
    confirmPassword: string;
}

export async function resetPassword(payload: PasswordResetPayload): Promise<void> {
    await apiClient.post("/users", payload, {headers: {'Content-Type': 'application/vnd.gotogether.passwordReset.v1+json'}});
}

export interface PasswordResetWithTokenPayload {
    email: string;
    token: string;
    password: string;
    confirmPassword: string;
}

export async function resetPasswordWithToken(payload: PasswordResetWithTokenPayload, signal?: AbortSignal): Promise<void> {
    const normalizedEmail = payload.email.trim().toLowerCase();
    if (!normalizedEmail) {
        throw new Error("missing-email");
    }
    if (payload.password !== payload.confirmPassword) {
        throw new Error("password-mismatch");
    }

    const usersPage = await listUsers({ search: normalizedEmail, page: 1, size: 10 }, signal);
    const matchedUser = usersPage.content.find((user) => user.email?.trim().toLowerCase() === normalizedEmail);
    if (!matchedUser) {
        throw new Error("user-not-found");
    }

    const basic = encodeBasicCredentials({ email: normalizedEmail, password: payload.token });
    await apiClient.put(
        `/users/${matchedUser.id}/password`,
        { password: payload.password },
        {
            signal,
            headers: { Authorization: `Basic ${basic}` },
            _skipAuthStore: true,
        } as unknown as Parameters<typeof apiClient.put>[2]
    );
}
