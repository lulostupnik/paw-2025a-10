import { classNames } from "../utils/classNames";

const LogoMark = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} aria-hidden="true">
        <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
        />
    </svg>
);

interface LogoProps {
    className?: string;
    text?: string;
    showText?: boolean;
}

export default function Logo({ className, text = "GoTogether", showText = true }: LogoProps) {
    return (
        <span className={classNames("logo", className)}>
            <span className="logo__icon" aria-hidden="true">
                <LogoMark />
            </span>
            {showText && <span className="logo__text">{text}</span>}
        </span>
    );
}
