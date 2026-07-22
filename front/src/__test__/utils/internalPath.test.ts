import { describe, it, expect } from "vitest";
import { sanitizeInternalPath, INTERNAL_PATH_FALLBACK } from "@/lib/utils/internalPath";


describe("sanitizeInternalPath", () => {
    describe("returns the fallback for non-navigable input", () => {
        it("returns fallback for null / undefined", () => {
            expect(sanitizeInternalPath(null)).toBeNull();
            expect(sanitizeInternalPath(undefined)).toBeNull();
            expect(sanitizeInternalPath(null, INTERNAL_PATH_FALLBACK)).toBe(INTERNAL_PATH_FALLBACK);
        });

        it("returns fallback for empty or whitespace-only strings", () => {
            expect(sanitizeInternalPath("")).toBeNull();
            expect(sanitizeInternalPath("   ")).toBeNull();
            expect(sanitizeInternalPath("\t\n")).toBeNull();
        });

        it("returns fallback for paths not starting with a single slash", () => {
            expect(sanitizeInternalPath("explore")).toBeNull();
            expect(sanitizeInternalPath("./events")).toBeNull();
        });
    });

    describe("blocks open-redirect vectors", () => {
        it("rejects protocol-relative URLs (//host)", () => {
            expect(sanitizeInternalPath("//evil.com")).toBeNull();
            expect(sanitizeInternalPath("//evil.com/path")).toBeNull();
        });

        it("rejects absolute URLs to another origin", () => {
            expect(sanitizeInternalPath("http://evil.com")).toBeNull();
            expect(sanitizeInternalPath("https://evil.com/login")).toBeNull();
        });

        it("rejects the javascript: scheme", () => {
            expect(sanitizeInternalPath("javascript:alert(1)")).toBeNull();
        });

        it("rejects backslashes used to fake a path", () => {
            expect(sanitizeInternalPath("/\\evil.com")).toBeNull();
            expect(sanitizeInternalPath("/foo\\bar")).toBeNull();
        });

        it("rejects control characters and embedded whitespace", () => {
            expect(sanitizeInternalPath("/foo bar")).toBeNull();
            expect(sanitizeInternalPath("/foo\tbar")).toBeNull();
            expect(sanitizeInternalPath("/foo\u0000bar")).toBeNull();
        });

        it("honours the provided fallback when rejecting", () => {
            expect(sanitizeInternalPath("//evil.com", INTERNAL_PATH_FALLBACK)).toBe(INTERNAL_PATH_FALLBACK);
        });
    });

    describe("accepts and normalizes safe internal paths", () => {
        it("returns a simple internal path unchanged", () => {
            expect(sanitizeInternalPath("/explore")).toBe("/explore");
            expect(sanitizeInternalPath("/events/42")).toBe("/events/42");
        });

        it("preserves query string and hash", () => {
            expect(sanitizeInternalPath("/events?page=2&sort=date#top")).toBe("/events?page=2&sort=date#top");
        });

        it("trims surrounding whitespace before validating", () => {
            expect(sanitizeInternalPath("  /explore  ")).toBe("/explore");
        });
    });
});
