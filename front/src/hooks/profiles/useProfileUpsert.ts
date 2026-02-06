import { useCallback, useState } from "react";
import type { ProfileEditPayload, ProfilePasswordPayload, ProfilePicturePayload } from "@/types/profile";
import { getUserId } from "@/lib/auth/auth";
import { addUserInterest, listAllUserInterests, removeUserInterest, updateUserPassword, updateUserProfile, updateUserProfilePicture } from "@/lib/api/users";

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
