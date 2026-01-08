import { useLocation, useNavigate } from "react-router-dom";
import { loginFake } from "../../../shared/auth/auth";

function getNext(search: string) {
    const p = new URLSearchParams(search);
    return p.get("next") || "/explore";
}

export default function LoginPage() {
    const nav = useNavigate();
    const loc = useLocation();
    const next = getNext(loc.search);

    return (
        <div style={{ padding: 20 }}>
            <h1>Login</h1>
            <p>Temporary login buttons for dev.</p>

            <div style={{ display: "flex", gap: 10 }}>
                <button
                    onClick={() => {
                        loginFake({ admin: false });
                        nav(next, { replace: true });
                    }}
                >
                    Login as USER
                </button>

                <button
                    onClick={() => {
                        loginFake({ admin: true });
                        nav(next, { replace: true });
                    }}
                >
                    Login as ADMIN
                </button>
            </div>
        </div>
    );
}
