import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";
import type { ProfileDetail, ProfileEditPayload, ProfileInterest, ProfileRatingStats } from "@/types/profile";
import { getUniversityByUrl } from "./journeys";
import { getEmail, getUserId } from "../auth/auth";
import { toPaged, mapPageList, type PageResult } from "@/types/pagination";

export interface RegisteredUser {
    id: number;
    username: string;
    links?: {
        selfUrl?: string | null;
        profilePictureUrl?: string | null;
        universityUrl?: string | null;
        careerUrl?: string | null;
        journeyUrl?: string | null;
    } | null;
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

export interface ListUsersParams {
    search?: string;
    page?: number;
    size?: number;
    blocked?: boolean;
    university?: number;
    career?: number;
    interest?: number;
}

export interface ListUserInterestParams {
    page?: number;
    size?: number;
}

export interface UserApi {
    id: number;
    username: string;
    firstname: string | null;
    lastname: string | null;
    links?: {
        selfUrl?: string | null;
        profilePictureUrl?: string | null;
        universityUrl?: string | null;
        careerUrl?: string | null;
        journeyUrl?: string | null;
    } | null;
}

export interface UserPrivateApi extends UserApi {
    email: string;
    isAdmin: boolean;
    verified: boolean;
    blocked: boolean;
}

interface UserInterestApi {
    interestId: number;
    interestName: string;
}

interface UserRatingApi {
    attendedEventsRating?: number | null;
    hostedEventsRating?: number | null;
    links?: {
        selfUrl?: string | null;
        userUrl?: string | null;
    } | null;
}

export const listUsers = async (params: ListUsersParams = {}, signal?: AbortSignal): Promise<PageResult<UserPrivateApi>> => {
    const response = await apiClient.get<UserPrivateApi[]>("/users", { params, signal, headers: { Accept: ContentTypes.USER_LIST } });
    return toPaged(response);
};

export const registerUser = async (payload: RegisterPayload, signal?: AbortSignal): Promise<RegisteredUser> => {
    const { data } = await apiClient.post<RegisteredUser>("/users", payload, { signal, headers: { "Content-Type": ContentTypes.USER } });
    return data;
};

export const updateUserBlocked = async (userId: number, blocked: boolean, signal?: AbortSignal) => {
    await apiClient.patch(`/users/${userId}`, { blocked }, { signal, headers: { "Content-Type": ContentTypes.USER } });
};

export const updateUserProfile = async (
    userId: number | string,
    payload: ProfileEditPayload,
    signal?: AbortSignal
): Promise<ProfileDetail> => {
    const response = await apiClient.patch<ProfileDetail>(`/users/${userId}`, payload, { signal, headers: { "Content-Type": ContentTypes.USER } });
    return response.data;
};

export const updateUserPassword = async (
    userId: number | string,
    password: string,
    signal?: AbortSignal
): Promise<void> => {
    await apiClient.patch(`/users/${userId}`, { password }, { signal, headers: { "Content-Type": ContentTypes.USER } });
};

export const updateUserProfilePicture = async (
    userId: number | string,
    picture: File,
    signal?: AbortSignal
): Promise<void> => {
    const formData = new FormData();
    formData.append("profilePicture", picture);
    await apiClient.put(`/users/${userId}/profilePicture`, formData, {
        signal,
        headers: { "Content-Type": "multipart/form-data" },
    });
};

export const getProfileDetail = async (id: string | number, signal?: AbortSignal): Promise<ProfileDetail> => {
    const response = await apiClient.get<ProfileDetail>(`/users/${id}`, { signal, headers: { Accept: ContentTypes.USER_PUBLIC } });
    return response.data
}

export const getUserPrivateById = async (id: number | string, signal?: AbortSignal): Promise<UserPrivateApi> => {
    const response = await apiClient.get<UserPrivateApi>(`/users/${id}`, { signal, headers: { Accept: ContentTypes.USER } });
    return response.data;
};

export const getUserRatingStats = async (userId: string | number, signal?: AbortSignal): Promise<ProfileRatingStats> => {
    const response = await apiClient.get<UserRatingApi>(`/users/${userId}/rating`, { signal, headers: { Accept: ContentTypes.USER_RATING } });
    const data = response.data ?? {};
    return {
        averageCreatedEventsRating: data.hostedEventsRating ?? null,
        averageAttendedEventsRating: data.attendedEventsRating ?? null,
    };
}

export const getUserInterests = async (userId: string | number, params: ListUserInterestParams = {}, signal?: AbortSignal): Promise<PageResult<ProfileInterest>> => {
    const response = await apiClient.get<UserInterestApi[]>(`/users/${userId}/interests`, { params, signal, headers: { Accept: ContentTypes.USER_INTEREST_LIST } });
    const page = toPaged(response);
    const mapped = page.content.map((interest) => ({
        id: interest.interestId,
        name: interest.interestName ?? "",
    }));
    return mapPageList(page, mapped);
}

export const addUserInterest = async (userId: string | number, interestId: number, signal?: AbortSignal) => {
    await apiClient.post(`/users/${userId}/interests`, { interestId }, { signal, headers: { "Content-Type": ContentTypes.USER_INTEREST } });
};

export const removeUserInterest = async (userId: string | number, interestId: number, signal?: AbortSignal) => {
    await apiClient.delete(`/users/${userId}/interests/${interestId}`, { signal });
};

export const listAllUserInterests = async (userId: string | number, signal?: AbortSignal) => {
    const pageSize = 200;
    const firstPage = await getUserInterests(userId, { page: 1, size: pageSize }, signal);
    const all = [...firstPage.content];
    for (let page = 2; page <= firstPage.totalPages; page += 1) {
        const nextPage = await getUserInterests(userId, { page, size: pageSize }, signal);
        all.push(...nextPage.content);
    }
    return all;
};

export const getCareerByUrl = async (url?: string | null, signal?: AbortSignal) => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<CareerApi>(normalizeApiPath(url), { signal, headers: { Accept: ContentTypes.CAREER } });
    return response.data;
};

export const buildProfileDetail = async (user: ProfileDetail, signal?: AbortSignal): Promise<ProfileDetail> => {
    const [ratingStats, university, career] = await Promise.all([
        getUserRatingStats(user.id, signal),
        getUniversityByUrl(user.links?.universityUrl, signal),
        getCareerByUrl(user.links?.careerUrl, signal),
    ]);

    const isMine = user.id == getUserId();
    return {
        id: user.id,
        firstname: user.firstname,
        lastname: user.lastname,
        username: user.username,
        email: isMine ? getEmail() : null,
        links: user.links ?? null,
        ratingStats: ratingStats,
        isMine,
        career,
        university
    };
}
