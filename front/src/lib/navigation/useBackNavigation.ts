import { useCallback } from "react";
import { useLocation, useNavigate, type NavigateOptions } from "react-router-dom";
import { sanitizeInternalPath } from "@/lib/utils/internalPath";

export interface BackNavigation {
    from: string | null;
    goBack: (fallback: string, options?: NavigateOptions) => void;
}

export function useBackNavigation(): BackNavigation {
    const navigate = useNavigate();
    const location = useLocation();

    const current = `${location.pathname}${location.search}`;
    const raw = sanitizeInternalPath((location.state as { from?: string } | null)?.from);
    const from = raw && raw !== current ? raw : null;

    const goBack = useCallback(
        (fallback: string, options?: NavigateOptions) => {
            navigate(from ?? fallback, options);
        },
        [navigate, from],
    );

    return { from, goBack };
}
