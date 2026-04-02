import { http, HttpResponse } from "msw";
import { BASE_URL } from "../utils/utils";
import { createMockEvent } from "../utils/factories";

const defaultEvent = createMockEvent();

export const eventsHandlers = [
    http.get(`${BASE_URL}/events`, () => {
        return HttpResponse.json([defaultEvent, createMockEvent({ id: 2, title: "Second Event" })], {
            headers: {
                "x-total-count": "2",
                link: `<${BASE_URL}/events?page=1>; rel="first", <${BASE_URL}/events?page=1>; rel="last"`,
            },
        });
    }),

    http.get(`${BASE_URL}/events/:id`, ({ params }) => {
        const { id } = params;
        if (id === "error") return new HttpResponse(null, { status: 500 });
        if (id === "404") return new HttpResponse(null, { status: 404 });
        return HttpResponse.json(createMockEvent({ id: Number(id) }));
    }),

    http.post(`${BASE_URL}/events`, async ({ request }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ id: 99, ...body }, { status: 201 });
    }),

    http.put(`${BASE_URL}/events/:id`, async ({ request, params }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ id: Number(params.id), ...body });
    }),

    http.delete(`${BASE_URL}/events/:id`, () => {
        return new HttpResponse(null, { status: 204 });
    }),

    http.get(`${BASE_URL}/events/:id/responses`, () => {
        return HttpResponse.json(
            [
                {
                    id: 1,
                    message: "Great event!",
                    dateTime: "2026-06-15T20:00:00Z",
                    links: {
                        authorUrl: `${BASE_URL}/users/2`,
                        selfUrl: `${BASE_URL}/events/1/responses/1`,
                        eventUrl: `${BASE_URL}/events/1`,
                    },
                },
            ],
            {
                headers: {
                    "x-total-count": "1",
                    link: `<${BASE_URL}/events/1/responses?page=1>; rel="first", <${BASE_URL}/events/1/responses?page=1>; rel="last"`,
                },
            },
        );
    }),

    http.post(`${BASE_URL}/events/:id/responses`, async ({ request }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ id: 10, ...body, dateTime: "2026-06-15T20:30:00Z" }, { status: 201 });
    }),

    http.delete(`${BASE_URL}/events/:eventId/responses/:responseId`, () => {
        return new HttpResponse(null, { status: 204 });
    }),

    http.get(`${BASE_URL}/events/:id/attendances`, () => {
        return HttpResponse.json(
            [
                {
                    links: {
                        userUrl: `${BASE_URL}/users/1`,
                        selfUrl: `${BASE_URL}/events/1/attendances/1`,
                        eventUrl: `${BASE_URL}/events/1`,
                    },
                },
            ],
            {
                headers: {
                    "x-total-count": "1",
                    link: `<${BASE_URL}/events/1/attendances?page=1>; rel="first", <${BASE_URL}/events/1/attendances?page=1>; rel="last"`,
                },
            },
        );
    }),

    http.post(`${BASE_URL}/events/:id/attendances`, () => {
        return HttpResponse.json({}, { status: 201 });
    }),

    http.get(`${BASE_URL}/events/:eventId/attendances/:userId`, () => {
        return HttpResponse.json({
            links: {
                userUrl: `${BASE_URL}/users/1`,
                selfUrl: `${BASE_URL}/events/1/attendances/1`,
            },
        });
    }),

    http.delete(`${BASE_URL}/events/:eventId/attendances/:userId`, () => {
        return new HttpResponse(null, { status: 204 });
    }),

    http.get(`${BASE_URL}/events/:id/ratings`, () => {
        return HttpResponse.json(
            [
                {
                    id: 1,
                    rating: 5,
                    links: {
                        userUrl: `${BASE_URL}/users/2`,
                        selfUrl: `${BASE_URL}/events/1/ratings/1`,
                        eventUrl: `${BASE_URL}/events/1`,
                    },
                },
            ],
            {
                headers: {
                    "x-total-count": "1",
                    link: `<${BASE_URL}/events/1/ratings?page=1>; rel="first", <${BASE_URL}/events/1/ratings?page=1>; rel="last"`,
                },
            },
        );
    }),

    http.post(`${BASE_URL}/events/:id/ratings`, async ({ request }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ id: 10, ...body }, { status: 201 });
    }),

    http.put(`${BASE_URL}/events/:eventId/ratings/:ratingId`, async ({ request }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json(body);
    }),

    http.delete(`${BASE_URL}/events/:eventId/ratings/:ratingId`, () => {
        return new HttpResponse(null, { status: 204 });
    }),

    http.get(`${BASE_URL}/events/:id/statistics`, () => {
        return HttpResponse.json({
            eventsCreatedByOrganizer: 5,
            eventsOrganizerAttends: 3,
            topCountry: "Argentina",
            topCountryCount: 10,
            totalParticipants: 25,
            maxParticipants: 50,
        });
    }),

    http.put(`${BASE_URL}/events/:id/flyer`, () => {
        return new HttpResponse(new ArrayBuffer(0), { status: 200 });
    }),
];
