import { apiClient } from "@/lib/api/client";

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

export const registerUser = async (payload: RegisterPayload, signal?: AbortSignal): Promise<RegisteredUser> => {
    const { data } = await apiClient.post<RegisteredUser>("/users", payload, { signal });
    return data;
};
