const INVALID_FIELD_SELECTORS = [
    "[aria-invalid='true']",
    ".input-control--error",
    ".form-input.error",
    ".form-textarea.error",
    ".upload-dropzone.has-error",
].join(", ");

export function focusFirstInvalidField(root: ParentNode | null) {
    if (!root) {
        return;
    }

    window.requestAnimationFrame(() => {
        const firstInvalid = root.querySelector<HTMLElement>(INVALID_FIELD_SELECTORS);
        if (!firstInvalid) {
            return;
        }

        try {
            firstInvalid.focus({ preventScroll: true });
        } catch {
            firstInvalid.focus();
        }
        if (typeof firstInvalid.scrollIntoView === "function") {
            firstInvalid.scrollIntoView({ block: "center", behavior: "smooth" });
        }
    });
}
