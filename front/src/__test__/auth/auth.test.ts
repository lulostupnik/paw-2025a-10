import { describe, it, expect, beforeEach } from "vitest";
import {
    isLoggedIn,
    isAdmin,
    getUsername,
    getUserId,
    getProfilePictureUrl,
    getAuthToken,
    getRefreshToken,
    setAuthTokens,
    setSession,
    logout,
} from "@/lib/auth/auth";

describe("Auth module", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    describe("setAuthTokens", () => {
        it("should store auth token in localStorage by default", () => {
            setAuthTokens({ authToken: "test-token" });
            expect(localStorage.getItem("authToken")).toBe("test-token");
            expect(sessionStorage.getItem("authToken")).toBeNull();
        });

        it("should store refresh token in localStorage by default", () => {
            setAuthTokens({ refreshToken: "test-refresh" });
            expect(localStorage.getItem("refreshToken")).toBe("test-refresh");
        });

        it("should store tokens in sessionStorage when storage is session", () => {
            setAuthTokens({ authToken: "test-token", refreshToken: "test-refresh", storage: "session" });
            expect(sessionStorage.getItem("authToken")).toBe("test-token");
            expect(sessionStorage.getItem("refreshToken")).toBe("test-refresh");
            expect(localStorage.getItem("authToken")).toBeNull();
        });

        it("should clear the other storage when setting a token", () => {
            localStorage.setItem("authToken", "old-token");
            setAuthTokens({ authToken: "new-token", storage: "session" });
            expect(sessionStorage.getItem("authToken")).toBe("new-token");
            expect(localStorage.getItem("authToken")).toBeNull();
        });

        it("should use session storage when session tokens already exist", () => {
            sessionStorage.setItem("authToken", "existing-session-token");
            setAuthTokens({ refreshToken: "new-refresh" });
            expect(sessionStorage.getItem("refreshToken")).toBe("new-refresh");
        });
    });

    describe("setSession", () => {
        it("should store username", () => {
            setSession({ username: "testuser" });
            expect(localStorage.getItem("username")).toBe("testuser");
        });

        it("should store isAdmin", () => {
            setSession({ isAdmin: true });
            expect(localStorage.getItem("isAdmin")).toBe("true");
        });

        it("should store userId as string", () => {
            setSession({ userId: 42 });
            expect(localStorage.getItem("userId")).toBe("42");
        });

        it("should store profilePictureUrl when provided", () => {
            setSession({ profilePictureUrl: "/webapp/api/users/42/profilePicture" });
            expect(getProfilePictureUrl()).toBe("/webapp/api/users/42/profilePicture");
        });

        it("should use session storage when session tokens exist", () => {
            sessionStorage.setItem("authToken", "session-token");
            setSession({ username: "testuser", storage: "session" });
            expect(sessionStorage.getItem("username")).toBe("testuser");
        });
    });

    describe("isLoggedIn", () => {
        it("should return false when no token exists", () => {
            expect(isLoggedIn()).toBe(false);
        });

        it("should return true when authToken is in localStorage", () => {
            localStorage.setItem("authToken", "some-token");
            expect(isLoggedIn()).toBe(true);
        });

        it("should return true when authToken is in sessionStorage", () => {
            sessionStorage.setItem("authToken", "some-token");
            expect(isLoggedIn()).toBe(true);
        });
    });

    describe("isAdmin", () => {
        it("should return false when no role is set", () => {
            expect(isAdmin()).toBe(false);
        });

        it("should return true when role is ADMIN", () => {
            localStorage.setItem("isAdmin", "true");
            expect(isAdmin()).toBe(true);
        });

        it("should return false when role is not ADMIN", () => {
            localStorage.setItem("isAdmin", "false");
            expect(isAdmin()).toBe(false);
        });
    });

    describe("getUsername", () => {
        it("should return 'user' when no username is stored", () => {
            expect(getUsername()).toBe("user");
        });

        it("should return stored username", () => {
            localStorage.setItem("username", "testuser");
            expect(getUsername()).toBe("testuser");
        });

        it("should prefer sessionStorage over localStorage", () => {
            localStorage.setItem("username", "local-user");
            sessionStorage.setItem("username", "session-user");
            expect(getUsername()).toBe("session-user");
        });
    });

    describe("getUserId", () => {
        it("should return null when no userId is stored", () => {
            expect(getUserId()).toBeNull();
        });

        it("should return parsed number", () => {
            localStorage.setItem("userId", "42");
            expect(getUserId()).toBe(42);
        });

        it("should return null for non-numeric values", () => {
            localStorage.setItem("userId", "invalid");
            expect(getUserId()).toBeNull();
        });
    });

    describe("getAuthToken", () => {
        it("should return null when no token exists", () => {
            expect(getAuthToken()).toBeNull();
        });

        it("should return stored auth token", () => {
            localStorage.setItem("authToken", "my-token");
            expect(getAuthToken()).toBe("my-token");
        });
    });

    describe("getRefreshToken", () => {
        it("should return null when no refresh token exists", () => {
            expect(getRefreshToken()).toBeNull();
        });

        it("should return stored refresh token", () => {
            localStorage.setItem("refreshToken", "my-refresh");
            expect(getRefreshToken()).toBe("my-refresh");
        });
    });

    describe("logout", () => {
        it("should clear all auth keys from both storages", () => {
            localStorage.setItem("authToken", "t");
            localStorage.setItem("refreshToken", "r");
            localStorage.setItem("username", "u");
            localStorage.setItem("isAdmin", "true");
            localStorage.setItem("userId", "1");
            localStorage.setItem("profilePictureUrl", "/webapp/api/users/1/profilePicture");
            sessionStorage.setItem("authToken", "st");

            logout();

            expect(localStorage.getItem("authToken")).toBeNull();
            expect(localStorage.getItem("refreshToken")).toBeNull();
            expect(localStorage.getItem("username")).toBeNull();
            expect(localStorage.getItem("isAdmin")).toBeNull();
            expect(localStorage.getItem("userId")).toBeNull();
            expect(localStorage.getItem("profilePictureUrl")).toBeNull();
            expect(sessionStorage.getItem("authToken")).toBeNull();
        });
    });
});
