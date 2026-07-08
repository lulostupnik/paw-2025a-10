import { http, HttpResponse } from "msw";
import { BASE_URL } from "../utils/utils";
import { createMockJourney } from "../utils/factories";

const defaultJourney = createMockJourney();

export const journeysHandlers = [
    http.get(`${BASE_URL}/journeys`, () => {
        return HttpResponse.json(
            [defaultJourney, createMockJourney({ id: 2, description: "Second journey" })],
            {
                headers: {
                    "Content-Type": "application/vnd.gotogether.journey-list.v1+json",
                    "x-total-count": "2",
                    link: `<${BASE_URL}/journeys?page=1>; rel="first", <${BASE_URL}/journeys?page=1>; rel="last"`,
                },
            },
        );
    }),

    http.get(`${BASE_URL}/journeys/:id`, ({ params }) => {
        const { id } = params;
        if (id === "404") return new HttpResponse(null, { status: 404 });
        if (id === "error") return new HttpResponse(null, { status: 500 });
        return HttpResponse.json(createMockJourney({ id: Number(id) }), { headers: { "Content-Type": "application/vnd.gotogether.journey.v1+json" } });
    }),

    http.post(`${BASE_URL}/journeys`, async ({ request }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ id: 99, ...body }, { status: 201, headers: { "Content-Type": "application/vnd.gotogether.journey.v1+json" } });
    }),

    http.put(`${BASE_URL}/journeys/:id`, async ({ request, params }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ id: Number(params.id), ...body }, { headers: { "Content-Type": "application/vnd.gotogether.journey.v1+json" } });
    }),

    http.delete(`${BASE_URL}/journeys/:id`, () => {
        return new HttpResponse(null, { status: 204 });
    }),

    http.get(`${BASE_URL}/journeys/:id/responses`, () => {
        return HttpResponse.json(
            [
                {
                    id: 1,
                    message: "Nice journey!",
                    dateTime: "2026-08-01T10:00:00Z",
                    links: {
                        authorUrl: `${BASE_URL}/users/2`,
                        selfUrl: `${BASE_URL}/journeys/1/responses/1`,
                        journeyUrl: `${BASE_URL}/journeys/1`,
                    },
                },
            ],
            {
                headers: {
                    "Content-Type": "application/vnd.gotogether.journey-response-list.v1+json",
                    "x-total-count": "1",
                    link: `<${BASE_URL}/journeys/1/responses?page=1>; rel="first", <${BASE_URL}/journeys/1/responses?page=1>; rel="last"`,
                },
            },
        );
    }),

    http.get(`${BASE_URL}/journeys/:journeyId/responses/:responseId`, () => {
        return HttpResponse.json({
            id: 1,
            message: "Nice journey!",
            dateTime: "2026-08-01T10:00:00Z",
        }, { headers: { "Content-Type": "application/vnd.gotogether.journey-response.v1+json" } });
    }),

    http.post(`${BASE_URL}/journeys/:id/responses`, async ({ request }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ id: 10, ...body, dateTime: "2026-08-01T10:30:00Z" }, { status: 201, headers: { "Content-Type": "application/vnd.gotogether.journey-response.v1+json" } });
    }),

    http.patch(`${BASE_URL}/journeys/:journeyId/responses/:responseId`, () => {
        return new HttpResponse(null, { status: 204 });
    }),

    http.get(`${BASE_URL}/journeys/:id/tips`, () => {
        return HttpResponse.json(
            [
                {
                    id: 1,
                    title: "Tip 1",
                    content: "Pack light",
                    dateTime: "2026-07-10T08:00:00Z",
                    links: {
                        selfUrl: `${BASE_URL}/journeys/1/tips/1`,
                        journeyUrl: `${BASE_URL}/journeys/1`,
                    },
                },
            ],
            {
                headers: {
                    "Content-Type": "application/vnd.gotogether.tip-list.v1+json",
                    "x-total-count": "1",
                    link: `<${BASE_URL}/journeys/1/tips?page=1>; rel="first", <${BASE_URL}/journeys/1/tips?page=1>; rel="last"`,
                },
            },
        );
    }),

    http.get(`${BASE_URL}/journeys/:journeyId/tips/:tipId`, () => {
        return HttpResponse.json({
            id: 1,
            title: "Tip 1",
            content: "Pack light",
            dateTime: "2026-07-10T08:00:00Z",
        }, { headers: { "Content-Type": "application/vnd.gotogether.tip.v1+json" } });
    }),

    http.post(`${BASE_URL}/journeys/:id/tips`, async ({ request }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ id: 10, ...body, dateTime: "2026-07-10T09:00:00Z" }, { status: 201, headers: { "Content-Type": "application/vnd.gotogether.tip.v1+json" } });
    }),

    http.put(`${BASE_URL}/journeys/:journeyId/tips/:tipId`, async ({ request }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json(body, { headers: { "Content-Type": "application/vnd.gotogether.tip.v1+json" } });
    }),

    http.delete(`${BASE_URL}/journeys/:journeyId/tips/:tipId`, () => {
        return new HttpResponse(null, { status: 204 });
    }),
];
