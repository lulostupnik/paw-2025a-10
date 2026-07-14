import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import ProfileForm from "@/pages/profiles/ProfileForm";
import ProfilePictureForm from "@/pages/profiles/ProfilePictureForm";
import ProfilePasswordForm from "@/pages/profiles/ProfilePasswordForm";
import { emptyPage } from "@/types/pagination";

vi.mock("@/lib/i18n", () => ({
    useI18n: () => ({
        t: (key: string) => key,
        locale: "en" as const,
        setLocale: vi.fn(),
        availableLocales: ["en", "es"] as const,
    }),
}));

const mockShowToast = vi.fn();
vi.mock("@/components/ui/ToastProvider", () => ({
    useToast: () => ({
        showToast: mockShowToast,
    }),
}));

vi.mock("@/lib/utils/internalPath", () => ({
    sanitizeInternalPath: (v: unknown) => (typeof v === "string" ? v : null),
}));

const mockUseProfileDetail = vi.fn();
vi.mock("@/hooks/profiles/useProfileDetail", () => ({
    useProfileDetail: (...args: unknown[]) => mockUseProfileDetail(...args),
}));

const mockUpdateProfile = vi.fn();
const mockUpdatePicture = vi.fn();
const mockUpdatePassword = vi.fn();
vi.mock("@/hooks/profiles/useProfileUpsert", () => ({
    useProfileUpsert: () => ({
        updateProfile: mockUpdateProfile,
        updatePicture: mockUpdatePicture,
        updatePassword: mockUpdatePassword,
        isLoading: false,
        isError: false,
        error: null,
    }),
}));

const mockUseQuery = vi.fn();

vi.mock("@tanstack/react-query", async () => {
    const actual = await vi.importActual<typeof import("@tanstack/react-query")>("@tanstack/react-query");
    return {
        ...actual,
        useQuery: (...args: unknown[]) => mockUseQuery(...args),
    };
});

vi.mock("@/lib/api/careers", () => ({
    listCareers: vi.fn(),
}));

vi.mock("@/lib/api/universities", () => ({
    listUniversities: vi.fn(),
}));

vi.mock("@/components/profiles/SingleSelectAutocomplete", () => ({
    default: ({
        id,
        value,
        options,
        onChange,
    }: {
        id: string;
        value: { id: number; name: string } | null;
        options: Array<{ id: number; name: string }>;
        onChange: (option: { id: number; name: string } | null) => void;
    }) => (
        <div data-testid={id}>
            <span>{value?.name ?? "empty"}</span>
            {options[1] && (
                <button type="button" onClick={() => onChange(options[1])}>
                    choose-{id}
                </button>
            )}
        </div>
    ),
}));

vi.mock("@/components/ui/PageStatus", () => ({
    default: ({ message }: { message: string }) => <div>{message}</div>,
}));

function renderWithRouter(initialEntry: string, state?: unknown) {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [
            { path: "/profiles/me/edit", element: <ProfileForm /> },
            { path: "/profiles/me/edit-picture", element: <ProfilePictureForm /> },
            { path: "/profiles/me/change-password", element: <ProfilePasswordForm /> },
            { path: "/profiles/:profileId/:tab", element: <div>profile</div> },
            { path: "/events/:id", element: <div>event</div> },
        ],
        {
            initialEntries: [{ pathname: initialEntry, state }],
            initialIndex: 0,
        }
    );

    const view = render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>
    );

    return { ...view, router };
}

describe("Profile forms", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        mockUseQuery.mockImplementation(({ queryKey }: { queryKey: string[] }) => {
            if (queryKey[0] === "profileUniversities") {
                return {
                    data: {
                        ...emptyPage(),
                        content: [
                            { id: 1, name: "UBA" },
                            { id: 2, name: "UTN" },
                        ],
                    },
                    isError: false,
                };
            }
            if (queryKey[0] === "profileCareers") {
                return {
                    data: {
                        ...emptyPage(),
                        content: [
                            { id: 1, name: "CS" },
                            { id: 2, name: "Math" },
                        ],
                    },
                    isError: false,
                };
            }
            return { data: undefined, isError: false };
        });
        mockUseProfileDetail.mockReturnValue({
            data: {
                id: 7,
                firstname: "Test",
                lastname: "User",
                username: "testuser",
                links: { profilePictureUrl: "/image.jpg" },
                isMine: true,
                university: { name: "UBA" },
                career: { name: "CS" },
            },
            isLoading: false,
            isError: false,
        });
    });

    it("uses the caller path for edit profile go back", () => {
        const { router } = renderWithRouter("/profiles/me/edit", { from: "/events/42" });

        screen.getByRole("button", { name: /profile.back.to.profile/i }).click();

        expect(router.state.location.pathname).toBe("/events/42");
    });

    it("navigates away from edit profile after saving", async () => {
        const user = userEvent.setup();
        mockUpdateProfile.mockResolvedValueOnce(undefined);
        const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
        const router = createMemoryRouter(
            [
                { path: "/profiles/:profileId/:tab", element: <div>profile detail</div> },
                { path: "/profiles/me/edit", element: <ProfileForm /> },
            ],
            {
                initialEntries: [{ pathname: "/profiles/me/edit", state: { from: "/profiles/me/info" } }],
            }
        );

        render(
            <QueryClientProvider client={queryClient}>
                <RouterProvider router={router} />
            </QueryClientProvider>
        );

        await user.click(screen.getByRole("button", { name: /profile.save.changes/i }));

        expect(router.state.location.pathname).toBe("/profiles/me/info");
        expect(await screen.findByText("profile detail")).toBeInTheDocument();
    });

    it("keeps the locally selected university and career before saving", async () => {
        const user = userEvent.setup();
        renderWithRouter("/profiles/me/edit", { from: "/profiles/me/info" });

        expect(screen.getByTestId("originUniversity")).toHaveTextContent("UBA");
        expect(screen.getByTestId("career")).toHaveTextContent("CS");

        await user.click(screen.getByRole("button", { name: "choose-originUniversity" }));
        await user.click(screen.getByRole("button", { name: "choose-career" }));

        expect(screen.getByTestId("originUniversity")).toHaveTextContent("UTN");
        expect(screen.getByTestId("career")).toHaveTextContent("Math");
    });

    it("shows backend error when picture update fails", async () => {
        const user = userEvent.setup();
        mockUpdatePicture.mockRejectedValueOnce({
            isAxiosError: true,
            response: { data: { message: "backend upload failed" } },
        });

        renderWithRouter("/profiles/me/edit-picture", { from: "/events/42" });

        const file = new File(["image"], "photo.jpg", { type: "image/jpeg" });
        await user.upload(screen.getByLabelText("profile.picture"), file);
        await user.click(screen.getByRole("button", { name: /profile.save.changes/i }));

        expect(await screen.findByText("backend upload failed")).toBeInTheDocument();
    });

    it("uses the caller path for picture go back", () => {
        renderWithRouter("/profiles/me/edit-picture", { from: "/events/42" });

        expect(screen.getByRole("link", { name: /profile.back.to.profile/i })).toHaveAttribute("href", "/events/42");
    });

    it("renders the signup-style password strength meter", async () => {
        const user = userEvent.setup();
        renderWithRouter("/profiles/me/change-password", { from: "/events/42" });

        await user.type(screen.getByLabelText("profile.new.password"), "Password1!");

        expect(screen.getByText("register.password.strength.label")).toBeInTheDocument();
        expect(screen.getByText("register.password.strength.strong")).toBeInTheDocument();
        expect(screen.getAllByText("", { selector: ".password-strength__bar" })).toHaveLength(4);
    });

    it("uses the caller path for password go back", () => {
        renderWithRouter("/profiles/me/change-password", { from: "/events/42" });

        expect(screen.getByRole("link", { name: /profile.back.to.profile/i })).toHaveAttribute("href", "/events/42");
    });
});
