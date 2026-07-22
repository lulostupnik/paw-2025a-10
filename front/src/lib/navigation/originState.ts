import type { Location } from "react-router-dom";

export interface OriginState {
    from: string;
}

export const originState = (location: Pick<Location, "pathname" | "search">): OriginState => ({
    from: `${location.pathname}${location.search}`,
});
