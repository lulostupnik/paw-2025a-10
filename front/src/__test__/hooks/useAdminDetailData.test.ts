import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import {
    useAdminUserDetailData,
    useAdminUniversityDetailData,
    useAdminInterestDetailData,
    useAdminCityDetailData,
    useAdminCareerDetailData,
} from "@/hooks/useAdminDetailData";

describe("useAdminDetailData", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    describe("useAdminUserDetailData", () => {
        it("should fetch user detail with university and career", async () => {
            const { result } = renderHook(() =>
                useAdminUserDetailData({ id: "1" }),
            );

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data).not.toBeNull();
            expect(result.current.data!.id).toBe(1);
            expect(result.current.data!.username).toBe("testuser");
            expect(result.current.data!.university).toBeDefined();
            expect(result.current.data!.career).toBeDefined();
        });

        it("should be disabled when no id is provided", () => {
            const { result } = renderHook(() => useAdminUserDetailData());
            expect(result.current.data).toBeNull();
            expect(result.current.isLoading).toBe(false);
        });
    });

    describe("useAdminUniversityDetailData", () => {
        it("should fetch university detail with city", async () => {
            const { result } = renderHook(() =>
                useAdminUniversityDetailData({ id: "1" }),
            );

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data).not.toBeNull();
            expect(result.current.data!.name).toBe("MIT");
            expect(result.current.data!.city).toBeDefined();
        });

        it("should be disabled when no id is provided", () => {
            const { result } = renderHook(() =>
                useAdminUniversityDetailData(),
            );
            expect(result.current.data).toBeNull();
        });
    });

    describe("useAdminInterestDetailData", () => {
        it("should fetch interest detail", async () => {
            const { result } = renderHook(() =>
                useAdminInterestDetailData({ id: "1" }),
            );

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data).not.toBeNull();
            expect(result.current.data!.name).toBe("Travel");
        });

        it("should be disabled when no id is provided", () => {
            const { result } = renderHook(() =>
                useAdminInterestDetailData(),
            );
            expect(result.current.data).toBeNull();
        });
    });

    describe("useAdminCityDetailData", () => {
        it("should fetch city detail", async () => {
            const { result } = renderHook(() =>
                useAdminCityDetailData({ id: "1" }),
            );

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data).not.toBeNull();
            expect(result.current.data!.name).toBe("Buenos Aires");
            expect(result.current.data!.country).toBe("Argentina");
        });

        it("should be disabled when no id is provided", () => {
            const { result } = renderHook(() => useAdminCityDetailData());
            expect(result.current.data).toBeNull();
        });
    });

    describe("useAdminCareerDetailData", () => {
        it("should fetch career detail", async () => {
            const { result } = renderHook(() =>
                useAdminCareerDetailData({ id: "1" }),
            );

            await waitFor(() => expect(result.current.isLoading).toBe(false));

            expect(result.current.data).not.toBeNull();
            expect(result.current.data!.name).toBe("Computer Science");
        });

        it("should be disabled when no id is provided", () => {
            const { result } = renderHook(() => useAdminCareerDetailData());
            expect(result.current.data).toBeNull();
        });
    });
});
