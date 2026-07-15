import { describe, it, expect } from "vitest";
import { http, HttpResponse, type JsonBodyType } from "msw";
import { server } from "../../setup/setup";
import { BASE_URL } from "../../utils/utils";
import { listCities, getCityById, createCity, updateCity, deleteCity } from "@/lib/api/cities";
import { listCareers, getCareerById, createCareer, updateCareer, deleteCareer } from "@/lib/api/careers";
import { listUniversities, getUniversityById, createUniversity, deleteUniversity } from "@/lib/api/universities";
import { listInterests, getInterestById, createInterest, deleteInterest } from "@/lib/api/interests";
import { listCountries } from "@/lib/api/countries";

/**
 * Captura el request que msw recibe para el handler dado, para poder afirmar sobre headers y body.
 */
const captureRequest = (method: "get" | "post" | "patch" | "delete", path: string, response: JsonBodyType, status = 200) => {
    const seen: { headers?: Headers; body?: unknown } = {};
    server.use(
        http[method](`${BASE_URL}${path}`, async ({ request }) => {
            seen.headers = request.headers;
            if (method === "post" || method === "patch") {
                seen.body = await request.json();
            }
            if (status === 204) {
                return new HttpResponse(null, { status });
            }
            return HttpResponse.json(response, { status });
        }),
    );
    return seen;
};

describe("catálogo — listados", () => {
    it("listCities pide el mime versionado de lista y devuelve la página parseada", async () => {
        const seen = captureRequest("get", "/cities", [{ id: 1, name: "Buenos Aires", country: "Argentina" }]);

        const page = await listCities();

        expect(seen.headers?.get("Accept")).toBe("application/vnd.gotogether.city-list.v1+json");
        expect(page.content).toHaveLength(1);
        expect(page.content[0].name).toBe("Buenos Aires");
    });

    it("listCities propaga search/page/size como query params", async () => {
        let url = "";
        server.use(
            http.get(`${BASE_URL}/cities`, ({ request }) => {
                url = request.url;
                return HttpResponse.json([], { headers: { "x-total-count": "0" } });
            }),
        );

        await listCities({ search: "bue", page: 2, size: 5 });

        expect(url).toContain("search=bue");
        expect(url).toContain("page=2");
        expect(url).toContain("size=5");
    });

    it("listCities lee la paginación de los headers, no del body", async () => {
        server.use(
            http.get(`${BASE_URL}/cities`, () =>
                HttpResponse.json([{ id: 1, name: "Buenos Aires" }], {
                    headers: {
                        "x-total-count": "37",
                        link: `<${BASE_URL}/cities?page=1>; rel="first", <${BASE_URL}/cities?page=4>; rel="last"`,
                    },
                }),
            ),
        );

        const page = await listCities();

        // El total y los links salen de X-Total-Count / Link, no del cuerpo (que trae 1 sola ciudad).
        expect(page.totalElements).toBe(37);
        expect(page.content).toHaveLength(1);
        expect(page.last).toContain("page=4");
    });

    it("listCareers / listUniversities / listInterests piden su propio mime de lista", async () => {
        const careers = captureRequest("get", "/careers", [{ id: 1, name: "Computer Science" }]);
        await listCareers();
        expect(careers.headers?.get("Accept")).toBe("application/vnd.gotogether.career-list.v1+json");

        const universities = captureRequest("get", "/universities", [{ id: 1, name: "MIT" }]);
        await listUniversities();
        expect(universities.headers?.get("Accept")).toBe("application/vnd.gotogether.university-list.v1+json");

        const interests = captureRequest("get", "/interests", [{ id: 1, name: "Hiking" }]);
        await listInterests();
        expect(interests.headers?.get("Accept")).toBe("application/vnd.gotogether.interest-list.v1+json");
    });

    it("listCountries devuelve la colección del catálogo", async () => {
        const countries = await listCountries();

        expect(Array.isArray(countries)).toBe(true);
    });
});

describe("catálogo — item", () => {
    it("getCityById pide el mime del item y devuelve el recurso", async () => {
        const seen = captureRequest("get", "/cities/1", { id: 1, name: "Buenos Aires", country: "Argentina" });

        const city = await getCityById(1);

        expect(seen.headers?.get("Accept")).toBe("application/vnd.gotogether.city.v1+json");
        expect(city).toMatchObject({ id: 1, name: "Buenos Aires" });
    });

    it("getCityById propaga el 404 cuando la ciudad no existe", async () => {
        await expect(getCityById(404)).rejects.toMatchObject({ response: { status: 404 } });
    });

    it("getCareerById / getUniversityById / getInterestById resuelven el item", async () => {
        await expect(getCareerById(1)).resolves.toMatchObject({ id: 1 });
        await expect(getUniversityById(1)).resolves.toMatchObject({ id: 1 });
        await expect(getInterestById(1)).resolves.toMatchObject({ id: 1 });
    });
});

describe("catálogo — escritura", () => {
    it("createCity manda el mime del recurso como Content-Type y el countryId en el body", async () => {
        const seen = captureRequest("post", "/cities", { id: 9, name: "Córdoba" }, 201);

        const created = await createCity({ name: "Córdoba", countryId: 1 });

        expect(seen.headers?.get("Content-Type")).toBe("application/vnd.gotogether.city.v1+json");
        expect(seen.body).toEqual({ name: "Córdoba", countryId: 1 });
        expect(created).toMatchObject({ id: 9, name: "Córdoba" });
    });

    it("updateCity usa PATCH (no PUT) contra la URN del item", async () => {
        const seen = captureRequest("patch", "/cities/1", { id: 1, name: "CABA" });

        const updated = await updateCity(1, { name: "CABA", countryId: 1 });

        expect(seen.headers?.get("Content-Type")).toBe("application/vnd.gotogether.city.v1+json");
        expect(seen.body).toEqual({ name: "CABA", countryId: 1 });
        expect(updated.name).toBe("CABA");
    });

    it("deleteCity resuelve con el 204 del server", async () => {
        captureRequest("delete", "/cities/1", null, 204);

        await expect(deleteCity(1)).resolves.toBeUndefined();
    });

    it("createCity propaga el error de validación del server (409 nombre duplicado)", async () => {
        server.use(
            http.post(`${BASE_URL}/cities`, () =>
                HttpResponse.json({ message: "City already exists" }, { status: 409 }),
            ),
        );

        await expect(createCity({ name: "Buenos Aires", countryId: 1 })).rejects.toMatchObject({
            response: { status: 409 },
        });
    });

    it("createCareer manda su propio mime", async () => {
        const seen = captureRequest("post", "/careers", { id: 5, name: "Física" }, 201);

        await createCareer({ name: "Física" });

        expect(seen.headers?.get("Content-Type")).toBe("application/vnd.gotogether.career.v1+json");
        expect(seen.body).toEqual({ name: "Física" });
    });

    it("updateCareer usa PATCH contra la URN del item", async () => {
        const seen = captureRequest("patch", "/careers/1", { id: 1, name: "Ingeniería" });

        await updateCareer(1, { name: "Ingeniería" });

        expect(seen.body).toEqual({ name: "Ingeniería" });
    });

    it("createUniversity manda cityId por id y su propio mime", async () => {
        const seen = captureRequest("post", "/universities", { id: 7, name: "ITBA" }, 201);

        await createUniversity({ name: "ITBA", abbreviation: "ITBA", cityId: 1 });

        expect(seen.headers?.get("Content-Type")).toBe("application/vnd.gotogether.university.v1+json");
        expect(seen.body).toEqual({ name: "ITBA", abbreviation: "ITBA", cityId: 1 });
    });

    it("createInterest manda el nombre y su propio mime", async () => {
        const seen = captureRequest("post", "/interests", { id: 3, name: "Cine" }, 201);

        await createInterest({ name: "Cine" });

        expect(seen.headers?.get("Content-Type")).toBe("application/vnd.gotogether.interest.v1+json");
        expect(seen.body).toMatchObject({ name: "Cine" });
    });

    it("deleteCareer / deleteUniversity / deleteInterest resuelven con 204", async () => {
        await expect(deleteCareer(1)).resolves.toBeUndefined();
        await expect(deleteUniversity(1)).resolves.toBeUndefined();
        await expect(deleteInterest(1)).resolves.toBeUndefined();
    });
});
