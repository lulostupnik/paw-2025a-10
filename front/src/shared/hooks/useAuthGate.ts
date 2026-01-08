import { useState } from "react";
import { isLoggedIn } from "../auth/auth";

export function useAuthGate() {
    const [open, setOpen] = useState(false);

    function runOrPrompt(fn: () => void) {
        if (isLoggedIn()) fn();
        else setOpen(true);
    }

    return { open, close: () => setOpen(false), runOrPrompt };
}
