// navigationStack.ts

let STACK_KEY = 'navigation_stack';

function pushToNavigationStack(path) {
    const stack = getNavigationStack();
    stack.push(path);
    sessionStorage.setItem(STACK_KEY, JSON.stringify(stack));
}

function popFromNavigationStack() {
    const stack = getNavigationStack();
    const last = stack.pop();
    sessionStorage.setItem(STACK_KEY, JSON.stringify(stack));
    return last ?? null;
}

function peekNavigationStack() {
    const stack = getNavigationStack();
    return stack.length > 0 ? stack[stack.length - 1] : null;
}
function clearNavigationStack() {
    sessionStorage.removeItem(STACK_KEY);
}
function getNavigationStack() {
    const raw = sessionStorage.getItem(STACK_KEY);
    return raw ? JSON.parse(raw) : [];
}
