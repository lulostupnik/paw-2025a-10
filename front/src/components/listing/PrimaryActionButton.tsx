import type { ReactNode } from "react";
import Button from "@/components/ui/Button";

interface PrimaryActionButtonProps {
    label: string;
    onClick: () => void;
    icon?: ReactNode;
}

export default function PrimaryActionButton({ label, onClick, icon }: PrimaryActionButtonProps) {
    return (
        <Button type="button" variant="primary" size="sm" className="primary-action-btn" onClick={onClick}>
            <span aria-hidden="true">{icon ?? "+"}</span>
            {label}
        </Button>
    );
}
