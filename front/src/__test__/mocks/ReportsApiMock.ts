import { http, HttpResponse } from "msw";
import { BASE_URL } from "../utils/utils";
import { createMockReport } from "../utils/factories";

const defaultReport = createMockReport();

export const reportsHandlers = [
    http.get(`${BASE_URL}/reports`, () => {
        return HttpResponse.json(
            [
                defaultReport,
                createMockReport({
                    id: 2,
                    reason: "HARASSMENT",
                    status: "UNDER_REVIEW",
                    links: {
                        selfUrl: `${BASE_URL}/reports/2`,
                        reportedUserUrl: `${BASE_URL}/users/2`,
                        reportingUserUrl: `${BASE_URL}/users/1`,
                        targetUrl: `${BASE_URL}/events/1`,
                    },
                }),
            ],
            {
                headers: {
                    "Content-Type": "application/vnd.gotogether.report-list.v1+json",
                    "x-total-count": "2",
                    link: `<${BASE_URL}/reports?page=1>; rel="first", <${BASE_URL}/reports?page=1>; rel="last"`,
                },
            },
        );
    }),

    http.get(`${BASE_URL}/reports/:id`, ({ params }) => {
        const { id } = params;
        if (id === "404") return new HttpResponse(null, { status: 404 });
        return HttpResponse.json(createMockReport({ id: Number(id) }), { headers: { "Content-Type": "application/vnd.gotogether.report.v1+json" } });
    }),

    http.post(`${BASE_URL}/reports`, async ({ request }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ id: 99, ...body, status: "PENDING" }, { status: 201, headers: { "Content-Type": "application/vnd.gotogether.report.v1+json" } });
    }),

    http.patch(`${BASE_URL}/reports/:id`, async ({ request, params }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ ...defaultReport, id: Number(params.id), ...body }, { headers: { "Content-Type": "application/vnd.gotogether.report.v1+json" } });
    }),

    http.delete(`${BASE_URL}/reports/:id`, () => {
        return new HttpResponse(null, { status: 204 });
    }),
];
