import { NavLink } from "react-router-dom";

const linkStyle = ({ isActive }: { isActive: boolean }) => ({
    display: "block",
    padding: "10px 12px",
    textDecoration: "none",
    color: "inherit",
    background: isActive ? "#eee" : "transparent",
    borderRadius: 8,
});

export default function Sidebar() {
    return (
        <aside style={{ width: 220, padding: 12, borderRight: "1px solid #ddd" }}>
            <div style={{ display: "grid", gap: 6 }}>
                <NavLink to="/" end style={linkStyle}>
                    Home
                </NavLink>
                <NavLink to="/journeys" style={linkStyle}>
                    Journeys
                </NavLink>
                <NavLink to="/events" style={linkStyle}>
                    Events
                </NavLink>
                <NavLink to="/profile" style={linkStyle}>
                    Profile
                </NavLink>
                <NavLink to="/admin" style={linkStyle}>
                    Admin
                </NavLink>
            </div>
        </aside>
    );
}
