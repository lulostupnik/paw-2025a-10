import { isAxiosError } from "axios";

const BLOCKED_MARKERS = ["blocked", "bloquead", "error.userblocked"];
const ACCESS_DENIED_MARKERS = ["access denied", "permission to access", "no tienes permiso", "error.accessdenied"];

function getErrorMessage(error: unknown): string {
    if (!isAxiosError(error)) {
        return "";
    }
    return String(error.response?.data?.message ?? "").toLowerCase();
}

export function isBlockedAuthError(error: unknown): boolean {
    const message = getErrorMessage(error);
    return BLOCKED_MARKERS.some((marker) => message.includes(marker));
}

export function isAccessDeniedAuthError(error: unknown): boolean {
    const message = getErrorMessage(error);
    return ACCESS_DENIED_MARKERS.some((marker) => message.includes(marker));
}
