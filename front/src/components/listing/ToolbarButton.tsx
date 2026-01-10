import { forwardRef } from "react";
import type { ReactNode } from "react";
import Button from "@/components/ui/Button";

interface ToolbarButtonProps {
    label: string;
    icon?: ReactNode;
    onClick?: () => void;
}

const ToolbarButton = forwardRef<HTMLButtonElement, ToolbarButtonProps>(function ToolbarButton(
    { label, icon, onClick }: ToolbarButtonProps,
    ref
) {
    return (
        <Button ref={ref} type="button" variant="ghost" size="sm" className="toolbar-button" onClick={onClick}>
            {icon && <span className="toolbar-button__icon" aria-hidden="true">{icon}</span>}
            {label}
        </Button>
    );
});

export default ToolbarButton;
