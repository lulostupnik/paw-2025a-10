import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import type {
    AdminCareerDetail,
    AdminCityDetail,
    AdminInterestDetail,
    AdminUniversityDetail,
    AdminUserDetail,
} from "@/types/admin";
import { getCareerById } from "@/lib/api/careers";
import { getCityById } from "@/lib/api/cities";
import { getInterestById } from "@/lib/api/interests";
import { getUniversityById, getUniversityByUrl } from "@/lib/api/universities";
import { getCareerByUrl, getUserPrivateById } from "@/lib/api/users";
import { getCityByUrl } from "@/lib/api/journeys";
import { DETAIL_QUERY_OPTIONS } from "@/lib/utils/queryDefaults";

interface AdminDetailParams {
    id?: string;
}

export const useAdminUserDetailData = (params?: AdminDetailParams) => {
    const userId = params?.id;
    const queryClient = useQueryClient();
    // Core user record renders immediately; the university/career lookups load
    // separately so they don't block the rest of the profile.
    const userQuery = useQuery({
        queryKey: ["adminUserDetail", userId],
        queryFn: ({ signal }) => {
            if (!userId) {
                throw new Error("missing-user-id");
            }
            return getUserPrivateById(userId, signal);
        },
        placeholderData: keepPreviousData,
        ...DETAIL_QUERY_OPTIONS,
        enabled: Boolean(userId),
    });

    const user = userQuery.data ?? null;
    const universityUrl = user?.links?.universityUrl ?? null;
    const careerUrl = user?.links?.careerUrl ?? null;

    const universityQuery = useQuery({
        queryKey: ["adminUserUniversity", userId, universityUrl],
        queryFn: ({ signal }) => getUniversityByUrl(universityUrl, signal, queryClient),
        enabled: Boolean(universityUrl),
        placeholderData: keepPreviousData,
    });

    const careerQuery = useQuery({
        queryKey: ["adminUserCareer", userId, careerUrl],
        queryFn: ({ signal }) => getCareerByUrl(careerUrl, signal, queryClient),
        enabled: Boolean(careerUrl),
        placeholderData: keepPreviousData,
    });

    const data: AdminUserDetail | null = user
        ? ({
              id: user.id,
              firstname: user.firstname ?? "",
              lastname: user.lastname ?? "",
              username: user.username ?? "",
              email: user.email,
              university: universityQuery.data ? { name: universityQuery.data.name } : null,
              career: careerQuery.data ? { name: careerQuery.data.name } : null,
              locale: null,
              profilePictureUrl: user.links?.profilePictureUrl ?? null,
              blocked: user.blocked,
          } satisfies AdminUserDetail)
        : null;

    return {
        data,
        isLoading: userQuery.isLoading,
        isError: userQuery.isError,
        universityLoading: Boolean(universityUrl) && universityQuery.isLoading,
        careerLoading: Boolean(careerUrl) && careerQuery.isLoading,
    };
};

export const useAdminUniversityDetailData = (params?: AdminDetailParams) => {
    const universityId = params?.id;
    const queryClient = useQueryClient();
    const query = useQuery({
        queryKey: ["adminUniversityDetail", universityId],
        queryFn: async ({ signal }) => {
            if (!universityId) {
                throw new Error("missing-university-id");
            }
            const university = await getUniversityById(universityId, signal);
            const city = await getCityByUrl(university.links?.cityUrl, signal, queryClient);
            return {
                id: university.id,
                name: university.name,
                abbreviation: university.abbreviation,
                city: { name: city?.name ?? "" },
            } satisfies AdminUniversityDetail;
        },
        placeholderData: keepPreviousData,
        ...DETAIL_QUERY_OPTIONS,
        enabled: Boolean(universityId),
    });
    return { data: query.data ?? null, isLoading: query.isLoading, isError: query.isError };
};

export const useAdminInterestDetailData = (params?: AdminDetailParams) => {
    const interestId = params?.id;
    const query = useQuery({
        queryKey: ["adminInterestDetail", interestId],
        queryFn: async ({ signal }) => {
            if (!interestId) {
                throw new Error("missing-interest-id");
            }
            const interest = await getInterestById(interestId, signal);
            return {
                id: interest.id,
                name: interest.name,
            } satisfies AdminInterestDetail;
        },
        placeholderData: keepPreviousData,
        ...DETAIL_QUERY_OPTIONS,
        enabled: Boolean(interestId),
    });
    return { data: query.data ?? null, isLoading: query.isLoading, isError: query.isError };
};

export const useAdminCityDetailData = (params?: AdminDetailParams) => {
    const cityId = params?.id;
    const query = useQuery({
        queryKey: ["adminCityDetail", cityId],
        queryFn: async ({ signal }) => {
            if (!cityId) {
                throw new Error("missing-city-id");
            }
            const city = await getCityById(cityId, signal);
            return {
                id: city.id,
                name: city.name,
                country: city.country ?? "",
            } satisfies AdminCityDetail;
        },
        placeholderData: keepPreviousData,
        ...DETAIL_QUERY_OPTIONS,
        enabled: Boolean(cityId),
    });
    return { data: query.data ?? null, isLoading: query.isLoading, isError: query.isError };
};

export const useAdminCareerDetailData = (params?: AdminDetailParams) => {
    const careerId = params?.id;
    const query = useQuery({
        queryKey: ["adminCareerDetail", careerId],
        queryFn: async ({ signal }) => {
            if (!careerId) {
                throw new Error("missing-career-id");
            }
            const career = await getCareerById(careerId, signal);
            return {
                id: career.id,
                name: career.name,
            } satisfies AdminCareerDetail;
        },
        placeholderData: keepPreviousData,
        ...DETAIL_QUERY_OPTIONS,
        enabled: Boolean(careerId),
    });
    return { data: query.data ?? null, isLoading: query.isLoading, isError: query.isError };
};
