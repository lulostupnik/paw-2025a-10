import { Navigate, useLocation, useParams } from "react-router-dom";

export default function ProfileRedirect() {
    const { profileId = "me" } = useParams();
    const location = useLocation();
    return <Navigate to={`/profiles/${profileId}/info`} replace state={location.state} />;
}
