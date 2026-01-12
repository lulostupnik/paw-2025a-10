import { useEffect, useLayoutEffect, useRef, useState } from "react";

interface SortOption {
    id: string;
    label: string;
}

interface ListingSortDropdownProps {
    title: string;
    open: boolean;
    options: SortOption[];
    selectedId?: string | null;
    anchorRef?: { current: HTMLElement | null } | null;
    onSelect: (id: string) => void;
    onClose: () => void;
}

export default function ListingSortDropdown({
    title,
    open,
    options,
    selectedId,
    anchorRef,
    onSelect,
    onClose,
}: ListingSortDropdownProps) {
    const panelRef = useRef<HTMLDivElement>(null);
    const [position, setPosition] = useState({ top: 0, left: 0 });

    useLayoutEffect(() => {
        if (!open) {
            return;
        }
        const updatePosition = () => {
            const anchor = anchorRef?.current;
            const panel = panelRef.current;
            const panelWidth = panel?.offsetWidth ?? 260;
            const panelHeight = panel?.offsetHeight ?? 260;
            const spacing = 6;
            const maxLeft = Math.max(12, window.innerWidth - panelWidth - 12);
            const maxTop = Math.max(12, window.innerHeight - panelHeight - 12);

            if (anchor) {
                const rect = anchor.getBoundingClientRect();
                const desiredLeft = rect.left + rect.width / 2 - panelWidth / 2;
                const safeLeft = Math.min(Math.max(12, desiredLeft), maxLeft);
                const desiredTop = rect.bottom + spacing;
                const safeTop = Math.min(Math.max(12, desiredTop), maxTop);
                setPosition({ top: safeTop, left: safeLeft });
            } else {
                setPosition({ top: 120, left: Math.max(12, (window.innerWidth - panelWidth) / 2) });
            }
        };

        updatePosition();
        window.addEventListener("resize", updatePosition);
        window.addEventListener("scroll", updatePosition, true);
        return () => {
            window.removeEventListener("resize", updatePosition);
            window.removeEventListener("scroll", updatePosition, true);
        };
    }, [anchorRef, open]);

    useEffect(() => {
        if (!open) {
            return;
        }
        const handlePointerDown = (event: MouseEvent) => {
            const target = event.target as Node;
            if (panelRef.current?.contains(target)) {
                return;
            }
            if (anchorRef?.current && anchorRef.current.contains(target)) {
                return;
            }
            onClose();
        };
        const handleKey = (event: KeyboardEvent) => {
            if (event.key === "Escape") {
                onClose();
            }
        };
        document.addEventListener("mousedown", handlePointerDown);
        document.addEventListener("keydown", handleKey);
        return () => {
            document.removeEventListener("mousedown", handlePointerDown);
            document.removeEventListener("keydown", handleKey);
        };
    }, [anchorRef, onClose, open]);

    if (!open) {
        return null;
    }

    return (
        <div ref={panelRef} className="sort-dropdown" role="menu" style={{ top: `${position.top}px`, left: `${position.left}px` }}>
            <p className="sort-dropdown__title">{title}</p>
            <ul className="sort-dropdown__list">
                {options.map((option) => (
                    <li key={option.id}>
                        <button
                            type="button"
                            className={option.id === selectedId ? "sort-dropdown__option is-active" : "sort-dropdown__option"}
                            onClick={() => onSelect(option.id)}
                            role="menuitemradio"
                            aria-checked={option.id === selectedId}
                        >
                            <span>{option.label}</span>
                            {option.id === selectedId && <span aria-hidden="true">✓</span>}
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    );
}
