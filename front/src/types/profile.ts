import type { ProfileEvent } from "@/types/event";
import type { ProfileJourney } from "@/types/journey";

export interface ProfileSummary {
    id: number;
    firstname: string;
    lastname: string;
    username: string;
    profilePictureUrl?: string | null;
    university?: { name: string } | null;
    career?: { name: string } | null;
}

export interface ProfileDetail extends ProfileSummary {
    email?: string | null;
    bio?: string | null;
    isMine: boolean;
    journey?: ProfileJourney | null;
    interests: ProfileInterest[];
    ratingStats: ProfileRatingStats;
}

export interface ProfileInterest {
    id: number;
    name: string;
}

export interface ProfileRatingStats {
    averageCreatedEventsRating?: number | null;
    averageAttendedEventsRating?: number | null;
}

export interface ProfileEditPayload {
    firstName: string;
    lastName: string;
    username: string;
    originUniversity?: string;
    career?: string;
}

export interface ProfilePicturePayload {
    picture: File | null;
}

export interface ProfilePasswordPayload {
    password: string;
    confirmPassword: string;
}

export interface ProfileEventsPayload {
    created: ProfileEvent[];
    attending: ProfileEvent[];
    finished: ProfileEvent[];
}
