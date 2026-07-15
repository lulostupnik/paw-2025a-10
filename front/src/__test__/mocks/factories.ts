import { http, HttpResponse } from "msw";
import { BASE_URL } from "../utils/utils";

interface CatalogItem {
    id: number;
    name: string;
    [key: string]: unknown;
}

export function createCatalogHandlers(endpoint: string, items: CatalogItem[], contentTypes?: { list: string; single: string }) {
    return [
        http.get(`${BASE_URL}/${endpoint}`, () => {
            return HttpResponse.json(items, {
                headers: {
                    ...(contentTypes ? { "Content-Type": contentTypes.list } : {}),
                    "x-total-count": String(items.length),
                    link: `<${BASE_URL}/${endpoint}?page=1>; rel="first", <${BASE_URL}/${endpoint}?page=1>; rel="last"`,
                },
            });
        }),
        http.get(`${BASE_URL}/${endpoint}/:id`, ({ params }) => {
            const { id } = params;
            const item = items.find((i) => String(i.id) === String(id));
            if (!item) return new HttpResponse(null, { status: 404 });
            return HttpResponse.json(item, contentTypes ? { headers: { "Content-Type": contentTypes.single } } : undefined);
        }),
        http.post(`${BASE_URL}/${endpoint}`, async ({ request }) => {
            const body = (await request.json()) as Record<string, unknown>;
            return HttpResponse.json({ id: items.length + 1, ...body }, { status: 201, ...(contentTypes ? { headers: { "Content-Type": contentTypes.single } } : {}) });
        }),
        http.put(`${BASE_URL}/${endpoint}/:id`, async ({ request, params }) => {
            const body = (await request.json()) as Record<string, unknown>;
            return HttpResponse.json({ id: Number(params.id), ...body }, contentTypes ? { headers: { "Content-Type": contentTypes.single } } : undefined);
        }),
        // La API actualiza los catálogos con PATCH; sin este handler los updates caerían en un 404 del mock.
        http.patch(`${BASE_URL}/${endpoint}/:id`, async ({ request, params }) => {
            const body = (await request.json()) as Record<string, unknown>;
            return HttpResponse.json({ id: Number(params.id), ...body }, contentTypes ? { headers: { "Content-Type": contentTypes.single } } : undefined);
        }),
        http.delete(`${BASE_URL}/${endpoint}/:id`, () => {
            return new HttpResponse(null, { status: 204 });
        }),
    ];
}
