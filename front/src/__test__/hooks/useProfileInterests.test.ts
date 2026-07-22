import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useProfileInterests } from "@/hooks/profiles/useProfileInterests";

describe("useProfileInterests", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should fetch paginated user interests", async () => {
        const { result } = renderHook(() =>
            useProfileInterests({ profileId: "1", page: 1, size: 10 }),
        );

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data.content).toHaveLength(2);
        expect(result.current.data.content[0].name).toBe("Travel");
        expect(result.current.data.content[1].name).toBe("Music");
        expect(result.current.isError).toBe(false);
    });

    it("should start in loading state", () => {
        const { result } = renderHook(() =>
            useProfileInterests({ profileId: "1", page: 1, size: 10 }),
        );
        expect(result.current.isLoading).toBe(true);
    });

    it("should handle 'me' alias when logged in", async () => {
        localStorage.setItem("userId", "1");
        localStorage.setItem("authToken", "test-token");
        const { result } = renderHook(() =>
            useProfileInterests({ profileId: "me", page: 1, size: 10 }),
        );

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data.content).toHaveLength(2);
    });
});
