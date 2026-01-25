import { useCallback, useState } from "react";
import type { ProfileEditPayload, ProfilePasswordPayload, ProfilePicturePayload } from "@/types/profile";
import { getUserId } from "@/lib/auth/auth";
import { updateUserPassword, updateUserProfile, updateUserProfilePicture } from "@/lib/api/users";

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
            }),
        [runMutation]
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
            }),
        [runMutation]
    );

    const updateInterests = useCallback(
        async (interestIds: number[]) =>
            runMutation(async () => {
                // TODO: Implement user interests update once backend supports add/remove in bulk.
                console.warn("TODO: update user interests", interestIds);
                throw new Error("profile-interests-not-implemented");
            }),
        [runMutation]
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
