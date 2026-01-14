export interface PageResult<T> {
    content: T[];
    totalPages: number;
    currentPage: number;
    pageSize: number;
    totalItems: number;
}
