import type { ReactNode } from "react";
import Button from "@/components/ui/Button";

interface ToolbarButtonProps {
    label: string;
    icon?: ReactNode;
    onClick?: () => void;
}

export default function ToolbarButton({ label, icon, onClick }: ToolbarButtonProps) {
    return (
        <Button type="button" variant="ghost" size="sm" className="toolbar-button" onClick={onClick}>
            {icon && <span className="toolbar-button__icon" aria-hidden="true">{icon}</span>}
            {label}
        </Button>
    );
}
