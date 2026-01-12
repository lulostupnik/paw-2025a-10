import type { ReactNode } from "react";
import { classNames } from "@/lib/utils/classNames";

interface BaseCardProps {
    imageUrl?: string;
    badge?: string;
    className?: string;
    children: ReactNode;
    footer?: ReactNode;
}

export default function BaseCard({ imageUrl, badge, className, children, footer }: BaseCardProps) {
    return (
        <article className={classNames("card listing-card", className)}>
            <div
                className="listing-card__image"
                style={{
                    backgroundImage: imageUrl ? `url(${imageUrl})` : undefined,
                }}
                aria-hidden="true"
            >
                {!imageUrl && <div className="listing-card__image-fallback" />}
                {badge && <span className="listing-card__badge">{badge}</span>}
            </div>
            <div className="listing-card__body">{children}</div>
            {footer && <div className="listing-card__footer">{footer}</div>}
        </article>
    );
}
