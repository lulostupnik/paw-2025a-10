import { describe, it, expect } from "vitest";
import { isEventFull, isEventFuture } from "@/lib/api/events";

describe("isEventFull (derived field)", () => {
    it("is false when there is no attendees limit", () => {
        expect(isEventFull(10, null)).toBe(false);
        expect(isEventFull(10, undefined)).toBe(false);
    });

    it("is false while the count is below the limit", () => {
        expect(isEventFull(4, 5)).toBe(false);
    });

    it("is true at the limit (boundary) and above it", () => {
        expect(isEventFull(5, 5)).toBe(true);
        expect(isEventFull(6, 5)).toBe(true);
    });

    it("treats a missing count as zero", () => {
        expect(isEventFull(undefined, 5)).toBe(false);
        expect(isEventFull(null, 5)).toBe(false);
    });
});

describe("isEventFuture (derived field)", () => {
    it("is false when there is no date", () => {
        expect(isEventFuture(null)).toBe(false);
        expect(isEventFuture(undefined)).toBe(false);
    });

    it("is true for a clearly future date", () => {
        expect(isEventFuture("2999-01-01")).toBe(true);
    });

    it("is false for a clearly past date", () => {
        expect(isEventFuture("2000-01-01")).toBe(false);
    });

    it("combines date and time when both are present", () => {
        expect(isEventFuture("2999-01-01", "10:00")).toBe(true);
        expect(isEventFuture("2000-01-01", "10:00")).toBe(false);
    });
});
