import { beforeEach, describe, expect, it } from "vitest";
import { http, HttpResponse } from "msw";
import { apiClient } from "@/lib/api/client";
import { server } from "../../setup/setup";
import { BASE_URL } from "../../utils/utils";

describe("apiClient auth interceptor", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("retries a 401 request once with the refresh token and stores returned tokens", async () => {
        localStorage.setItem("authToken", "expired-access");
        localStorage.setItem("refreshToken", "valid-refresh");
        const authorizations: Array<string | null> = [];

        server.use(
            http.get(`${BASE_URL}/protected`, ({ request }) => {
                authorizations.push(request.headers.get("Authorization"));
                if (authorizations.length === 1) {
                    return new HttpResponse(null, { status: 401 });
                }
                return HttpResponse.json(
                    { ok: true },
                    {
                        headers: {
                            "x-gotogether-authtoken": "fresh-access",
                            "x-gotogether-refreshtoken": "fresh-refresh",
                        },
                    },
                );
            }),
        );

        const response = await apiClient.get("/protected");

        expect(response.data).toEqual({ ok: true });
        expect(authorizations).toEqual(["Bearer expired-access", "Bearer valid-refresh"]);
        expect(localStorage.getItem("authToken")).toBe("fresh-access");
        expect(localStorage.getItem("refreshToken")).toBe("fresh-refresh");
    });

    it("does not retry 401 responses when no refresh token exists", async () => {
        localStorage.setItem("authToken", "expired-access");
        let attempts = 0;

        server.use(
            http.get(`${BASE_URL}/protected`, () => {
                attempts += 1;
                return new HttpResponse(null, { status: 401 });
            }),
        );

        await expect(apiClient.get("/protected")).rejects.toMatchObject({
            response: { status: 401 },
        });
        expect(attempts).toBe(1);
        expect(localStorage.getItem("authToken")).toBeNull();
    });

    it("clears local session when the refresh retry also fails", async () => {
        localStorage.setItem("authToken", "expired-access");
        localStorage.setItem("refreshToken", "expired-refresh");
        const authorizations: Array<string | null> = [];

        server.use(
            http.get(`${BASE_URL}/protected`, ({ request }) => {
                authorizations.push(request.headers.get("Authorization"));
                return new HttpResponse(null, { status: 401 });
            }),
        );

        await expect(apiClient.get("/protected")).rejects.toMatchObject({
            response: { status: 401 },
        });

        expect(authorizations).toEqual(["Bearer expired-access", "Bearer expired-refresh"]);
        expect(localStorage.getItem("authToken")).toBeNull();
        expect(localStorage.getItem("refreshToken")).toBeNull();
    });
});
