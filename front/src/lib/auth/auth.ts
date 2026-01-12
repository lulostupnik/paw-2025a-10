export function isLoggedIn(): boolean {
    return Boolean(localStorage.getItem("token"));
}

export function isAdmin(): boolean {
    return localStorage.getItem("role") === "ADMIN";
}

export function getUsername(): string {
    return localStorage.getItem("username") || "user";
}

export function loginFake(opts?: { admin?: boolean }) {
    localStorage.setItem("token", "dev-token");
    localStorage.setItem("username", "ivovila1");
    localStorage.setItem("role", opts?.admin ? "ADMIN" : "USER");
}

export function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");
}
