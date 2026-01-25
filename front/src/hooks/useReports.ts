import { useMemo } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { PagedResult } from "@/types/admin";
import { listReports, resolveReportListItem, type ListReportsParams, type ReportListItem } from "@/lib/api/reports";

interface UseReportsParams {
    search?: string;
    page?: number;
    pageSize?: number;
}

interface UseReportsResult {
    data: PagedResult<ReportListItem>;
    isLoading: boolean;
    isError: boolean;
}

const getTotalPages = (currentPage: number, pageSize: number, count: number) => {
    if (count < pageSize) {
        return currentPage;
    }
    return currentPage + 1;
};

export const useReports = ({ search = "", page = 1, pageSize = 10 }: UseReportsParams = {}): UseReportsResult => {
    const safePage = Math.max(1, page);
    const safePageSize = Math.max(1, pageSize);
    const params = useMemo<ListReportsParams>(
        () => ({
            search: search.trim() || undefined,
            page: safePage - 1,
            size: safePageSize,
        }),
        [safePage, safePageSize, search]
    );

    const query = useQuery({
        queryKey: ["reports", params],
        queryFn: async ({ signal }) => {
            const reports = await listReports(params, signal);
            return Promise.all(reports.map((report) => resolveReportListItem(report, signal)));
        },
        placeholderData: keepPreviousData,
    });

    const content = query.data ?? [];
    const totalPages = getTotalPages(safePage, safePageSize, content.length);

    return {
        data: {
            content,
            totalPages,
            currentPage: safePage,
            pageSize: safePageSize,
            totalItems: content.length,
        },
        isLoading: query.isLoading,
        isError: query.isError,
    };
};
