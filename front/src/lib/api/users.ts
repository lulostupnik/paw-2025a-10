import { apiClient, normalizeApiPath } from "@/lib/api/client";
import type { ProfileDetail, ProfileInterest, ProfileRatingStats } from "@/types/profile";
import { getUniversityByUrl } from "./journeys";
import { getUserId } from "../auth/auth";

export interface RegisteredUser {
    id: number;
    username: string;
    email: string;
    role?: string;
    profilePictureUrl?: string | null;
    selfUrl?: string;
    active?: boolean;
}

export interface RegisterPayload {
    email: string;
    username: string;
    firstName: string;
    lastName: string;
    password: string;
    confirmPassword: string;
    career: string;
    originUniversity: string;
    interests: string[];
}

export interface CareerApi {
    name: string
}

export const registerUser = async (payload: RegisterPayload, signal?: AbortSignal): Promise<RegisteredUser> => {
    const { data } = await apiClient.post<RegisteredUser>("/users", payload, { signal });
    return data;
};

export const updateUserBlocked = async (userId: number, blocked: boolean, signal?: AbortSignal) => {
    await apiClient.put(`/users/${userId}/blocked`, { blocked }, { signal });
};

export const getProfileDetail = async (id: string | number, signal?: AbortSignal): Promise<ProfileDetail> => {
    const response = await apiClient.get<ProfileDetail>(`/users/${id}`, { signal });
    return response.data
}

export const getUserRatingStats = async (userId: string | number, signal?: AbortSignal): Promise<ProfileRatingStats> => {
    const response = await apiClient.get<ProfileRatingStats>(`/users/${userId}/rating`, { signal });
    return response.data
}

export const getUserInterests = async (userId: string | number, signal?: AbortSignal): Promise<ProfileInterest[]> => {
    const response = await apiClient.get<ProfileInterest[]>(`/users/${userId}/interests`, { signal });
    return response.data
}

export const getCareerByUrl = async (url?: string | null, signal?: AbortSignal) => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<CareerApi>(normalizeApiPath(url), { signal });
    return response.data;
};

export const buildProfileDetail = async (user: ProfileDetail, signal?: AbortSignal): Promise<ProfileDetail> => {
    const [ratingStats, interests, university, career] = await Promise.all([
        getUserRatingStats(user.id, signal),
        getUserInterests(user.id, signal),
        getUniversityByUrl(user.universityUrl, signal),
        getCareerByUrl(user.careerUrl, signal),
    ]);


    return {
        id: user.id,
        firstname: user.firstname,
        lastname: user.lastname,
        username: user.username,
        ratingStats: ratingStats,
        interests: interests,
        isMine: user.id == getUserId(),
        profilePictureUrl: user.profilePictureUrl,
        career,
        university
    };
}