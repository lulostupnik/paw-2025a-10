import { http, HttpResponse } from "msw";
import { BASE_URL } from "../utils/utils";

interface CatalogItem {
    id: number;
    name: string;
    [key: string]: unknown;
}

export function createCatalogHandlers(endpoint: string, items: CatalogItem[]) {
    return [
        http.get(`${BASE_URL}/${endpoint}`, () => {
            return HttpResponse.json(items, {
                headers: {
                    "x-total-count": String(items.length),
                    link: `<${BASE_URL}/${endpoint}?page=1>; rel="first", <${BASE_URL}/${endpoint}?page=1>; rel="last"`,
                },
            });
        }),
        http.get(`${BASE_URL}/${endpoint}/:id`, ({ params }) => {
            const { id } = params;
            const item = items.find((i) => String(i.id) === String(id));
            if (!item) return new HttpResponse(null, { status: 404 });
            return HttpResponse.json(item);
        }),
        http.post(`${BASE_URL}/${endpoint}`, async ({ request }) => {
            const body = (await request.json()) as Record<string, unknown>;
            return HttpResponse.json({ id: items.length + 1, ...body }, { status: 201 });
        }),
        http.put(`${BASE_URL}/${endpoint}/:id`, async ({ request, params }) => {
            const body = (await request.json()) as Record<string, unknown>;
            return HttpResponse.json({ id: Number(params.id), ...body });
        }),
        http.delete(`${BASE_URL}/${endpoint}/:id`, () => {
            return new HttpResponse(null, { status: 204 });
        }),
    ];
}
