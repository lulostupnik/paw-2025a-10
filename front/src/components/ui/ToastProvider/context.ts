import { createContext, useContext } from "react";

export type ToastVariant = "success" | "error" | "info";

export interface ToastOptions {
    variant?: ToastVariant;
    duration?: number;
}

export interface ToastContextValue {
    showToast: (message: string, options?: ToastOptions) => void;
}

export const ToastContext = createContext<ToastContextValue | undefined>(undefined);

export function useToast() {
    const context = useContext(ToastContext);
    if (!context) {
        throw new Error("useToast must be used within a ToastProvider");
    }
    return context;
}
