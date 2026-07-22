import { describe, it, expect } from "vitest";
import { parseApiDate, getTodayIsoDate } from "@/lib/utils/date";

describe("parseApiDate", () => {
    it("parses a date-only value as a LOCAL date (no UTC/timezone shift)", () => {
        const d = parseApiDate("2026-03-15");
        expect(d.getFullYear()).toBe(2026);
        expect(d.getMonth()).toBe(2);
        expect(d.getDate()).toBe(15);
    });

    it("parses a full ISO datetime value", () => {
        const d = parseApiDate("2026-03-15T10:30:00");
        expect(d.getFullYear()).toBe(2026);
        expect(d.getMonth()).toBe(2);
        expect(d.getDate()).toBe(15);
        expect(d.getHours()).toBe(10);
        expect(d.getMinutes()).toBe(30);
    });
});

describe("getTodayIsoDate", () => {
    it("returns today's local date as yyyy-MM-dd", () => {
        const iso = getTodayIsoDate();
        expect(iso).toMatch(/^\d{4}-\d{2}-\d{2}$/);

        const now = new Date();
        const expected = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}-${String(now.getDate()).padStart(2, "0")}`;
        expect(iso).toBe(expected);
    });
});
