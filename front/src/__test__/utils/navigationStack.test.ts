import { beforeEach, describe, expect, it } from "vitest";
import {
    clearNavigationStack,
    peekNavigationStack,
    popFromNavigationStack,
    pushToNavigationStack,
} from "@/lib/utils/navigationStack";

const STORAGE_KEY = "navigation_stack";
const raw = () => sessionStorage.getItem(STORAGE_KEY);

describe("navigationStack", () => {
    beforeEach(() => {
        sessionStorage.clear();
    });

    it("apila y desapila en orden LIFO", () => {
        pushToNavigationStack("/events");
        pushToNavigationStack("/events/1");

        expect(popFromNavigationStack()).toBe("/events/1");
        expect(popFromNavigationStack()).toBe("/events");
        expect(popFromNavigationStack()).toBeNull();
    });

    it("peek devuelve el tope sin sacarlo", () => {
        pushToNavigationStack("/journeys");

        expect(peekNavigationStack()).toBe("/journeys");
        expect(peekNavigationStack()).toBe("/journeys");
        expect(popFromNavigationStack()).toBe("/journeys");
    });

    it("peek y pop sobre una pila vacía devuelven null", () => {
        expect(peekNavigationStack()).toBeNull();
        expect(popFromNavigationStack()).toBeNull();
    });

    it("no llega a guardar los paths que no son internos", () => {
        pushToNavigationStack("https://evil.com/phishing");
        pushToNavigationStack("//evil.com");
        pushToNavigationStack("javascript:alert(1)");

        expect(raw()).toBeNull();
        expect(peekNavigationStack()).toBeNull();
    });

    it("no deja que un path externo se cuele entre dos internos", () => {
        pushToNavigationStack("/events");
        pushToNavigationStack("https://evil.com");

        expect(raw()).toBe(JSON.stringify(["/events"]));
        expect(peekNavigationStack()).toBe("/events");
    });

    it("descarta las entradas no internas que ya estuvieran guardadas", () => {
        // sessionStorage lo escribe el cliente: puede venir manipulado
        sessionStorage.setItem(STORAGE_KEY, JSON.stringify(["/events", "https://evil.com", "/events/1"]));

        expect(popFromNavigationStack()).toBe("/events/1");
        expect(popFromNavigationStack()).toBe("/events");
        expect(popFromNavigationStack()).toBeNull();
    });

    it("tolera un sessionStorage corrupto sin explotar", () => {
        sessionStorage.setItem(STORAGE_KEY, "{no es json");
        expect(peekNavigationStack()).toBeNull();

        sessionStorage.setItem(STORAGE_KEY, JSON.stringify({ noEsUnArray: true }));
        expect(peekNavigationStack()).toBeNull();
    });

    it("ignora las entradas que no son strings", () => {
        sessionStorage.setItem(STORAGE_KEY, JSON.stringify(["/events", 42, null, "/journeys"]));

        expect(popFromNavigationStack()).toBe("/journeys");
        expect(popFromNavigationStack()).toBe("/events");
    });

    it("clear vacía la pila entera", () => {
        pushToNavigationStack("/events");
        clearNavigationStack();

        expect(raw()).toBeNull();
        expect(peekNavigationStack()).toBeNull();
    });
});
