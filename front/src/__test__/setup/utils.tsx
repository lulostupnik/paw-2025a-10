import type { ReactElement, ReactNode } from "react";
import {
    render,
    renderHook,
    type RenderOptions,
    type RenderHookOptions,
} from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { MemoryRouter } from "react-router-dom";

const AllTheProviders = ({ children }: { children: ReactNode }) => {
    const queryClient = new QueryClient({
        defaultOptions: {
            queries: {
                retry: false,
                gcTime: 0,
                staleTime: 0,
            },
        },
    });

    return (
        <QueryClientProvider client={queryClient}>
            <MemoryRouter initialEntries={["/"]}>
                {children}
            </MemoryRouter>
        </QueryClientProvider>
    );
};

export const testQueryClient = () =>
    new QueryClient({
        defaultOptions: {
            queries: {
                retry: false,
                gcTime: 0,
                staleTime: 0,
            },
        },
    });

const customRender = (ui: ReactElement, options?: RenderOptions) =>
    render(ui, { wrapper: AllTheProviders, ...options });

const customRenderHook = <Result, Props>(
    hook: (initialProps: Props) => Result,
    options?: RenderHookOptions<Props>,
) => {
    const userWrapper = options?.wrapper;
    return renderHook(hook, {
        wrapper: userWrapper ?? AllTheProviders,
        ...options,
    });
};

export * from "@testing-library/react";
export { customRender as render };
export { customRenderHook as renderHook };
