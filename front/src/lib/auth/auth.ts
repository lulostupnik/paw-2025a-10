const AUTH_TOKEN_KEY = "authToken";
const REFRESH_TOKEN_KEY = "refreshToken";
const USERNAME_KEY = "username";
const ROLE_KEY = "role";
const USER_ID_KEY = "userId";

type AuthStorage = "local" | "session";

function getStoredValue(key: string): string | null {
    return sessionStorage.getItem(key) ?? localStorage.getItem(key);
}

function clearStoredValue(key: string, storage: AuthStorage) {
    if (storage === "local") {
        localStorage.removeItem(key);
    } else {
        sessionStorage.removeItem(key);
    }
}

function setStoredValue(key: string, value: string, storage: AuthStorage) {
    if (storage === "local") {
        localStorage.setItem(key, value);
        sessionStorage.removeItem(key);
    } else {
        sessionStorage.setItem(key, value);
        localStorage.removeItem(key);
    }
}

function getActiveStorage(): AuthStorage {
    if (sessionStorage.getItem(AUTH_TOKEN_KEY) || sessionStorage.getItem(REFRESH_TOKEN_KEY)) {
        return "session";
    }
    return "local";
}

export function isLoggedIn(): boolean {
    return Boolean(getStoredValue(AUTH_TOKEN_KEY));
}

export function isAdmin(): boolean {
    return getStoredValue(ROLE_KEY) === "ADMIN";
}

export function getUsername(): string {
    return getStoredValue(USERNAME_KEY) || "user";
}

export function getUserId(): number {
    const raw = getStoredValue(USER_ID_KEY);
    const parsed = raw ? Number(raw) : NaN;
    return Number.isFinite(parsed) ? parsed : 1;
}

export function getAuthToken(): string | null {
    return getStoredValue(AUTH_TOKEN_KEY);
}

export function getRefreshToken(): string | null {
    return getStoredValue(REFRESH_TOKEN_KEY);
}

export function setAuthTokens(tokens: {
    authToken?: string | null;
    refreshToken?: string | null;
    storage?: AuthStorage;
}) {
    const storage = tokens.storage ?? getActiveStorage();
    if (tokens.authToken) {
        setStoredValue(AUTH_TOKEN_KEY, tokens.authToken, storage);
    }
    if (tokens.refreshToken) {
        setStoredValue(REFRESH_TOKEN_KEY, tokens.refreshToken, storage);
    }
}

export function setSession(session: {
    username?: string;
    role?: string | null;
    userId?: number | null;
    storage?: AuthStorage;
}) {
    const storage = session.storage ?? getActiveStorage();
    if (session.username) {
        setStoredValue(USERNAME_KEY, session.username, storage);
    }
    if (typeof session.role === "string") {
        setStoredValue(ROLE_KEY, session.role, storage);
    }
    if (typeof session.userId === "number" && Number.isFinite(session.userId)) {
        setStoredValue(USER_ID_KEY, String(session.userId), storage);
    }
}

export function logout() {
    [AUTH_TOKEN_KEY, REFRESH_TOKEN_KEY, USERNAME_KEY, ROLE_KEY, USER_ID_KEY].forEach((key) => {
        clearStoredValue(key, "local");
        clearStoredValue(key, "session");
    });
}
