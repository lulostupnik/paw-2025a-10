import type { QueryClient } from "@tanstack/react-query";
import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";
import type { ProfileDetail, ProfileEditPayload, ProfileInterest, ProfileRatingStats } from "@/types/profile";
import { getUniversityByUrl } from "./journeys";
import { toPaged, mapPageList, type PageResult } from "@/types/pagination";
import { fetchByUrl } from "@/lib/utils/fetchByUrl";

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
    careerId: number;
    universityId: number;
    interestIds: number[];
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
    const response = await apiClient.get<UserPrivateApi[]>("/users", { params, signal, headers: { Accept: ContentTypes.USER_PRIVATE_LIST } });
    return toPaged(response);
};

export const registerUser = async (payload: RegisterPayload, signal?: AbortSignal): Promise<RegisteredUser> => {
    const { data } = await apiClient.post<RegisteredUser>("/users", payload, { signal, headers: { "Content-Type": ContentTypes.USER, Accept: ContentTypes.USER_PUBLIC } });
    return data;
};

export const updateUserBlocked = async (userId: number, blocked: boolean, signal?: AbortSignal) => {
    await apiClient.patch(`/users/${userId}`, { blocked }, { signal, headers: { "Content-Type": ContentTypes.USER, Accept: ContentTypes.USER_PUBLIC } });
};

export const updateUserProfile = async (
    userId: number | string,
    payload: ProfileEditPayload,
    signal?: AbortSignal
): Promise<ProfileDetail> => {
    const response = await apiClient.patch<ProfileDetail>(`/users/${userId}`, payload, { signal, headers: { "Content-Type": ContentTypes.USER, Accept: ContentTypes.USER_PUBLIC } });
    return response.data;
};

export const updateUserPassword = async (
    userId: number | string,
    password: string,
    signal?: AbortSignal
): Promise<void> => {
    await apiClient.patch(`/users/${userId}`, { password }, { signal, headers: { "Content-Type": ContentTypes.USER, Accept: ContentTypes.USER_PUBLIC } });
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
        headers: { Accept: "image/jpeg, image/png, image/webp" },
        responseType: "blob",
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
    await apiClient.post(`/users/${userId}/interests`, { interestId }, { signal, headers: { "Content-Type": ContentTypes.USER_INTEREST, Accept: ContentTypes.USER_INTEREST } });
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

export const getCareerByUrl = async (url?: string | null, signal?: AbortSignal, queryClient?: QueryClient) =>
    fetchByUrl(queryClient, "career", url, async (fetchSignal) => {
        const response = await apiClient.get<CareerApi>(normalizeApiPath(url as string), { signal: fetchSignal, headers: { Accept: ContentTypes.CAREER } });
        return response.data;
    }, signal);

export const invalidateUserViewQueries = async (
    queryClient: QueryClient,
    userId: number | string,
    selfUrl?: string | null
) => {
    const normalizedUserId = String(userId);
    const normalizedSelfUrl = normalizeApiPath(selfUrl ?? `/users/${normalizedUserId}`);

    await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["profileDetail", normalizedUserId] }),
        queryClient.invalidateQueries({ queryKey: ["profileDetail", "me"] }),
        queryClient.invalidateQueries({
            predicate: (query) => query.queryKey[0] === "profileInfo" && String(query.queryKey[1]) === normalizedUserId,
        }),
        queryClient.invalidateQueries({ queryKey: ["adminUserDetail", normalizedUserId] }),
        queryClient.invalidateQueries({ queryKey: ["adminUsers"] }),
        queryClient.invalidateQueries({ queryKey: ["profileTrips", normalizedUserId] }),
        queryClient.invalidateQueries({ queryKey: ["profileTrips", "me"] }),
        queryClient.invalidateQueries({ queryKey: ["profileEvents", normalizedUserId] }),
        queryClient.invalidateQueries({ queryKey: ["profileEvents", "me"] }),
        queryClient.invalidateQueries({ queryKey: ["events"] }),
        queryClient.invalidateQueries({ queryKey: ["journeys"] }),
        queryClient.invalidateQueries({ queryKey: ["adminEvents"] }),
        queryClient.invalidateQueries({ queryKey: ["byUrl", "user-public", normalizedSelfUrl] }),
        queryClient.invalidateQueries({
            predicate: (query) => {
                const key = query.queryKey[0];
                return key === "eventDetail" ||
                    key === "eventComments" ||
                    key === "eventAttendees" ||
                    key === "journeyDetail" ||
                    key === "journeyComments";
            },
        }),
    ]);
};

export const invalidateCurrentUserInterestQueries = async (queryClient: QueryClient, userId: number | string) => {
    const normalizedUserId = String(userId);
    await Promise.all([
        queryClient.invalidateQueries({
            predicate: (query) => {
                if (query.queryKey[0] !== "profileInterests") {
                    return false;
                }
                const params = query.queryKey[1];
                if (!params || typeof params !== "object") {
                    return false;
                }
                const profileId = (params as { profileId?: string }).profileId;
                return profileId === "me" || profileId === normalizedUserId;
            },
            refetchType: "all",
        }),
        queryClient.invalidateQueries({ queryKey: ["events"] }),
        queryClient.invalidateQueries({ queryKey: ["journeys"] }),
    ]);
};

// Resolves the profile's rating stats, university and career. Kept separate from
// the core profile fetch so the profile header can render before these complete.
export const buildProfileInfo = async (
    user: Pick<ProfileDetail, "id" | "links">,
    signal?: AbortSignal,
    queryClient?: QueryClient
): Promise<Pick<ProfileDetail, "ratingStats" | "university" | "career">> => {
    const [ratingStats, university, career] = await Promise.all([
        getUserRatingStats(user.id, signal),
        getUniversityByUrl(user.links?.universityUrl, signal, queryClient),
        getCareerByUrl(user.links?.careerUrl, signal, queryClient),
    ]);

    return { ratingStats, university, career };
};
