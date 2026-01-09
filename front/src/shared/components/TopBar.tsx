import { useEffect, useRef, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import Button from "./ui/Button";
import { getUsername, isAdmin, isLoggedIn, logout } from "../auth/auth";
import { classNames } from "../utils/classNames";
import { useI18n } from "../i18n";
import Logo from "./Logo";

const linkClassName = ({ isActive }: { isActive: boolean }) => classNames("top-bar__link", isActive && "is-active");

const getInitials = (value: string) =>
    value
        .split(" ")
        .filter(Boolean)
        .slice(0, 2)
        .map((chunk) => chunk[0])
        .join("")
        .toUpperCase();

export default function TopBar() {
    const nav = useNavigate();
    const logged = isLoggedIn();
    const username = getUsername();
    const { t } = useI18n();
    const [menuOpen, setMenuOpen] = useState(false);
    const profileRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (!logged && menuOpen) {
            setMenuOpen(false);
        }
    }, [logged, menuOpen]);

    useEffect(() => {
        if (!menuOpen) {
            return undefined;
        }

        const handleClickOutside = (event: MouseEvent) => {
            if (profileRef.current && !profileRef.current.contains(event.target as Node)) {
                setMenuOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, [menuOpen]);

    return (
        <header className="top-bar">
            <NavLink to="/" className="top-bar__brand" aria-label={t("app.name")}>
                <Logo text={t("app.name")} />
            </NavLink>

            <nav className="top-bar__nav">
                {logged && (
                    <NavLink to="/explore" className={linkClassName}>
                        {t("nav.explore")}
                    </NavLink>
                )}
                <NavLink to="/journeys" className={linkClassName}>
                    {t("nav.journeys")}
                </NavLink>
                <NavLink to="/events" className={linkClassName}>
                    {t("nav.events")}
                </NavLink>
                {logged && isAdmin() && (
                    <NavLink to="/admin" className={linkClassName}>
                        {t("admin.manage.reports")}
                    </NavLink>
                )}
            </nav>

            <div className="top-bar__actions">
                {!logged ? (
                    <>
                        <Button size="sm" variant="ghost" onClick={() => nav("/login")}>
                            {t("nav.login")}
                        </Button>
                        <Button size="sm" onClick={() => nav("/register")}>
                            {t("nav.register")}
                        </Button>
                    </>
                ) : (
                    <div className="top-bar__profile" ref={profileRef}>
                        <button
                            type="button"
                            className={classNames("top-bar__profile-trigger", menuOpen && "is-open")}
                            onClick={() => setMenuOpen((prev) => !prev)}
                            aria-haspopup="menu"
                            aria-expanded={menuOpen}
                        >
                            <span className="top-bar__avatar" aria-hidden="true">
                                {getInitials(username)}
                            </span>
                            <span className="top-bar__profile-name">{username}</span>
                            <span className="top-bar__chevron" aria-hidden="true">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                                </svg>
                            </span>
                        </button>

                        <div className={classNames("top-bar__profile-menu", menuOpen && "is-open")} role="menu">
                            <button
                                type="button"
                                className="top-bar__profile-menu-item"
                                onClick={() => {
                                    setMenuOpen(false);
                                    nav("/profile");
                                }}
                            >
                                {t("nav.profile")}
                            </button>
                            <button
                                type="button"
                                className="top-bar__profile-menu-item top-bar__profile-menu-item--danger"
                                onClick={() => {
                                    setMenuOpen(false);
                                    logout();
                                    nav("/", { replace: true });
                                }}
                            >
                                {t("nav.logout")}
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </header>
    );
}
