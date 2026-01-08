import { Link } from "react-router-dom";

export default function Navbar() {
    return (
        <header
            style={{
                height: 56,
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                padding: "0 16px",
                borderBottom: "1px solid #ddd",
            }}
        >
            <Link to="/" style={{ textDecoration: "none", color: "inherit", fontWeight: 700 }}>
                PAW
            </Link>

            <nav style={{ display: "flex", gap: 12, alignItems: "center" }}>
                <Link to="/profile">Profile</Link>
                <Link to="/login">Logout</Link>
            </nav>
        </header>
    );
}
