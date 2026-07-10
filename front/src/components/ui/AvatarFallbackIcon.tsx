import { classNames } from "@/lib/utils/classNames";

interface AvatarFallbackIconProps {
    size?: number;
    className?: string;
}

export default function AvatarFallbackIcon({ size = 24, className }: AvatarFallbackIconProps) {
    return (
        <svg
            aria-hidden="true"
            focusable="false"
            className={classNames("avatar-fallback-icon", className)}
            width={size}
            height={size}
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.8"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <path d="M20 21a8 8 0 1 0-16 0" />
            <circle cx="12" cy="8" r="4" />
        </svg>
    );
}
