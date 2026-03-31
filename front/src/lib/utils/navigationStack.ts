import { sanitizeInternalPath } from "@/lib/utils/internalPath";

const STORAGE_KEY = "navigation_stack";

const getNavigationStack = (): string[] => {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) {
        return [];
    }
    try {
        const parsed = JSON.parse(raw);
        return Array.isArray(parsed)
            ? parsed
                  .map((entry) => sanitizeInternalPath(typeof entry === "string" ? entry : null))
                  .filter((entry): entry is string => Boolean(entry))
            : [];
    } catch {
        return [];
    }
};

const setNavigationStack = (stack: string[]) => {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(stack));
};

export const pushToNavigationStack = (path: string) => {
    const safePath = sanitizeInternalPath(path);
    if (!safePath) {
        return;
    }
    const stack = getNavigationStack();
    stack.push(safePath);
    setNavigationStack(stack);
};

export const popFromNavigationStack = (): string | null => {
    const stack = getNavigationStack();
    let last = stack.pop() ?? null;
    while (last && !sanitizeInternalPath(last)) {
        last = stack.pop() ?? null;
    }
    setNavigationStack(stack);
    return last ?? null;
};

export const peekNavigationStack = (): string | null => {
    const stack = getNavigationStack();
    return stack.length > 0 ? stack[stack.length - 1] : null;
};

export const clearNavigationStack = () => {
    sessionStorage.removeItem(STORAGE_KEY);
};
