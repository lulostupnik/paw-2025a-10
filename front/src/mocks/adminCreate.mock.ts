import type { CatalogOption } from "@/lib/api/catalog";

export type AdminCreateScenario = "normal" | "empty";

const CITY_OPTIONS: CatalogOption[] = [
    { id: 1, name: "Buenos Aires" },
    { id: 2, name: "Cordoba" },
    { id: 3, name: "Rosario" },
    { id: 4, name: "Mendoza" },
    { id: 5, name: "Mar del Plata" },
];

const COUNTRY_OPTIONS: CatalogOption[] = [
    { id: 1, name: "Argentina" },
    { id: 2, name: "Uruguay" },
    { id: 3, name: "Chile" },
    { id: 4, name: "Paraguay" },
    { id: 5, name: "Brasil" },
];

export const getCityOptionsMock = (scenario: AdminCreateScenario): CatalogOption[] =>
    scenario === "empty" ? [] : CITY_OPTIONS;

export const getCountryOptionsMock = (scenario: AdminCreateScenario): CatalogOption[] =>
    scenario === "empty" ? [] : COUNTRY_OPTIONS;
