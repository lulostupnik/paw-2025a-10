import type { ProfileEvent } from "@/types/event";
import type { ProfileJourney } from "@/types/journey";

export interface ProfileLinks {
    selfUrl?: string | null;
    profilePictureUrl?: string | null;
    universityUrl?: string | null;
    careerUrl?: string | null;
    journeyUrl?: string | null;
}

export interface ProfileSummary {
    id: number;
    firstname: string;
    lastname: string;
    username: string;
    email?: string | null;
    links?: ProfileLinks | null;
}

export interface ProfileDetail extends ProfileSummary {
    isMine: boolean;
    journey?: ProfileJourney | null;
    ratingStats: ProfileRatingStats;
    university?: { name: string } | null;
    career?: { name: string } | null;
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
    universityId?: number;
    careerId?: number;
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
