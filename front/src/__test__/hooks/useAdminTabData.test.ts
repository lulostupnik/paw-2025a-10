import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import {
    useAdminJourneys,
    useAdminUsers,
    useAdminEvents,
    useAdminUniversities,
    useAdminInterests,
    useAdminCities,
    useAdminCareers,
} from "@/hooks/admin/useAdminTabData";

describe("useAdminTabData", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    describe("useAdminJourneys", () => {
        it("should return paginated admin journeys", async () => {
            const { result } = renderHook(() => useAdminJourneys());

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data.content).toHaveLength(2);
            expect(result.current.isError).toBe(false);
        });

        it("should map journeys to admin format", async () => {
            const { result } = renderHook(() => useAdminJourneys());

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            const journey = result.current.data.content[0];
            expect(journey.id).toBeDefined();
            expect(journey.user).toBeDefined();
            expect(journey.destinationUniversity).toBeDefined();
        });
    });

    describe("useAdminUsers", () => {
        it("should return paginated admin users", async () => {
            const { result } = renderHook(() => useAdminUsers());

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data.content).toHaveLength(2);
            expect(result.current.isError).toBe(false);
        });

        it("should resolve university for each user", async () => {
            const { result } = renderHook(() => useAdminUsers());

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            const user = result.current.data.content[0];
            expect(user.university).toBeDefined();
        });
    });

    describe("useAdminEvents", () => {
        it("should return paginated admin events", async () => {
            const { result } = renderHook(() => useAdminEvents());

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data.content).toHaveLength(2);
            expect(result.current.isError).toBe(false);
        });

        it("should resolve creator and city for each event", async () => {
            const { result } = renderHook(() => useAdminEvents());

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            const event = result.current.data.content[0];
            expect(event.user).toBeDefined();
            expect(event.city).toBeDefined();
        });
    });

    describe("useAdminUniversities", () => {
        it("should return paginated admin universities", async () => {
            const { result } = renderHook(() => useAdminUniversities());

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data.content).toHaveLength(2);
            expect(result.current.data.content[0].name).toBe("MIT");
            expect(result.current.isError).toBe(false);
        });
    });

    describe("useAdminInterests", () => {
        it("should return paginated admin interests", async () => {
            const { result } = renderHook(() => useAdminInterests());

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data.content).toHaveLength(3);
            expect(result.current.data.content[0].name).toBe("Travel");
            expect(result.current.isError).toBe(false);
        });
    });

    describe("useAdminCities", () => {
        it("should return paginated admin cities", async () => {
            const { result } = renderHook(() => useAdminCities());

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data.content).toHaveLength(2);
            expect(result.current.data.content[0].name).toBe("Buenos Aires");
            expect(result.current.isError).toBe(false);
        });
    });

    describe("useAdminCareers", () => {
        it("should return paginated admin careers", async () => {
            const { result } = renderHook(() => useAdminCareers());

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data.content).toHaveLength(2);
            expect(result.current.data.content[0].name).toBe("Computer Science");
            expect(result.current.isError).toBe(false);
        });
    });
});
