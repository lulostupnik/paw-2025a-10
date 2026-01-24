import type { PagedResult } from "@/types/admin";

export const buildPagedResult = <T>(items: T[], page: number, pageSize: number): PagedResult<T> => {
    const safePageSize = Math.max(1, pageSize);
    const totalItems = items.length;
    const totalPages = items.length < safePageSize ? page : page + 1;
    const safePage = Math.min(Math.max(page, 1), Math.max(totalPages, 1));

    return {
        content: items,
        totalPages,
        currentPage: safePage,
        pageSize: safePageSize,
        totalItems,
    };
};
