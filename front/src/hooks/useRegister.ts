import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { isAxiosError } from "axios";
import { registerUser, type RegisterPayload, type RegisteredUser } from "@/lib/api/users";

export interface UseRegisterResult {
    register: (payload: RegisterPayload) => Promise<RegisteredUser>;
    loading: boolean;
    error: string | null;
    success: boolean;
    nextPath: string;
    reset: () => void;
}

const normalizeNextPath = (raw: string | null): string => {
    if (!raw || raw.trim().length === 0) {
        return "/explore";
    }
    return raw.startsWith("/") ? raw : `/${raw}`;
};

export function useRegister(): UseRegisterResult {
    const [searchParams] = useSearchParams();
    const nextPath = useMemo(() => normalizeNextPath(searchParams.get("next")), [searchParams]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);
    const abortRef = useRef<AbortController | null>(null);

    useEffect(() => {
        return () => {
            abortRef.current?.abort();
        };
    }, []);

    const register = useCallback(async (payload: RegisterPayload) => {
        abortRef.current?.abort();
        const controller = new AbortController();
        abortRef.current = controller;
        setLoading(true);
        setError(null);
        setSuccess(false);
        try {
            const user = await registerUser(payload, controller.signal);
            setSuccess(true);
            return user;
        } catch (err) {
            if (!controller.signal.aborted) {
                let key = "register.error.generic";
                if (isAxiosError(err)) {
                    const status = err.response?.status;
                    if (status === 400 || status === 409 || status === 422) {
                        key = "register.error.conflict";
                    }
                }
                setError(key);
            }
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    const reset = useCallback(() => {
        setError(null);
        setSuccess(false);
    }, []);

    return { register, loading, error, success, nextPath, reset };
}
