import type { ProfileScenario } from "@/mocks/profiles.mock";

export const getProfileScenarioFromSearch = (): ProfileScenario => {
    if (typeof window === "undefined") {
        return "normal";
    }
    const params = new URLSearchParams(window.location.search);
    const raw = params.get("profileScenario") ?? params.get("scenario");
    if (raw === "empty" || raw === "error" || raw === "loading") {
        return raw;
    }
    return "normal";
};
