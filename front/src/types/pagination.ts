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
}

export function toPaged<T>(response: AxiosResponse<T[], any, {}>): PageResult<T> {
    const links: string = response.headers['link'].replace(/[<>"]/g, "");
    const linkMap: {[key: string]: string} = {};
    links.split(', ')
        .map(entry => entry.split("; ")
            .map(entry => entry.includes("rel") ? entry.split("=")[1] : entry)
        )
        .forEach(entry => linkMap[entry[1]] = entry[0]);
    const pages = parseInt(linkMap['last'].split("page=")[1].split("&")[0]) ?? 0;
    const currentPage = parseInt(response.config.params['page']) ?? 0;
    const pageSize = parseInt(response.config.params['size']) ?? 0;
    return {
        content: Array.isArray(response.data) ? response.data : [],
        next: linkMap['next'] ?? null,
        last: linkMap['last'] ?? null,
        first: linkMap['first'] ?? null, 
        prev: linkMap['prev'] ?? null,
        totalPages: pages,
        currentPage,
        pageSize
    }
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
        pageSize: page.pageSize
    }
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
        pageSize: 0
    }
}