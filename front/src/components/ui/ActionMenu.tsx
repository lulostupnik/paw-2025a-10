import { useEffect, useLayoutEffect, useRef, useState } from "react";
import { classNames } from "@/lib/utils/classNames";

export interface ActionMenuItem {
    id: string;
    label: string;
    onSelect: () => void;
    destructive?: boolean;
    disabled?: boolean;
    variant?: "default" | "info" | "success" | "warning" | "danger";
}

interface ActionMenuProps {
    open: boolean;
    anchorRef: React.RefObject<HTMLElement>;
    onClose: () => void;
    title?: string;
    items: ActionMenuItem[];
}

export default function ActionMenu({ open, anchorRef, onClose, title, items }: ActionMenuProps) {
    const panelRef = useRef<HTMLDivElement>(null);
    const [position, setPosition] = useState({ top: 0, left: 0 });

    useLayoutEffect(() => {
        if (!open) {
            return;
        }

        const updatePosition = () => {
            const anchor = anchorRef.current;
            const panel = panelRef.current;
            const panelWidth = panel?.offsetWidth ?? 240;
            const panelHeight = panel?.offsetHeight ?? 240;
            const spacing = 8;

            if (anchor) {
                const rect = anchor.getBoundingClientRect();
                const desiredLeft = rect.left + rect.width / 2 - panelWidth / 2;
                const maxLeft = Math.max(12, window.innerWidth - panelWidth - 12);
                const safeLeft = Math.min(Math.max(12, desiredLeft), maxLeft);
                const desiredTop = rect.bottom + spacing;
                const maxTop = Math.max(12, window.innerHeight - panelHeight - 12);
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
            if (anchorRef.current && anchorRef.current.contains(target)) {
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

    if (!open || items.length === 0) {
        return null;
    }

    return (
        <div ref={panelRef} className="action-menu" role="menu" style={{ top: `${position.top}px`, left: `${position.left}px` }}>
            {title && <p className="action-menu__title">{title}</p>}
            <ul className="action-menu__list">
                {items.map((item) => {
                    const variantClass = item.variant ?? (item.destructive ? "danger" : undefined);
                    const itemClassName = classNames(
                        "action-menu__item",
                        variantClass && `is-${variantClass}`,
                        item.disabled && "is-disabled"
                    );
                    return (
                        <li key={item.id}>
                            <button
                                type="button"
                                className={itemClassName}
                                onClick={() => {
                                    if (item.disabled) {
                                        return;
                                    }
                                    item.onSelect();
                                }}
                                role="menuitem"
                                disabled={item.disabled}
                            >
                                {item.label}
                            </button>
                        </li>
                    );
                })}
            </ul>
        </div>
    );
}
