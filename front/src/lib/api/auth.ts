import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";
import { getAuthToken, setAuthTokens, setSession } from "@/lib/auth/auth";

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
    links?: {
        profilePictureUrl?: string | null;
    } | null;
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

export async function login(credentials: LoginCredentials): Promise<AuthenticatedUser> {
    const basic = encodeBasicCredentials(credentials);
    const storage = credentials.remember ? "local" : "session";

    // GET (not HEAD) so that on an auth error the server's ErrorDto body — carrying the localized
    // reason (blocked / not verified) — reaches the client to be shown to the user.
    const loginResponse = await apiClient.get("/", {
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

    // Source of truth for profile data (email, admin role) is the user resource, not the JWT.
    // At login we are the resource owner, so the private representation is authorized.
    const { data } = await apiClient.get<PrivateUserDto>(normalizeApiPath(selfUrl), { headers: { Accept: ContentTypes.USER } });
    const email = data.email ?? credentials.email;
    const username = data.username ?? email;
    const isAdmin = data.isAdmin ?? false;
    setSession({ username, email, isAdmin, userId: data.id, profilePictureUrl: data.links?.profilePictureUrl ?? null, storage });
    return { id: data.id, username, email, isAdmin };
}

export interface PasswordResetRequest {
    email: string;
}

export async function requestPasswordReset(body: PasswordResetRequest): Promise<void> {
    await apiClient.post("/users", body, { headers: { "Content-Type": ContentTypes.USER_PASSWORD } });
}

// Resending the verification email has no endpoint: it happens automatically server-side when an
// unverified user attempts to log in (see AuthAnywhereFilter).

async function hydrateSessionFromStoredToken(signal?: AbortSignal): Promise<void> {
    const authToken = getAuthToken();
    if (!authToken) {
        return;
    }
    const selfUrl = decodeJwtPayload(authToken)?.selfUrl;
    if (!selfUrl) {
        return;
    }
    try {
        const { data } = await apiClient.get<PrivateUserDto>(normalizeApiPath(selfUrl), {
            signal,
            headers: { Accept: ContentTypes.USER },
        });
        const email = data.email ?? undefined;
        const username = data.username ?? email;
        setSession({ username, email, isAdmin: data.isAdmin ?? false, userId: data.id, profilePictureUrl: data.links?.profilePictureUrl ?? null });
    } catch {
        // Ignore — session hydration is non-critical.
    }
}

export interface EmailVerificationPayload {
    userId: number | string;
    email: string;
    token: string;
}

export async function verifyEmailToken(payload: EmailVerificationPayload, signal?: AbortSignal): Promise<void> {
    const token = payload.token.trim();
    const email = payload.email.trim();
    if (!token) {
        throw new Error("missing-token");
    }
    if (!email) {
        throw new Error("missing-email");
    }
    if (payload.userId === "" || payload.userId === null || payload.userId === undefined) {
        throw new Error("missing-user");
    }

    // The email carries a one-time token; authenticating with Basic email:token makes the server
    // (AuthAnywhereFilter) verify the account, consume the token and reply with the JWTs. 'verified'
    // is an authentication concern, not a user property, so the PATCH body carries no fields.
    const basic = encodeBasicCredentials({ email, password: token });
    await apiClient.patch(
        `/users/${payload.userId}`,
        {},
        { signal, headers: { "Content-Type": ContentTypes.USER, Accept: ContentTypes.USER_PUBLIC, Authorization: `Basic ${basic}` } },
    );

    await hydrateSessionFromStoredToken(signal);
}

export interface PasswordResetWithTokenPayload {
    userId: number | string;
    email: string;
    token: string;
    password: string;
    confirmPassword: string;
}

export async function resetPasswordWithToken(payload: PasswordResetWithTokenPayload, signal?: AbortSignal): Promise<void> {
    if (payload.password !== payload.confirmPassword) {
        throw new Error("password-mismatch");
    }
    const token = payload.token.trim();
    const email = payload.email.trim();
    if (!token) {
        throw new Error("missing-token");
    }
    if (!email) {
        throw new Error("missing-email");
    }
    if (payload.userId === "" || payload.userId === null || payload.userId === undefined) {
        throw new Error("missing-user");
    }

    // The email carries a one-time token; the PATCH authenticates with Basic email:token and the
    // server (AuthAnywhereFilter) verifies it against the token service and replies with the JWTs.
    const basic = encodeBasicCredentials({ email, password: token });
    await apiClient.patch(
        `/users/${payload.userId}`,
        { password: payload.password },
        { signal, headers: { "Content-Type": ContentTypes.USER, Accept: ContentTypes.USER_PUBLIC, Authorization: `Basic ${basic}` } },
    );

    await hydrateSessionFromStoredToken(signal);
}
