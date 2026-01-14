import { apiClient } from "@/lib/api/client";
import { getUserId, setSession } from "@/lib/auth/auth";

export interface LoginCredentials {
    email: string;
    password: string;
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

function normalizeRole(role?: string): string | undefined {
    if (typeof role !== "string" || role.length === 0) {
        return undefined;
    }
    return role.replace(/^ROLE_/i, "").toUpperCase();
}

export async function login(credentials: LoginCredentials): Promise<AuthenticatedUser> {
    const basic = encodeBasicCredentials(credentials);

    await apiClient.get("/", {
        headers: { Authorization: `Basic ${basic}` },
        params: { size: 1 },
    });

    const userId = getUserId();
    if (typeof userId === "number" && Number.isFinite(userId)) {
        const { data } = await apiClient.get<UserDto>(`/users/${userId}`);
        const normalizedRole = normalizeRole(data.role);
        const username = data.username ?? data.email ?? credentials.email;
        const email = data.email ?? credentials.email;
        setSession({ username, role: normalizedRole, userId: data.id });
        return { id: data.id, username, email, role: normalizedRole };
    }

    const fallbackUser: AuthenticatedUser = {
        id: null,
        username: credentials.email,
        email: credentials.email,
        role: undefined,
    };
    setSession({ username: fallbackUser.username });
    return fallbackUser;
}
