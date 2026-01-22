import { useMemo } from "react";
import { classNames } from "@/lib/utils/classNames";

interface AdminPaginationProps {
    totalPages: number;
    currentPage: number;
    pageSize: number;
    onPageChange: (page: number) => void;
    previousLabel: string;
    nextLabel: string;
}

export default function AdminPagination({
    totalPages,
    currentPage,
    pageSize,
    onPageChange,
    previousLabel,
    nextLabel,
}: AdminPaginationProps) {
    const { start, end } = useMemo(() => {
        const startValue = Math.max(1, currentPage - 2);
        const endValue = Math.min(totalPages, currentPage + 2);
        return { start: startValue, end: endValue };
    }, [currentPage, totalPages]);

    if (totalPages <= 1 || pageSize <= 0) {
        return null;
    }

    const handlePageClick = (page: number) => () => {
        if (page !== currentPage) {
            onPageChange(page);
        }
    };

    const canGoPrev = currentPage > 1;
    const canGoNext = currentPage < totalPages;

    return (
        <div className="pagination">
            <button
                type="button"
                className={classNames("page-link", !canGoPrev && "page-link--disabled")}
                onClick={canGoPrev ? handlePageClick(currentPage - 1) : undefined}
                disabled={!canGoPrev}
                aria-disabled={!canGoPrev}
            >
                &laquo; {previousLabel}
            </button>

            {start > 1 && (
                <>
                    <button type="button" className="page-link" onClick={handlePageClick(1)}>
                        1
                    </button>
                    <span className="page-ellipsis">...</span>
                </>
            )}

            {Array.from({ length: end - start + 1 }, (_, index) => start + index).map((page) => (
                <button
                    key={page}
                    type="button"
                    className={classNames("page-link", page === currentPage && "active")}
                    onClick={handlePageClick(page)}
                    aria-current={page === currentPage ? "page" : undefined}
                >
                    {page}
                </button>
            ))}

            {end < totalPages && (
                <>
                    <span className="page-ellipsis">...</span>
                    <button type="button" className="page-link" onClick={handlePageClick(totalPages)}>
                        {totalPages}
                    </button>
                </>
            )}

            <button
                type="button"
                className={classNames("page-link", !canGoNext && "page-link--disabled")}
                onClick={canGoNext ? handlePageClick(currentPage + 1) : undefined}
                disabled={!canGoNext}
                aria-disabled={!canGoNext}
            >
                {nextLabel} &raquo;
            </button>
        </div>
    );
}
