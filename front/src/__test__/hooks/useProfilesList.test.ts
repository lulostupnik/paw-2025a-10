import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useProfilesList } from "@/hooks/profiles/useProfilesList";

describe("useProfilesList", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should return paginated profile summaries", async () => {
        const { result } = renderHook(() => useProfilesList());

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data.content).toHaveLength(2);
        expect(result.current.isError).toBe(false);
    });

    it("should map users to profile summaries", async () => {
        const { result } = renderHook(() => useProfilesList());

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        const profile = result.current.data.content[0];
        expect(profile.username).toBe("testuser");
        expect(profile.firstname).toBe("Test");
        expect(profile.lastname).toBe("User");
    });

    it("should start in loading state", () => {
        const { result } = renderHook(() => useProfilesList());
        expect(result.current.isLoading).toBe(true);
    });

    it("should provide a refetch function", async () => {
        const { result } = renderHook(() => useProfilesList());

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(typeof result.current.refetch).toBe("function");
    });
});
