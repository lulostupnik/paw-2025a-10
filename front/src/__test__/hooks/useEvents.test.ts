import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useEvents } from "@/hooks/useEvents";

describe("useEvents", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should return events list", async () => {
        const { result } = renderHook(() => useEvents());

        await waitFor(() => expect(result.current.loading).toBe(false));

        expect(result.current.events.content).toHaveLength(2);
        expect(result.current.events.content[0].title).toBe("Test Event");
        expect(result.current.events.content[1].title).toBe("Second Event");
        expect(result.current.error).toBeNull();
    });

    it("should map DTO to EventSummary correctly", async () => {
        const { result } = renderHook(() => useEvents());

        await waitFor(() => expect(result.current.loading).toBe(false));

        const event = result.current.events.content[0];
        expect(event.id).toBe(1);
        expect(event.title).toBe("Test Event");
        expect(event.description).toBe("A test event description");
        expect(event.date).toBe("2030-06-15");
        expect(event.time).toBe("18:00");
        expect(event.attendeesCount).toBe(10);
        expect(event.attendeesLimit).toBe(50);
        expect(event.isFull).toBe(false);
        expect(event.isFuture).toBe(true);
    });

    it("should start in loading state", () => {
        const { result } = renderHook(() => useEvents());
        expect(result.current.loading).toBe(true);
    });

    it("should return pagination info", async () => {
        const { result } = renderHook(() => useEvents());

        await waitFor(() => expect(result.current.loading).toBe(false));

        expect(result.current.events.totalElements).toBe(2);
    });

    it("should provide a refetch function", async () => {
        const { result } = renderHook(() => useEvents());

        await waitFor(() => expect(result.current.loading).toBe(false));

        expect(typeof result.current.refetch).toBe("function");
    });
});
