export const INTERNAL_PATH_FALLBACK = "/explore";

// El rango de control es intencional: un path que contenga esos caracteres se
// rechaza, porque intercalarlos permite disfrazar un esquema no interno.
// eslint-disable-next-line no-control-regex
const CONTROL_OR_WHITESPACE = /[\u0000-\u001f\u007f\s]/;

export function sanitizeInternalPath(raw: string | null | undefined, fallback: string | null = null): string | null {
    if (typeof raw !== "string") {
        return fallback;
    }

    const trimmed = raw.trim();
    if (!trimmed) {
        return fallback;
    }

    if (!trimmed.startsWith("/") || trimmed.startsWith("//") || trimmed.includes("\\") || CONTROL_OR_WHITESPACE.test(trimmed)) {
        return fallback;
    }

    try {
        const base = typeof window !== "undefined" ? window.location.origin : "http://localhost";
        const parsed = new URL(trimmed, base);
        if (parsed.origin !== base || parsed.protocol !== new URL(base).protocol) {
            return fallback;
        }
        if (!parsed.pathname.startsWith("/")) {
            return fallback;
        }
        return `${parsed.pathname}${parsed.search}${parsed.hash}`;
    } catch {
        return fallback;
    }
}

