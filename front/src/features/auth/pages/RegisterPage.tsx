import { useLocation, useNavigate } from "react-router-dom";
import { loginFake } from "../../../shared/auth/auth";

function getNext(search: string) {
    const p = new URLSearchParams(search);
    return p.get("next") || "/explore";
}

export default function RegisterPage() {
    const nav = useNavigate();
    const loc = useLocation();
    const next = getNext(loc.search);

    return (
        <div style={{ padding: 20 }}>
            <h1>Register</h1>
            <p>Temporary “register” button for dev.</p>

            <button
                onClick={() => {
                    loginFake({ admin: false });
                    nav(next, { replace: true });
                }}
            >
                Create account (fake) and continue
            </button>
        </div>
    );
}
