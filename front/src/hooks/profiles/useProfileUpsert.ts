import { useCallback, useState } from "react";
import type { ProfileEditPayload, ProfilePasswordPayload, ProfilePicturePayload } from "@/types/profile";

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
                // TODO: PATCH /api/profiles/me
                // TODO: request body: ProfileEditPayload
                // TODO: expected response: ProfileDetail
                const USE_MOCKS = true;
                if (USE_MOCKS) {
                    return;
                }
            }),
        [runMutation]
    );

    const updatePassword = useCallback(
        async (payload: ProfilePasswordPayload) =>
            runMutation(async () => {
                // TODO: POST /api/profiles/me/password
                // TODO: request body: ProfilePasswordPayload
                // TODO: expected response: { success: boolean }
                const USE_MOCKS = true;
                if (USE_MOCKS) {
                    return;
                }
            }),
        [runMutation]
    );

    const updatePicture = useCallback(
        async (payload: ProfilePicturePayload) =>
            runMutation(async () => {
                // TODO: POST /api/profiles/me/picture
                // TODO: request body: FormData with ProfilePicturePayload
                // TODO: expected response: { profilePictureUrl: string }
                const USE_MOCKS = true;
                if (USE_MOCKS) {
                    return;
                }
            }),
        [runMutation]
    );

    const updateInterests = useCallback(
        async (interestIds: number[]) =>
            runMutation(async () => {
                // TODO: PUT /api/profiles/me/interests
                // TODO: request body: { interests: number[] }
                // TODO: expected response: ProfileDetail
                const USE_MOCKS = true;
                if (USE_MOCKS) {
                    return;
                }
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
