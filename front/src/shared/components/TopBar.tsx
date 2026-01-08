import { NavLink, useNavigate } from "react-router-dom";
import { getUsername, isAdmin, isLoggedIn, logout } from "../auth/auth";

const linkStyle = ({ isActive }: { isActive: boolean }) => ({
    padding: "8px 12px",
    borderRadius: 10,
    textDecoration: "none",
    color: "inherit",
    background: isActive ? "#eef2ff" : "transparent",
});

export default function TopBar() {
    const nav = useNavigate();
    const logged = isLoggedIn();

    return (
        <header
            style={{
                height: 72,
                borderBottom: "1px solid #eee",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                padding: "0 20px",
                gap: 16,
            }}
        >
            {/* Left: Brand */}
            <NavLink to="/" style={{ textDecoration: "none", color: "inherit", fontWeight: 800, fontSize: 18 }}>
                GoTogether
            </NavLink>

            {/* Center: Nav */}
            <nav style={{ display: "flex", gap: 10, alignItems: "center" }}>
                {logged && (
                    <NavLink to="/explore" style={linkStyle}>
                        Explorar
                    </NavLink>
                )}
                <NavLink to="/journeys" style={linkStyle}>
                    Viajes
                </NavLink>
                <NavLink to="/events" style={linkStyle}>
                    Eventos
                </NavLink>
                {logged && isAdmin() && (
                    <NavLink to="/admin" style={linkStyle}>
                        Administrador
                    </NavLink>
                )}
            </nav>

            {/* Right: Auth buttons or user menu */}
            <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
                {!logged ? (
                    <>
                        <button
                            onClick={() => nav("/login")}
                            style={{ padding: "10px 14px", borderRadius: 10, border: "1px solid #4f46e5", background: "#4f46e5", color: "#fff" }}
                        >
                            Iniciar sesión
                        </button>
                        <button
                            onClick={() => nav("/register")}
                            style={{ padding: "10px 14px", borderRadius: 10, border: "1px solid #4f46e5", background: "transparent", color: "#4f46e5" }}
                        >
                            Registrarse
                        </button>
                    </>
                ) : (
                    <>
                        <span style={{ fontWeight: 600 }}>{getUsername()}</span>
                        <button
                            onClick={() => {
                                logout();
                                nav("/", { replace: true });
                            }}
                            style={{ padding: "10px 14px", borderRadius: 10, border: "1px solid #ddd", background: "#fff" }}
                        >
                            Logout
                        </button>
                    </>
                )}
            </div>
        </header>
    );
}
