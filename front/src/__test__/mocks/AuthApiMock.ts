import { http, HttpResponse } from "msw";
import { BASE_URL } from "../utils/utils";

export const authHandlers = [
    http.head(`${BASE_URL}/`, ({ request }) => {
        const auth = request.headers.get("Authorization") ?? "";

        if (auth.includes("blocked")) {
            return new HttpResponse(null, { status: 423 });
        }
        if (auth.includes("notverified")) {
            return new HttpResponse(null, { status: 403 });
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
