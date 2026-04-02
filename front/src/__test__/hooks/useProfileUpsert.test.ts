import { describe, it, expect, vi, beforeEach } from "vitest";
import { renderHook, act } from "../setup/utils";
import { useProfileUpsert } from "@/hooks/profiles/useProfileUpsert";
import * as usersApi from "@/lib/api/users";

describe("useProfileUpsert", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
        vi.restoreAllMocks();
    });

    it("should start with initial state", () => {
        const { result } = renderHook(() => useProfileUpsert());
        expect(result.current.isLoading).toBe(false);
        expect(result.current.isError).toBe(false);
        expect(result.current.error).toBeNull();
    });

    it("should call updateUserProfile on updateProfile", async () => {
        localStorage.setItem("userId", "1");
        const spy = vi.spyOn(usersApi, "updateUserProfile").mockResolvedValue({} as usersApi.RegisteredUser);
        const { result } = renderHook(() => useProfileUpsert());

        await act(async () => {
            await result.current.updateProfile({ firstname: "Updated" });
        });

        expect(spy).toHaveBeenCalledWith(1, { firstname: "Updated" });
        expect(result.current.isLoading).toBe(false);
    });

    it("should call updateUserPassword on updatePassword", async () => {
        localStorage.setItem("userId", "1");
        const spy = vi.spyOn(usersApi, "updateUserPassword").mockResolvedValue();
        const { result } = renderHook(() => useProfileUpsert());

        await act(async () => {
            await result.current.updatePassword({ password: "NewPass123" });
        });

        expect(spy).toHaveBeenCalledWith(1, "NewPass123");
        expect(result.current.isLoading).toBe(false);
    });

    it("should call updateUserProfilePicture on updatePicture", async () => {
        localStorage.setItem("userId", "1");
        const spy = vi.spyOn(usersApi, "updateUserProfilePicture").mockResolvedValue();
        const file = new File(["test"], "photo.jpg", { type: "image/jpeg" });
        const { result } = renderHook(() => useProfileUpsert());

        await act(async () => {
            await result.current.updatePicture({ picture: file });
        });

        expect(spy).toHaveBeenCalledWith(1, file);
    });

    it("should set error when userId is missing", async () => {
        const { result } = renderHook(() => useProfileUpsert());

        await act(async () => {
            try {
                await result.current.updateProfile({ firstname: "Test" });
            } catch {
                // expected
            }
        });

        expect(result.current.isError).toBe(true);
        expect(result.current.error).toBe("missing-user-id");
    });
});
