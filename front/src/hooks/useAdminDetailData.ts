import { keepPreviousData, useQuery } from "@tanstack/react-query";
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
import { getCareerByUrl, getUserById } from "@/lib/api/users";
import { getCityByUrl } from "@/lib/api/journeys";

interface AdminDetailParams {
    id?: string;
}

export const useAdminUserDetailData = (params?: AdminDetailParams) => {
    const userId = params?.id;
    const query = useQuery({
        queryKey: ["adminUserDetail", userId],
        queryFn: async ({ signal }) => {
            if (!userId) {
                throw new Error("missing-user-id");
            }
            const user = await getUserById(userId, signal);
            const [university, career] = await Promise.all([
                getUniversityByUrl(user.links?.universityUrl, signal),
                getCareerByUrl(user.links?.careerUrl, signal),
            ]);
            return {
                id: user.id,
                firstname: user.firstname ?? "",
                lastname: user.lastname ?? "",
                username: user.username ?? "",
                email: user.email ?? "",
                university: university ? { name: university.name } : null,
                career: career ? { name: career.name } : null,
                locale: null,
                profilePictureUrl: user.links?.profilePictureUrl ?? null,
                blocked: user.active === false,
            } satisfies AdminUserDetail;
        },
        placeholderData: keepPreviousData,
        enabled: Boolean(userId),
    });
    return { data: query.data ?? null, isLoading: query.isLoading, isError: query.isError };
};

export const useAdminUniversityDetailData = (params?: AdminDetailParams) => {
    const universityId = params?.id;
    const query = useQuery({
        queryKey: ["adminUniversityDetail", universityId],
        queryFn: async ({ signal }) => {
            if (!universityId) {
                throw new Error("missing-university-id");
            }
            const university = await getUniversityById(universityId, signal);
            const city = await getCityByUrl(university.links?.cityUrl, signal);
            return {
                id: university.id,
                name: university.name,
                abbreviation: university.abbreviation,
                city: { name: city?.name ?? "" },
            } satisfies AdminUniversityDetail;
        },
        placeholderData: keepPreviousData,
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
        enabled: Boolean(careerId),
    });
    return { data: query.data ?? null, isLoading: query.isLoading, isError: query.isError };
};
