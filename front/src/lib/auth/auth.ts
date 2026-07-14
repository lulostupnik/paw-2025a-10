const AUTH_TOKEN_KEY = "authToken";
const REFRESH_TOKEN_KEY = "refreshToken";
const USERNAME_KEY = "username";
const IS_ADMIN_KEY = "isAdmin";
const USER_ID_KEY = "userId";
const EMAIL_KEY = "email";
const PROFILE_PICTURE_URL_KEY = "profilePictureUrl";
const PROFILE_PICTURE_VERSION_KEY = "profilePictureVersion";

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
    return getStoredValue(IS_ADMIN_KEY) === "true";
}

export function getUsername(): string {
    return getStoredValue(USERNAME_KEY) || "user";
}

export function getEmail(): string | null {
    return getStoredValue(EMAIL_KEY);
}

export function getUserId(): number | null {
    const raw = getStoredValue(USER_ID_KEY);
    const parsed = raw ? Number(raw) : NaN;
    return Number.isFinite(parsed) ? parsed : null;
}

export function getProfilePictureUrl(): string | null {
    return getStoredValue(PROFILE_PICTURE_URL_KEY);
}

export function getProfilePictureVersion(): string | null {
    return getStoredValue(PROFILE_PICTURE_VERSION_KEY);
}

export function withProfilePictureVersion(url?: string | null): string | null {
    if (!url) {
        return null;
    }
    const version = getProfilePictureVersion();
    if (!version) {
        return url;
    }
    const separator = url.includes("?") ? "&" : "?";
    return `${url}${separator}v=${encodeURIComponent(version)}`;
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
    email?: string | null;
    isAdmin?: boolean | null;
    userId?: number | null;
    profilePictureUrl?: string | null;
    profilePictureVersion?: string | null;
    storage?: AuthStorage;
}) {
    const storage = session.storage ?? getActiveStorage();
    if (session.username) {
        setStoredValue(USERNAME_KEY, session.username, storage);
    }
    if (session.email) {
        setStoredValue(EMAIL_KEY, session.email, storage);
    }
    if (typeof session.isAdmin === "boolean") {
        setStoredValue(IS_ADMIN_KEY, String(session.isAdmin), storage);
    }
    if (typeof session.userId === "number" && Number.isFinite(session.userId)) {
        setStoredValue(USER_ID_KEY, String(session.userId), storage);
    }
    if (session.profilePictureUrl) {
        setStoredValue(PROFILE_PICTURE_URL_KEY, session.profilePictureUrl, storage);
    } else if (session.profilePictureUrl === null) {
        clearStoredValue(PROFILE_PICTURE_URL_KEY, "local");
        clearStoredValue(PROFILE_PICTURE_URL_KEY, "session");
    }
    if (session.profilePictureVersion) {
        setStoredValue(PROFILE_PICTURE_VERSION_KEY, session.profilePictureVersion, storage);
    } else if (session.profilePictureVersion === null) {
        clearStoredValue(PROFILE_PICTURE_VERSION_KEY, "local");
        clearStoredValue(PROFILE_PICTURE_VERSION_KEY, "session");
    }
}

export function logout() {
    [AUTH_TOKEN_KEY, REFRESH_TOKEN_KEY, USERNAME_KEY, IS_ADMIN_KEY, USER_ID_KEY, EMAIL_KEY, PROFILE_PICTURE_URL_KEY, PROFILE_PICTURE_VERSION_KEY].forEach((key) => {
        clearStoredValue(key, "local");
        clearStoredValue(key, "session");
    });
}
