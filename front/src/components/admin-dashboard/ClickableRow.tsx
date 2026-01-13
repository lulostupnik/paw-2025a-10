import type { KeyboardEvent, ReactNode } from "react";
import { classNames } from "@/lib/utils/classNames";

interface ClickableRowProps {
    onClick?: () => void;
    children: ReactNode;
    className?: string;
}

export default function ClickableRow({ onClick, children, className }: ClickableRowProps) {
    const handleKeyDown = (event: KeyboardEvent<HTMLTableRowElement>) => {
        if (!onClick) {
            return;
        }

        if (event.key === "Enter" || event.key === " ") {
            event.preventDefault();
            onClick();
        }
    };

    return (
        <tr
            className={classNames("clickable-row", className)}
            onClick={onClick}
            onKeyDown={handleKeyDown}
            tabIndex={onClick ? 0 : undefined}
            role={onClick ? "link" : undefined}
        >
            {children}
        </tr>
    );
}
