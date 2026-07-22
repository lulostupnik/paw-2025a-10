import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useReports } from "@/hooks/useReports";

describe("useReports", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should resolve reporting user info", async () => {
        const { result } = renderHook(() => useReports());

        await waitFor(() => expect(result.current.data.content[0]?.reportingUser).toBeDefined());

        const report = result.current.data.content[0];
        expect(report.reportingUser).toBeDefined();
        expect(report.reportingUser.username).toBeDefined();
    });

    it("should include report metadata", async () => {
        const { result } = renderHook(() => useReports());

        await waitFor(() => expect(result.current.data.content[0]?.reason).toBe("SPAM"));

        const report = result.current.data.content[0];
        expect(report.reason).toBe("SPAM");
        expect(report.status).toBe("PENDING");
    });

    it("should start in loading state", () => {
        const { result } = renderHook(() => useReports());
        expect(result.current.isLoading).toBe(true);
    });
});
