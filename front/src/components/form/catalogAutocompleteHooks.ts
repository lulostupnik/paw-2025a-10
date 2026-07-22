import { useEffect, useRef, useState } from "react";
import type { CatalogOption, CatalogSearchFn } from "@/lib/api/catalog";

export function useAsyncCatalogOptions(fetcher: CatalogSearchFn, enabled: boolean, query: string, debounceMs = 0) {
    const [options, setOptions] = useState<CatalogOption[]>([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!enabled) {
            return;
        }
        const controller = new AbortController();
        const handle = window.setTimeout(() => {
            setLoading(true);
            fetcher(query, controller.signal)
                .then((result) => setOptions(result))
                .catch(() => {
                    if (!controller.signal.aborted) {
                        setOptions([]);
                    }
                })
                .finally(() => {
                    if (!controller.signal.aborted) {
                        setLoading(false);
                    }
                });
        }, debounceMs);

        return () => {
            controller.abort();
            window.clearTimeout(handle);
        };
    }, [debounceMs, enabled, fetcher, query]);

    return { options, loading };
}

export function useDropdownState() {
    const [open, setOpen] = useState(false);
    const ref = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (!open) {
            return;
        }
        const handleClick = (event: MouseEvent) => {
            if (ref.current && !ref.current.contains(event.target as Node)) {
                setOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClick);
        return () => document.removeEventListener("mousedown", handleClick);
    }, [open]);

    return { open, setOpen, ref } as const;
}
