import { describe, expect, it } from "vitest";
import { shouldRetryQuery } from "@/lib/api/queryClient";

describe("shouldRetryQuery", () => {
    it.each([400, 401, 403, 404, 415])("does not retry deterministic %s responses", (status) => {
        expect(shouldRetryQuery(0, { response: { status } })).toBe(false);
    });

    it("retries transient failures up to three attempts", () => {
        expect(shouldRetryQuery(0, { response: { status: 500 } })).toBe(true);
        expect(shouldRetryQuery(1, { response: { status: 500 } })).toBe(true);
        expect(shouldRetryQuery(2, { response: { status: 500 } })).toBe(true);
        expect(shouldRetryQuery(3, { response: { status: 500 } })).toBe(false);
    });

    it("retries network-like failures up to three attempts", () => {
        expect(shouldRetryQuery(0, new Error("network"))).toBe(true);
        expect(shouldRetryQuery(3, new Error("network"))).toBe(false);
    });
});
