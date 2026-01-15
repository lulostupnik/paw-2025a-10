const AUTH_TOKEN_KEY = "authToken";
const REFRESH_TOKEN_KEY = "refreshToken";
const USERNAME_KEY = "username";
const ROLE_KEY = "role";
const USER_ID_KEY = "userId";

export function isLoggedIn(): boolean {
    return Boolean(localStorage.getItem(AUTH_TOKEN_KEY));
}

export function isAdmin(): boolean {
    return localStorage.getItem(ROLE_KEY) === "ADMIN";
}

export function getUsername(): string {
    return localStorage.getItem(USERNAME_KEY) || "user";
}

export function getUserId(): number {
    const raw = localStorage.getItem(USER_ID_KEY);
    const parsed = raw ? Number(raw) : NaN;
    return Number.isFinite(parsed) ? parsed : 1;
}

export function getAuthToken(): string | null {
    return localStorage.getItem(AUTH_TOKEN_KEY);
}

export function getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
}

export function setAuthTokens(tokens: { authToken?: string | null; refreshToken?: string | null }) {
    if (tokens.authToken) {
        localStorage.setItem(AUTH_TOKEN_KEY, tokens.authToken);
    }
    if (tokens.refreshToken) {
        localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
    }
}

export function setSession(session: { username?: string; role?: string | null; userId?: number | null }) {
    if (session.username) {
        localStorage.setItem(USERNAME_KEY, session.username);
    }
    if (typeof session.role === "string") {
        localStorage.setItem(ROLE_KEY, session.role);
    }
    if (typeof session.userId === "number" && Number.isFinite(session.userId)) {
        localStorage.setItem(USER_ID_KEY, String(session.userId));
    }
}

export function loginFake(opts?: { admin?: boolean }) {
    localStorage.setItem(AUTH_TOKEN_KEY, "dev-token");
    localStorage.setItem(REFRESH_TOKEN_KEY, "dev-refresh-token");
    setSession({
        username: "username",
        role: opts?.admin ? "ADMIN" : "USER",
        userId: 1,
    });
}

export function logout() {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USERNAME_KEY);
    localStorage.removeItem(ROLE_KEY);
    localStorage.removeItem(USER_ID_KEY);
}
