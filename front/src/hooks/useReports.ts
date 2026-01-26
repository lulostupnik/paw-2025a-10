import { useMemo } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { listReports, resolveReportListItem, type ListReportsParams, type ReportListItem } from "@/lib/api/reports";
import { emptyPage, mapPageList, type PageResult } from "@/types/pagination";

interface UseReportsParams {
    search?: string;
    page?: number;
    pageSize?: number;
}

interface UseReportsResult {
    data: PageResult<ReportListItem>;
    isLoading: boolean;
    isError: boolean;
}

export const useReports = ({ search = "", page = 1, pageSize = 10 }: UseReportsParams = {}): UseReportsResult => {
    const safePage = Math.max(1, page);
    const safePageSize = Math.max(1, pageSize);
    const params = useMemo<ListReportsParams>(
        () => ({
            search: search.trim() || undefined,
            page: safePage,
            size: safePageSize,
        }),
        [safePage, safePageSize, search]
    );

    const query = useQuery({
        queryKey: ["reports", params],
        queryFn: async ({ signal }) => {
            const reports = await listReports(params, signal);
            const reportItems = await Promise.all(reports.content.map((report) => resolveReportListItem(report, signal)));
            return mapPageList(reports, reportItems);
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data ?? emptyPage(),
        isLoading: query.isLoading,
        isError: query.isError,
    };
};
