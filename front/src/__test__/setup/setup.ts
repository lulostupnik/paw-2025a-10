import "@testing-library/jest-dom";
import { cleanup } from "@testing-library/react";
import { beforeAll, afterEach, afterAll, vi } from "vitest";
import { setupServer } from "msw/node";
import { handlers } from "./index";

export const server = setupServer(...handlers);

beforeAll(() => server.listen({ onUnhandledRequest: "error" }));

afterEach(() => {
    server.resetHandlers();
    cleanup();
});

afterAll(() => server.close());

window.scrollTo = vi.fn() as unknown as typeof window.scrollTo;
