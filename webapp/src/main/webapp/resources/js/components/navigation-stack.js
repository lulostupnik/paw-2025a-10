// navigationStack.ts


function pushToNavigationStack(path) {
    const stack = getNavigationStack();
    stack.push(path);
    sessionStorage.setItem('navigation_stack', JSON.stringify(stack));
}

function popFromNavigationStack() {
    const stack = getNavigationStack();
    const last = stack.pop();
    sessionStorage.setItem('navigation_stack', JSON.stringify(stack));
    return last ?? null;
}

function peekNavigationStack() {
    const stack = getNavigationStack();
    return stack.length > 0 ? stack[stack.length - 1] : null;
}
function clearNavigationStack() {
    sessionStorage.removeItem('navigation_stack');
}
function getNavigationStack() {
    const raw = sessionStorage.getItem('navigation_stack');
    return raw ? JSON.parse(raw) : [];
}
