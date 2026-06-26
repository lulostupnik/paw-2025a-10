import { http, HttpResponse } from "msw";
import { BASE_URL } from "../utils/utils";
import { createMockUser } from "../utils/factories";

const defaultUser = createMockUser();
const secondUser = createMockUser({
    id: 2,
    username: "seconduser",
    email: "second@example.com",
    firstname: "Second",
    lastname: "User",
    links: {
        selfUrl: `${BASE_URL}/users/2`,
        profilePictureUrl: `${BASE_URL}/users/2/profilePicture`,
        universityUrl: `${BASE_URL}/universities/1`,
        careerUrl: `${BASE_URL}/careers/1`,
        journeyUrl: null,
    },
});

export const usersHandlers = [
    http.get(`${BASE_URL}/users`, () => {
        return HttpResponse.json([defaultUser, secondUser], {
            headers: {
                "Content-Type": "application/vnd.gotogether.user-list.v1+json",
                "x-total-count": "2",
                link: `<${BASE_URL}/users?page=1>; rel="first", <${BASE_URL}/users?page=1>; rel="last"`,
            },
        });
    }),

    http.get(`${BASE_URL}/users/:id`, ({ params }) => {
        const { id } = params;
        if (id === "404") return new HttpResponse(null, { status: 404 });
        if (id === "error") return new HttpResponse(null, { status: 500 });
        if (id === "2") return HttpResponse.json(secondUser, { headers: { "Content-Type": "application/vnd.gotogether.user.v1+json" } });
        return HttpResponse.json(createMockUser({ id: Number(id) || 1 }), { headers: { "Content-Type": "application/vnd.gotogether.user.v1+json" } });
    }),

    http.post(`${BASE_URL}/users`, async ({ request }) => {
        const contentType = request.headers.get("Content-Type") ?? "";
        // Forgot password request rides on POST /users with the user-password media type.
        if (contentType.includes("user-password")) {
            return new HttpResponse(null, { status: 204 });
        }

        const body = (await request.json()) as Record<string, unknown>;

        if (body.email === "conflict@example.com") {
            return new HttpResponse(null, { status: 409 });
        }

        return HttpResponse.json(
            { id: 99, username: body.username, email: body.email, role: "USER" },
            { status: 201, headers: { "Content-Type": "application/vnd.gotogether.user.v1+json" } },
        );
    }),

    http.patch(`${BASE_URL}/users/:id`, async ({ request, params }) => {
        const body = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ ...defaultUser, ...body, id: Number(params.id) }, { headers: { "Content-Type": "application/vnd.gotogether.user.v1+json" } });
    }),

    // User state sub-resources.
    http.put(`${BASE_URL}/users/:id/password`, () => new HttpResponse(null, { status: 204 })),
    http.post(`${BASE_URL}/users/:id/verification`, () => new HttpResponse(null, { status: 204 })),
    http.put(`${BASE_URL}/users/:id/blocked`, () => new HttpResponse(null, { status: 204 })),
    http.get(`${BASE_URL}/users/:id/blocked`, ({ params }) => {
        return HttpResponse.json(
            {
                blocked: false,
                links: { selfUrl: `${BASE_URL}/users/${params.id}/blocked`, userUrl: `${BASE_URL}/users/${params.id}` },
            },
            { headers: { "Content-Type": "application/vnd.gotogether.user-blocked.v1+json" } },
        );
    }),

    http.put(`${BASE_URL}/users/:id/profilePicture`, () => {
        return new HttpResponse(null, { status: 204 });
    }),

    http.get(`${BASE_URL}/users/:id/interests`, () => {
        return HttpResponse.json(
            [
                { interestId: 1, interestName: "Travel" },
                { interestId: 2, interestName: "Music" },
            ],
            {
                headers: {
                    "Content-Type": "application/vnd.gotogether.user-interest-list.v1+json",
                    "x-total-count": "2",
                    link: `<${BASE_URL}/users/1/interests?page=1>; rel="first", <${BASE_URL}/users/1/interests?page=1>; rel="last"`,
                },
            },
        );
    }),

    http.post(`${BASE_URL}/users/:id/interests`, () => {
        return new HttpResponse(null, { status: 201 });
    }),

    http.delete(`${BASE_URL}/users/:userId/interests/:interestId`, () => {
        return new HttpResponse(null, { status: 204 });
    }),

    http.get(`${BASE_URL}/users/:id/rating`, () => {
        return HttpResponse.json({
            attendedEventsRating: 4.2,
            hostedEventsRating: 4.8,
            links: {
                selfUrl: `${BASE_URL}/users/1/rating`,
                userUrl: `${BASE_URL}/users/1`,
            },
        }, { headers: { "Content-Type": "application/vnd.gotogether.user-rating.v1+json" } });
    }),

    http.get(`${BASE_URL}/users/:id/profilePicture`, () => {
        return new HttpResponse(new ArrayBuffer(0), {
            status: 200,
            headers: { "Content-Type": "image/jpeg" },
        });
    }),
];
