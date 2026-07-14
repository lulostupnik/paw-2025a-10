import { useCallback, useState } from "react";
import type { ProfileEditPayload, ProfilePasswordPayload, ProfilePicturePayload } from "@/types/profile";
import { getProfilePictureUrl, getUserId, setSession } from "@/lib/auth/auth";
import { useQueryClient } from "@tanstack/react-query";
import {
    addUserInterest,
    invalidateCurrentUserInterestQueries,
    invalidateUserViewQueries,
    listAllUserInterests,
    removeUserInterest,
    updateUserPassword,
    updateUserProfile,
    updateUserProfilePicture,
} from "@/lib/api/users";
import type { ProfileDetail } from "@/types/profile";
import { apiBaseUrl } from "@/lib/api/client";

interface UseProfileUpsertResult {
    isLoading: boolean;
    isError: boolean;
    error: string | null;
    updateProfile: (payload: ProfileEditPayload) => Promise<void>;
    updatePassword: (payload: ProfilePasswordPayload) => Promise<void>;
    updatePicture: (payload: ProfilePicturePayload) => Promise<void>;
    updateInterests: (interestIds: number[]) => Promise<void>;
}

export const useProfileUpsert = (): UseProfileUpsertResult => {
    const queryClient = useQueryClient();
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const runMutation = useCallback(async (action: () => Promise<void>) => {
        setIsLoading(true);
        setError(null);
        try {
            await action();
        } catch (err) {
            setError(err instanceof Error ? err.message : "unknown-error");
            throw err;
        } finally {
            setIsLoading(false);
        }
    }, []);

    const updateProfile = useCallback(
        async (payload: ProfileEditPayload) =>
            runMutation(async () => {
                const userId = getUserId();
                if (!userId) {
                    throw new Error("missing-user-id");
                }
                await updateUserProfile(userId, payload);
                await invalidateUserViewQueries(queryClient, userId);
            }),
        [queryClient, runMutation]
    );

    const updatePassword = useCallback(
        async (payload: ProfilePasswordPayload) =>
            runMutation(async () => {
                const userId = getUserId();
                if (!userId) {
                    throw new Error("missing-user-id");
                }
                await updateUserPassword(userId, payload.password);
            }),
        [runMutation]
    );

    const updatePicture = useCallback(
        async (payload: ProfilePicturePayload) =>
            runMutation(async () => {
                const userId = getUserId();
                if (!userId) {
                    throw new Error("missing-user-id");
                }
                if (!payload.picture) {
                    throw new Error("missing-profile-picture");
                }
                await updateUserProfilePicture(userId, payload.picture);
                const currentProfile =
                    (queryClient.getQueryData(["profileDetail", String(userId)]) as ProfileDetail | undefined) ??
                    (queryClient.getQueryData(["profileDetail", "me"]) as ProfileDetail | undefined);
                const profilePictureUrl =
                    currentProfile?.links?.profilePictureUrl ??
                    getProfilePictureUrl() ??
                    `${apiBaseUrl}/users/${userId}/profilePicture`;
                setSession({
                    profilePictureUrl,
                    profilePictureVersion: String(Date.now()),
                });
                await invalidateUserViewQueries(queryClient, userId);
            }),
        [queryClient, runMutation]
    );

    const updateInterests = useCallback(
        async (interestIds: number[]) =>
            runMutation(async () => {
                const userId = getUserId();
                if (!userId) {
                    throw new Error("missing-user-id");
                }
                const current = await listAllUserInterests(userId);
                const currentIds = new Set(current.map((interest) => interest.id));
                const nextIds = new Set(interestIds);
                const toAdd = [...nextIds].filter((id) => !currentIds.has(id));
                const toRemove = [...currentIds].filter((id) => !nextIds.has(id));
                await Promise.all([
                    ...toAdd.map((id) => addUserInterest(userId, id)),
                    ...toRemove.map((id) => removeUserInterest(userId, id)),
                ]);
                await invalidateCurrentUserInterestQueries(queryClient, userId);
            }),
        [queryClient, runMutation]
    );

    return {
        isLoading,
        isError: Boolean(error),
        error,
        updateProfile,
        updatePassword,
        updatePicture,
        updateInterests,
    };
};
