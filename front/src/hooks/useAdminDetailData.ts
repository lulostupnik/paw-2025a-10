import { useMemo } from "react";
import {
    type AdminCareerDetail,
    type AdminCityDetail,
    type AdminDetailScenario,
    type AdminInterestDetail,
    type AdminUniversityDetail,
    type AdminUserDetail,
    getAdminCareerDetailMock,
    getAdminCityDetailMock,
    getAdminInterestDetailMock,
    getAdminUniversityDetailMock,
    getAdminUserDetailMock,
} from "@/mocks/adminDetail.mock";

interface AdminDetailParams {
    scenario?: AdminDetailScenario;
    id?: string;
}

const useAdminDetailScenario = ({ scenario = "normal" }: AdminDetailParams = {}) => {
    return {
        isLoading: scenario === "loading",
        isError: scenario === "error",
    };
};

export const useAdminUserDetailData = (params?: AdminDetailParams) => {
    const scenario = params?.scenario ?? "normal";
    const userId = params?.id;
    // TODO: GET /api/admin/users/{userId}
    // TODO: expected response shape: AdminUserDetail
    const USE_MOCKS = true;
    const data = useMemo<AdminUserDetail>(() => {
        if (USE_MOCKS) {
            return getAdminUserDetailMock(scenario);
        }
        return getAdminUserDetailMock(scenario);
    }, [scenario, userId]);
    return { data, ...useAdminDetailScenario({ scenario }) };
};

export const useAdminUniversityDetailData = (params?: AdminDetailParams) => {
    const scenario = params?.scenario ?? "normal";
    const universityId = params?.id;
    // TODO: GET /api/admin/universities/{universityId}
    // TODO: expected response shape: AdminUniversityDetail
    const USE_MOCKS = true;
    const data = useMemo<AdminUniversityDetail>(() => {
        if (USE_MOCKS) {
            return getAdminUniversityDetailMock(scenario);
        }
        return getAdminUniversityDetailMock(scenario);
    }, [scenario, universityId]);
    return { data, ...useAdminDetailScenario({ scenario }) };
};

export const useAdminInterestDetailData = (params?: AdminDetailParams) => {
    const scenario = params?.scenario ?? "normal";
    const interestId = params?.id;
    // TODO: GET /api/admin/interests/{interestId}
    // TODO: expected response shape: AdminInterestDetail
    const USE_MOCKS = true;
    const data = useMemo<AdminInterestDetail>(() => {
        if (USE_MOCKS) {
            return getAdminInterestDetailMock(scenario);
        }
        return getAdminInterestDetailMock(scenario);
    }, [scenario, interestId]);
    return { data, ...useAdminDetailScenario({ scenario }) };
};

export const useAdminCityDetailData = (params?: AdminDetailParams) => {
    const scenario = params?.scenario ?? "normal";
    const cityId = params?.id;
    // TODO: GET /api/admin/cities/{cityId}
    // TODO: expected response shape: AdminCityDetail
    const USE_MOCKS = true;
    const data = useMemo<AdminCityDetail>(() => {
        if (USE_MOCKS) {
            return getAdminCityDetailMock(scenario);
        }
        return getAdminCityDetailMock(scenario);
    }, [scenario, cityId]);
    return { data, ...useAdminDetailScenario({ scenario }) };
};

export const useAdminCareerDetailData = (params?: AdminDetailParams) => {
    const scenario = params?.scenario ?? "normal";
    const careerId = params?.id;
    // TODO: GET /api/admin/careers/{careerId}
    // TODO: expected response shape: AdminCareerDetail
    const USE_MOCKS = true;
    const data = useMemo<AdminCareerDetail>(() => {
        if (USE_MOCKS) {
            return getAdminCareerDetailMock(scenario);
        }
        return getAdminCareerDetailMock(scenario);
    }, [scenario, careerId]);
    return { data, ...useAdminDetailScenario({ scenario }) };
};
