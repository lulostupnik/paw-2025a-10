import { describe, it, expect, vi, beforeEach } from "vitest";
import { renderHook, act } from "../setup/utils";
import { useAuthGate } from "@/hooks/useAuthGate";
import * as auth from "@/lib/auth/auth";

describe("useAuthGate", () => {
    beforeEach(() => {
        vi.restoreAllMocks();
    });

    it("should start with open = false", () => {
        vi.spyOn(auth, "isLoggedIn").mockReturnValue(false);
        const { result } = renderHook(() => useAuthGate());
        expect(result.current.open).toBe(false);
    });

    it("should run callback when user is logged in", () => {
        vi.spyOn(auth, "isLoggedIn").mockReturnValue(true);
        const callback = vi.fn();
        const { result } = renderHook(() => useAuthGate());

        act(() => {
            result.current.runOrPrompt(callback);
        });

        expect(callback).toHaveBeenCalledTimes(1);
        expect(result.current.open).toBe(false);
    });

    it("should open modal when user is not logged in", () => {
        vi.spyOn(auth, "isLoggedIn").mockReturnValue(false);
        const callback = vi.fn();
        const { result } = renderHook(() => useAuthGate());

        act(() => {
            result.current.runOrPrompt(callback);
        });

        expect(callback).not.toHaveBeenCalled();
        expect(result.current.open).toBe(true);
    });

    it("should close modal when close is called", () => {
        vi.spyOn(auth, "isLoggedIn").mockReturnValue(false);
        const { result } = renderHook(() => useAuthGate());

        act(() => {
            result.current.runOrPrompt(vi.fn());
        });
        expect(result.current.open).toBe(true);

        act(() => {
            result.current.close();
        });
        expect(result.current.open).toBe(false);
    });
});
