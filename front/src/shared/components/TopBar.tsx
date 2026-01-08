import { NavLink, useNavigate } from "react-router-dom";
import Button from "./ui/Button";
import { getUsername, isAdmin, isLoggedIn, logout } from "../auth/auth";
import { classNames } from "../utils/classNames";

const linkClassName = ({ isActive }: { isActive: boolean }) => classNames("top-bar__link", isActive && "is-active");

export default function TopBar() {
    const nav = useNavigate();
    const logged = isLoggedIn();

    return (
        <header className="top-bar">
            <NavLink to="/" className="top-bar__brand">
                GoTogether
            </NavLink>

            <nav className="top-bar__nav">
                {logged && (
                    <NavLink to="/explore" className={linkClassName}>
                        Explorar
                    </NavLink>
                )}
                <NavLink to="/journeys" className={linkClassName}>
                    Viajes
                </NavLink>
                <NavLink to="/events" className={linkClassName}>
                    Eventos
                </NavLink>
                {logged && isAdmin() && (
                    <NavLink to="/admin" className={linkClassName}>
                        Administrador
                    </NavLink>
                )}
            </nav>

            <div className="top-bar__actions">
                {!logged ? (
                    <>
                        <Button size="sm" variant="ghost" onClick={() => nav("/login")}>
                            Iniciar sesión
                        </Button>
                        <Button size="sm" onClick={() => nav("/register")}>
                            Registrarse
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
                            Logout
                        </Button>
                    </>
                )}
            </div>
        </header>
    );
}
