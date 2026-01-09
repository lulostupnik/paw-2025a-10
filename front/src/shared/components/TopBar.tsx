import { NavLink, useNavigate } from "react-router-dom";
import Button from "./ui/Button";
import { getUsername, isAdmin, isLoggedIn, logout } from "../auth/auth";
import { classNames } from "../utils/classNames";
import { useI18n } from "../i18n";
import Logo from "./Logo";

const linkClassName = ({ isActive }: { isActive: boolean }) => classNames("top-bar__link", isActive && "is-active");

export default function TopBar() {
    const nav = useNavigate();
    const logged = isLoggedIn();
    const { t } = useI18n();

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
                    <>
                        <span className="top-bar__username">{getUsername()}</span>
                        <Button
                            size="sm"
                            variant="secondary"
                            onClick={() => {
                                logout();
                                nav("/", { replace: true });
                            }}
                        >
                            {t("nav.logout")}
                        </Button>
                    </>
                )}
            </div>
        </header>
    );
}
