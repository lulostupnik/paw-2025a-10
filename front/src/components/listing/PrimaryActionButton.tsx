import Button from "@/components/ui/Button";

interface PrimaryActionButtonProps {
    label: string;
    onClick: () => void;
}

export default function PrimaryActionButton({ label, onClick }: PrimaryActionButtonProps) {
    return (
        <Button type="button" variant="primary" size="sm" className="primary-action-btn" onClick={onClick}>
            <span aria-hidden="true">+</span>
            {label}
        </Button>
    );
}
