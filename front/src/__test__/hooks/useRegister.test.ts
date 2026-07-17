import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, act } from "../setup/utils";
import { useRegister } from "@/hooks/useRegister";

describe("useRegister", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    const validPayload = {
        email: "new@example.com",
        username: "newuser",
        firstName: "New",
        lastName: "User",
        password: "Password123",
        careerId: 1,
        universityId: 1,
        interestIds: [1],
    };

    it("should start with initial state", () => {
        const { result } = renderHook(() => useRegister());
        expect(result.current.loading).toBe(false);
        expect(result.current.error).toBeNull();
        expect(result.current.success).toBe(false);
    });

    it("should set success to true after successful registration", async () => {
        const { result } = renderHook(() => useRegister());

        await act(async () => {
            await result.current.register(validPayload);
        });

        expect(result.current.success).toBe(true);
        expect(result.current.error).toBeNull();
    });

    it("should set conflict error on 409 response", async () => {
        const { result } = renderHook(() => useRegister());

        await act(async () => {
            try {
                await result.current.register({
                    ...validPayload,
                    email: "conflict@example.com",
                });
            } catch {
            }
        });

        expect(result.current.error).toBe("register.error.conflict");
        expect(result.current.success).toBe(false);
    });

    it("should reset error and success state", async () => {
        const { result } = renderHook(() => useRegister());

        await act(async () => {
            await result.current.register(validPayload);
        });
        expect(result.current.success).toBe(true);

        act(() => {
            result.current.reset();
        });

        expect(result.current.success).toBe(false);
        expect(result.current.error).toBeNull();
    });

    it("should provide a nextPath", () => {
        const { result } = renderHook(() => useRegister());
        expect(typeof result.current.nextPath).toBe("string");
    });
});
