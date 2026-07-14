import { describe, it, expect } from "vitest";
import { AxiosError } from "axios";
import { apiErrorMessage, apiFieldErrors, apiErrorStatus } from "@/lib/api/client";

function axiosError(status: number, data: unknown): AxiosError {
    return new AxiosError("request failed", "ERR_BAD_REQUEST", undefined, undefined, {
        status,
        data,
        statusText: "",
        headers: {},
        config: {} as never,
    });
}

describe("apiErrorStatus", () => {
    it("returns the HTTP status of an axios error", () => {
        expect(apiErrorStatus(axiosError(404, {}))).toBe(404);
    });

    it("returns undefined for a non-axios error", () => {
        expect(apiErrorStatus(new Error("boom"))).toBeUndefined();
        expect(apiErrorStatus(null)).toBeUndefined();
    });
});

describe("apiFieldErrors", () => {
    it("maps the ErrorDto field errors to {field: message}", () => {
        const err = axiosError(400, { errors: [{ field: "email", message: "invalid" }, { field: "name", message: "required" }] });
        expect(apiFieldErrors(err)).toEqual({ email: "invalid", name: "required" });
    });

    it("ignores entries without field or message", () => {
        const err = axiosError(400, { errors: [{ field: "email" }, { message: "orphan" }, { field: "ok", message: "good" }] });
        expect(apiFieldErrors(err)).toEqual({ ok: "good" });
    });

    it("returns an empty object for a non-axios error or no body", () => {
        expect(apiFieldErrors(new Error("x"))).toEqual({});
        expect(apiFieldErrors(axiosError(500, undefined))).toEqual({});
    });
});

describe("apiErrorMessage", () => {
    it("joins the field-error messages with '. '", () => {
        const err = axiosError(400, { errors: [{ field: "a", message: "first" }, { field: "b", message: "second" }] });
        expect(apiErrorMessage(err, "fallback")).toBe("first. second");
    });

    it("uses the top-level message when there are no field errors", () => {
        const err = axiosError(409, { message: "email already in use" });
        expect(apiErrorMessage(err, "fallback")).toBe("email already in use");
    });

    it("ignores the generic validation wrapper message and uses the fallback", () => {
        const err = axiosError(400, { message: "Validation failed", errors: [] });
        expect(apiErrorMessage(err, "fallback")).toBe("fallback");
    });

    it("ignores the generic validation wrapper whatever language it comes in", () => {
        const err = axiosError(400, { message: "La validación falló", errors: [{ field: "email", message: "" }] });
        expect(apiErrorMessage(err, "fallback")).toBe("fallback");
    });

    it("returns the fallback for a non-axios error", () => {
        expect(apiErrorMessage(new Error("boom"), "fallback")).toBe("fallback");
    });
});
