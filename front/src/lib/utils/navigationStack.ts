const STORAGE_KEY = "navigation_stack";

const getNavigationStack = (): string[] => {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) {
        return [];
    }
    try {
        const parsed = JSON.parse(raw);
        return Array.isArray(parsed) ? parsed.filter((entry) => typeof entry === "string") : [];
    } catch {
        return [];
    }
};

const setNavigationStack = (stack: string[]) => {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(stack));
};

export const pushToNavigationStack = (path: string) => {
    const stack = getNavigationStack();
    stack.push(path);
    setNavigationStack(stack);
};

export const popFromNavigationStack = (): string | null => {
    const stack = getNavigationStack();
    const last = stack.pop();
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
