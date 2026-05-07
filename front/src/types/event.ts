export interface ProfileEvent {
    id: number;
    title: string;
    description?: string | null;
    date: string;
    city: { name: string };
    flyerImageUrl?: string | null;
    attendeesLimit?: number | null;
    attendeesCount?: number | null;
    isFull?: boolean;
    user: {
        id: number;
        username: string;
        firstname: string;
        lastname: string;
    };
}

export interface EventCreator {
    id: number;
    firstname: string;
    lastname: string;
    username: string;
    profilePictureUrl?: string | null;
    university?: { name: string } | null;
    career?: { name: string } | null;
}

export interface EventComment {
    id: number;
    user: { username: string };
    message: string;
    dateTime: string;
}

export interface EventAttendee {
    id: number;
    firstname: string;
    lastname: string;
    profilePictureUrl?: string | null;
}

export interface EventRating {
    id: number;
    user: { username: string };
    rating: number;
    dateTime: string;
}

export interface EventDetail {
    id: number;
    title: string;
    description: string;
    city: { name: string };
    date: string;
    time?: string | null;
    address?: string | null;
    flyerImageUrl?: string | null;
    attendeesCount: number;
    attendeesLimit?: number | null;
    isFuture: boolean;
    user: EventCreator;
    comments: EventComment[];
    attendees: EventAttendee[];
    ratings: EventRating[];
    averageRating?: number | null;
}

export interface EventStatistics {
    eventsCreatedByOrganizer: number;
    eventsOrganizerAttends: number;
    topCountry: string;
    topCountryCount: number;
    totalParticipants: number;
    maxParticipants: number;
    links?: {
        selfUrl?: string;
        eventUrl?: string;
    } | null;
}
