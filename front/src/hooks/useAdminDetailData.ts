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
}

const useAdminDetailScenario = ({ scenario = "normal" }: AdminDetailParams = {}) => {
    return {
        isLoading: scenario === "loading",
        isError: scenario === "error",
    };
};

export const useAdminUserDetailData = (params?: AdminDetailParams) => {
    const scenario = params?.scenario ?? "normal";
    // TODO: replace mock user detail with API fetch + caching + error handling.
    const data = useMemo<AdminUserDetail>(() => getAdminUserDetailMock(scenario), [scenario]);
    return { data, ...useAdminDetailScenario({ scenario }) };
};

export const useAdminUniversityDetailData = (params?: AdminDetailParams) => {
    const scenario = params?.scenario ?? "normal";
    // TODO: replace mock university detail with API fetch + caching + error handling.
    const data = useMemo<AdminUniversityDetail>(() => getAdminUniversityDetailMock(scenario), [scenario]);
    return { data, ...useAdminDetailScenario({ scenario }) };
};

export const useAdminInterestDetailData = (params?: AdminDetailParams) => {
    const scenario = params?.scenario ?? "normal";
    // TODO: replace mock interest detail with API fetch + caching + error handling.
    const data = useMemo<AdminInterestDetail>(() => getAdminInterestDetailMock(scenario), [scenario]);
    return { data, ...useAdminDetailScenario({ scenario }) };
};

export const useAdminCityDetailData = (params?: AdminDetailParams) => {
    const scenario = params?.scenario ?? "normal";
    // TODO: replace mock city detail with API fetch + caching + error handling.
    const data = useMemo<AdminCityDetail>(() => getAdminCityDetailMock(scenario), [scenario]);
    return { data, ...useAdminDetailScenario({ scenario }) };
};

export const useAdminCareerDetailData = (params?: AdminDetailParams) => {
    const scenario = params?.scenario ?? "normal";
    // TODO: replace mock career detail with API fetch + caching + error handling.
    const data = useMemo<AdminCareerDetail>(() => getAdminCareerDetailMock(scenario), [scenario]);
    return { data, ...useAdminDetailScenario({ scenario }) };
};
