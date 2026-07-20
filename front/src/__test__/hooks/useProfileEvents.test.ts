import { describe, it, expect, beforeEach, vi } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useProfileEvents } from "@/hooks/profiles/useProfileEvents";

const mockFetchEvents = vi.fn();
const mockBuildProfileEvent = vi.fn();

vi.mock("@/lib/api/events", () => ({
    fetchEvents: (...args: unknown[]) => mockFetchEvents(...args),
    buildProfileEvent: (...args: unknown[]) => mockBuildProfileEvent(...args),
}));

describe("useProfileEvents", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        localStorage.clear();
        sessionStorage.clear();
        mockFetchEvents.mockResolvedValue({
            content: [],
            currentPage: 1,
            pageSize: 6,
            totalPages: 0,
            totalElements: 0,
        });
        mockBuildProfileEvent.mockResolvedValue([]);
    });

    it("should fetch created events for a profile", async () => {
        const { result } = renderHook(() => useProfileEvents("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.created.content).toBeDefined();
        expect(Array.isArray(result.current.created.content)).toBe(true);
        expect(result.current.isError).toBe(false);
    });

    it("should fetch attending events for a profile", async () => {
        const { result } = renderHook(() => useProfileEvents("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.attending.content).toBeDefined();
        expect(Array.isArray(result.current.attending.content)).toBe(true);
    });

    it("should fetch finished events for a profile", async () => {
        const { result } = renderHook(() => useProfileEvents("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.finished.content).toBeDefined();
        expect(Array.isArray(result.current.finished.content)).toBe(true);
    });

    it("should fetch finished events from attended history", async () => {
        renderHook(() => useProfileEvents("1", { finishedPage: 3, size: 6 }));

        await waitFor(() => expect(mockFetchEvents).toHaveBeenCalledTimes(3));

        expect(mockFetchEvents).toHaveBeenNthCalledWith(
            3,
            expect.objectContaining({
                attendedBy: 1,
                beforeDate: expect.any(String),
                page: 3,
                size: 6,
            }),
            expect.any(AbortSignal),
        );
        expect(mockFetchEvents).not.toHaveBeenNthCalledWith(
            3,
            expect.objectContaining({
                creatorId: 1,
            }),
            expect.anything(),
        );
    });

    it("should start in loading state", () => {
        const { result } = renderHook(() => useProfileEvents("1"));
        expect(result.current.isLoading).toBe(true);
    });

    it("should respect enabled param", () => {
        const { result } = renderHook(() =>
            useProfileEvents("1", { enabled: false }),
        );
        expect(result.current.isLoading).toBe(false);
        expect(result.current.created.content).toHaveLength(0);
    });
});
