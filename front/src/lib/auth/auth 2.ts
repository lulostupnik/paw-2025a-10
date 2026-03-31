export function isLoggedIn(): boolean {
    return Boolean(localStorage.getItem("token"));
}

export function isAdmin(): boolean {
    return localStorage.getItem("role") === "ADMIN";
}

export function getUsername(): string {
    return localStorage.getItem("username") || "user";
}

export function getUserId(): number {
    const raw = localStorage.getItem("userId");
    const parsed = raw ? Number(raw) : NaN;
    return Number.isFinite(parsed) ? parsed : 1;
}

export function loginFake(_opts?: { admin?: boolean }) {
    localStorage.setItem("token", "dev-token");
    localStorage.setItem("username", "username");
    localStorage.setItem("role", "ADMIN");
    localStorage.setItem("userId", "1");
}

export function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");
}
