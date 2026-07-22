import type { AxiosResponse } from "axios";

export interface PageResult<T> {
    content: T[];
    next: string | null;
    prev: string | null;
    first: string | null;
    last: string | null;
    totalPages: number;
    currentPage: number;
    pageSize: number;
    totalElements: number;
}

export function toPaged<T>(response: AxiosResponse<T[]>): PageResult<T> {
    const data = Array.isArray(response.data) ? response.data : [];
    const headers = response.headers ?? {};
    const rawLinkHeader = headers["link"];
    const linkHeader = Array.isArray(rawLinkHeader) ? rawLinkHeader[0] : rawLinkHeader;
    const linkMap: { [key: string]: string } = {};

    if (typeof linkHeader === "string" && linkHeader.length > 0) {
        linkHeader
            .replace(/[<>"]/g, "")
            .split(", ")
            .map((entry) =>
                entry.split("; ").map((token) => (token.includes("rel") ? token.split("=")[1] : token))
            )
            .forEach((entry) => {
                if (entry.length >= 2) {
                    linkMap[entry[1]] = entry[0];
                }
            });
    }

    const getPageFromLink = (value: string | null | undefined) => {
        if (!value) {
            return null;
        }
        try {
            const parsed = new URL(
                value,
                typeof window !== "undefined" ? window.location.origin : "http://localhost"
            );
            const pageValue = Number(parsed.searchParams.get("page") ?? "");
            return Number.isFinite(pageValue) ? pageValue : null;
        } catch {
            return null;
        }
    };

    const currentPageParam = Number(response.config?.params?.["page"] ?? "");
    const currentPage = Number.isFinite(currentPageParam) && currentPageParam > 0 ? currentPageParam : 1;

    const pageSizeParam = Number(response.config?.params?.["size"] ?? "");
    const pageSize =
        Number.isFinite(pageSizeParam) && pageSizeParam > 0
            ? pageSizeParam
            : data.length;

    const lastPage = getPageFromLink(linkMap["last"]);
    const totalPages = lastPage ?? 0;

    const rawTotalCount = headers["x-total-count"];
    const totalCountHeaderValue = Array.isArray(rawTotalCount) ? rawTotalCount[0] : rawTotalCount;
    const parsedTotalElements = Number(totalCountHeaderValue ?? "");

    const roughEstimatedTotalElements =
        totalPages > 0
            ? currentPage < totalPages
                ? totalPages * pageSize
                : (totalPages - 1) * pageSize + data.length
            : data.length;
    const estimatedTotalElements =
        totalPages > 0 ? Math.max(1, roughEstimatedTotalElements) : roughEstimatedTotalElements;
    const totalElements =
        Number.isFinite(parsedTotalElements) && parsedTotalElements >= 0
            ? parsedTotalElements
            : estimatedTotalElements;

    return {
        content: data,
        next: linkMap["next"] ?? null,
        last: linkMap["last"] ?? null,
        first: linkMap["first"] ?? null,
        prev: linkMap["prev"] ?? null,
        totalPages,
        currentPage,
        pageSize,
        totalElements,
    };
}

export function mapPageList<T, V>(page: PageResult<V>, newList: T[]): PageResult<T>{
    return {
        content: Array.isArray(newList) ? newList : [],
        next: page.next,
        last: page.last,
        first: page.first,
        prev: page.prev,
        totalPages: page.totalPages,
        currentPage: page.currentPage,
        pageSize: page.pageSize,
        totalElements: page.totalElements,
    };
}

export function emptyPage<T>(): PageResult<T>{
    return {
        content: [],
        next: null,
        last: null,
        first: null,
        prev: null,
        totalPages: 0,
        currentPage: 0,
        pageSize: 0,
        totalElements: 0,
    };
}
