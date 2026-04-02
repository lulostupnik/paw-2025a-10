import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useReports } from "@/hooks/useReports";

describe("useReports", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should return paginated reports", async () => {
        const { result } = renderHook(() => useReports());

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data.content).toHaveLength(2);
        expect(result.current.isError).toBe(false);
    });

    it("should resolve reported user info", async () => {
        const { result } = renderHook(() => useReports());

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        const report = result.current.data.content[0];
        expect(report.reportedUser).toBeDefined();
        expect(report.reportedUser.username).toBeDefined();
    });

    it("should resolve reporting user info", async () => {
        const { result } = renderHook(() => useReports());

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        const report = result.current.data.content[0];
        expect(report.reportingUser).toBeDefined();
        expect(report.reportingUser.username).toBeDefined();
    });

    it("should include report metadata", async () => {
        const { result } = renderHook(() => useReports());

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        const report = result.current.data.content[0];
        expect(report.reason).toBe("SPAM");
        expect(report.status).toBe("PENDING");
    });

    it("should start in loading state", () => {
        const { result } = renderHook(() => useReports());
        expect(result.current.isLoading).toBe(true);
    });
});
