interface ListingSkeletonGridProps {
    count?: number;
    variant?: "default" | "compact";
}

export default function ListingSkeletonGrid({ count = 6, variant = "default" }: ListingSkeletonGridProps) {
    return (
        <div className={`skeleton-grid${variant === "compact" ? " skeleton-grid--compact" : ""}`}>
            {Array.from({ length: count }).map((_, index) => (
                <div className="skeleton-card" key={`skeleton-${index}`}>
                    <div className="skeleton-media skeleton-block" />
                    <div className="skeleton-body">
                        <div className="skeleton-line skeleton-block" />
                        <div className="skeleton-line skeleton-block skeleton-line--short" />
                        <div className="skeleton-line skeleton-block skeleton-line--wide" />
                        <div className="skeleton-meta">
                            <span className="skeleton-pill skeleton-block" />
                            <span className="skeleton-pill skeleton-block" />
                            <span className="skeleton-pill skeleton-block" />
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
}
