import { createContext, useCallback, useContext, useMemo, useState } from "react";
import type { ReactNode } from "react";

type ToastVariant = "success" | "error" | "info";

interface ToastItem {
    id: string;
    message: string;
    variant: ToastVariant;
}

interface ToastContextValue {
    showToast: (message: string, options?: { variant?: ToastVariant; duration?: number }) => void;
}

const ToastContext = createContext<ToastContextValue | undefined>(undefined);

const createToastId = () => {
    if (typeof crypto !== "undefined" && "randomUUID" in crypto) {
        return crypto.randomUUID();
    }
    return `${Date.now()}-${Math.random().toString(16).slice(2)}`;
};

interface ToastProviderProps {
    children: ReactNode;
}

export function ToastProvider({ children }: ToastProviderProps) {
    const [toasts, setToasts] = useState<ToastItem[]>([]);

    const showToast = useCallback(
        (message: string, options?: { variant?: ToastVariant; duration?: number }) => {
            const id = createToastId();
            const variant = options?.variant ?? "info";
            const duration = options?.duration ?? 2400;
            setToasts((prev) => [...prev, { id, message, variant }]);
            window.setTimeout(() => {
                setToasts((prev) => prev.filter((toast) => toast.id !== id));
            }, duration);
        },
        []
    );

    const value = useMemo(() => ({ showToast }), [showToast]);

    return (
        <ToastContext.Provider value={value}>
            {children}
            <div className="toast-viewport" role="status" aria-live="polite">
                {toasts.map((toast) => (
                    <div key={toast.id} className={`toast toast--${toast.variant}`}>
                        {toast.message}
                    </div>
                ))}
            </div>
        </ToastContext.Provider>
    );
}

export function useToast() {
    const context = useContext(ToastContext);
    if (!context) {
        throw new Error("useToast must be used within a ToastProvider");
    }
    return context;
}
