import { http, HttpResponse } from "msw";
import { BASE_URL } from "../utils/utils";

export const authHandlers = [
    http.get(`${BASE_URL}/`, ({ request }) => {
        const auth = request.headers.get("Authorization") ?? "";

        if (auth.includes("blocked")) {
            return HttpResponse.json(
                { status: 403, error: "Forbidden", message: "Your account has been blocked by an administrator." },
                { status: 403 },
            );
        }
        if (auth.includes("notverified")) {
            return HttpResponse.json(
                { status: 403, error: "Forbidden", message: "Your account has not been verified yet" },
                { status: 403 },
            );
        }
        if (auth.includes("invalid")) {
            return new HttpResponse(null, { status: 401 });
        }

        return new HttpResponse(null, {
            status: 200,
            headers: {
                "x-gotogether-authtoken": "eyJhbGciOiJIUzI1NiJ9.eyJzZWxmVXJsIjoiaHR0cDovL2xvY2FsaG9zdC93ZWJhcHAvYXBpL3VzZXJzLzEifQ.fake",
                "x-gotogether-refreshtoken": "fake-refresh-token",
            },
        });
    }),
];
