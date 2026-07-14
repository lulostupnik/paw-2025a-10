import ReactDOM from "react-dom/client";
import { RouterProvider } from "react-router-dom";
import { QueryClientProvider } from "@tanstack/react-query";
import { router } from "@app/router";
import { RootErrorBoundary } from "@app/RootErrorBoundary";
import { I18nProvider } from "@/lib/i18n";
import { queryClient } from "@/lib/api/queryClient";
import { ToastProvider } from "@/components/ui/ToastProvider";
import "./index.css";

ReactDOM.createRoot(document.getElementById("root")!).render(
    <QueryClientProvider client={queryClient}>
        <I18nProvider>
            <RootErrorBoundary>
                <ToastProvider>
                    <RouterProvider router={router} />
                </ToastProvider>
            </RootErrorBoundary>
        </I18nProvider>
    </QueryClientProvider>
);
