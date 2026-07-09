import { useCallback, useEffect, useRef, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import Button from "./ui/Button";
import { getUserId, getUsername, isAdmin, isLoggedIn, logout } from "@/lib/auth/auth";
import { classNames } from "@/lib/utils/classNames";
import { useI18n } from "@/lib/i18n";
import Logo from "./Logo";
import { apiBaseUrl } from "@/lib/api/client";

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
    const userId = logged ? getUserId() : null;
    const { t, locale, setLocale } = useI18n();
    const [navOpen, setNavOpen] = useState(false);
    const [menuOpen, setMenuOpen] = useState(false);
    const [avatarError, setAvatarError] = useState(false);
    const profileRef = useRef<HTMLDivElement>(null);
    const headerRef = useRef<HTMLElement | null>(null);
    const profilePictureSrc = logged && userId ? `${apiBaseUrl}/users/${userId}/profilePicture` : null;
    const showProfilePicture = Boolean(profilePictureSrc && !avatarError);

    const updateTopBarOffset = useCallback(() => {
        if (typeof window === "undefined" || !headerRef.current) {
            return;
        }
        document.documentElement.style.setProperty("--top-bar-offset", `${headerRef.current.offsetHeight}px`);
    }, []);

    useEffect(() => {
        if (!logged && menuOpen) {
            setMenuOpen(false);
        }
        if (!logged && navOpen) {
            setNavOpen(false);
        }
    }, [logged, menuOpen, navOpen]);

    useEffect(() => {
        if (typeof window === "undefined") {
            return undefined;
        }

        updateTopBarOffset();
        window.addEventListener("resize", updateTopBarOffset);
        return () => {
            window.removeEventListener("resize", updateTopBarOffset);
        };
    }, [updateTopBarOffset]);

    useEffect(() => {
        updateTopBarOffset();
    }, [updateTopBarOffset, logged, username]);

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
        <header className="top-bar" ref={headerRef}>
            <NavLink to="/" className="top-bar__brand" aria-label={t("app.name")}>
                <Logo text={t("app.name")} />
            </NavLink>

            <button
                type="button"
                className="top-bar__menu-toggle"
                aria-label={t("nav.toggle", { defaultValue: "Toggle navigation" })}
                onClick={() => setNavOpen((prev) => !prev)}
            >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
            </button>

            <nav className={classNames("top-bar__nav", navOpen && "is-open")}>
                {logged && (
                    <NavLink to="/explore" className={linkClassName} onClick={() => setNavOpen(false)}>
                        {t("nav.explore")}
                    </NavLink>
                )}
                <NavLink to="/journeys" className={linkClassName} onClick={() => setNavOpen(false)}>
                    {t("nav.journeys")}
                </NavLink>
                <NavLink to="/events" className={linkClassName} onClick={() => setNavOpen(false)}>
                    {t("nav.events")}
                </NavLink>
                {logged && isAdmin() && (
                    <NavLink to="/admin" className={linkClassName} onClick={() => setNavOpen(false)}>
                        {t("admin.manage.reports")}
                    </NavLink>
                )}
            </nav>

            <div className="top-bar__actions">
                <Button
                    size="sm"
                    variant="ghost"
                    aria-label={t("nav.language", { defaultValue: "Change language" })}
                    onClick={() => setLocale(locale === "en" ? "es" : "en")}
                >
                    {locale === "en" ? "ES" : "EN"}
                </Button>
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
                                {showProfilePicture ? (
                                    <img
                                        src={profilePictureSrc ?? ""}
                                        alt=""
                                        className="top-bar__avatar-image"
                                        onError={() => setAvatarError(true)}
                                    />
                                ) : (
                                    getInitials(username)
                                )}
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
                                    nav("/profiles/me/info");
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
            {navOpen && (
                <button
                    type="button"
                    className="top-bar__overlay"
                    aria-label={t("nav.close", { defaultValue: "Close navigation" })}
                    onClick={() => setNavOpen(false)}
                />
            )}
        </header>
    );
}
