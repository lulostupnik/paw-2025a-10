import type { ReactNode } from "react";

interface CardMetaRowProps {
    icon: ReactNode;
    label: string;
    value: string;
}

export default function CardMetaRow({ icon, label, value }: CardMetaRowProps) {
    return (
        <div className="card-meta-row">
            <span className="card-meta-icon" aria-hidden="true">
                {icon}
            </span>
            <div>
                <p className="card-meta-label">{label}</p>
                <p className="card-meta-value">{value}</p>
            </div>
        </div>
    );
}
