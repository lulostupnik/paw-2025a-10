export interface ProfileJourney {
    id: number;
    title: string;
    deleted?: boolean;
}

export interface JourneyLinks {
    selfUrl?: string | null;
    userUrl?: string | null;
    destinationUniversityUrl?: string | null;
    tipsUrl?: string | null;
    responsesUrl?: string | null;
}

export interface JourneySummary {
    id: number;
    description: string;
    startDate: string;
    endDate: string;
    destinationUniversityName?: string | null;
    links?: JourneyLinks | null;
    city?: string;
    country?: string;
    university?: string;
    userName?: string;
    profilePictureUrl?: string | null;
}

export interface JourneyCreator {
    id: number;
    firstname: string;
    lastname: string;
    username: string;
    profilePictureUrl?: string | null;
    university?: { name: string } | null;
    career?: { name: string } | null;
}

export interface JourneyEvent {
    id: number;
    title: string;
    description: string;
    city: string;
    date: string;
    time?: string | null;
    flyerImageUrl?: string | null;
}

export interface JourneyComment {
    id: number;
    user: {
        username: string;
    };
    message: string;
    dateTime: string;
}

export interface JourneyTip {
    id: number;
    title: string;
    content: string;
    dateTime: string;
}

export interface JourneyDetail {
    id: number;
    description: string;
    startDate: string;
    endDate: string;
    links?: JourneyLinks | null;
    destinationUniversity?: {
        name?: string;
        city?: string;
    };
    user?: JourneyCreator;
    interests: string[];
    events: JourneyEvent[];
    tips: JourneyTip[];
    comments: JourneyComment[];
}
