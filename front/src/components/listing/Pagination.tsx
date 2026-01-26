import { useMemo } from "react";
import { classNames } from "@/lib/utils/classNames";

interface PaginationProps {
    totalPages: number;
    currentPage: number;
    pageSize: number;
    nextPage?: string | null;
    lastPage?: string | null;
    prevPage?: string | null;
    firstPage?: string | null;
    onPageChange: (page: number) => void;
    previousLabel: string;
    nextLabel: string;
}

export default function Pagination({
    totalPages,
    currentPage,
    pageSize,
    onPageChange,
    previousLabel,
    nextLabel,
    nextPage, 
    lastPage,
    firstPage,
    prevPage
}: PaginationProps) {
    const { start, end } = useMemo(() => {
        const startValue = Math.max(1, currentPage - 2);
        const endValue = Math.min(totalPages, currentPage + 2);
        return { start: startValue, end: endValue };
    }, [currentPage, totalPages]);

    if (totalPages <= 1 || pageSize <= 0) {
        return null;
    }

    const handlePageClick = (page: number | string) => () => {
        if (typeof(page) === 'string'){
            console.log(page);
        }
        else if (page !== currentPage) {
            onPageChange(page);
        }
    };

    const canGoPrev: boolean = typeof(prevPage) === 'string';
    const canGoNext: boolean = typeof(nextPage) === 'string';

    return (
        <div className="pagination">
            <button
                type="button"
                className={classNames("page-link", !canGoPrev && "page-link--disabled")}
                onClick={canGoPrev ? handlePageClick(prevPage ?? "") : undefined}
                disabled={!canGoPrev}
                aria-disabled={!canGoPrev}
            >
                &laquo; {previousLabel}
            </button>

            {start > 1 && (
                <>
                    <button type="button" className="page-link" onClick={handlePageClick(firstPage ?? "")}>
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
                    <button type="button" className="page-link" onClick={handlePageClick(lastPage ?? "")}>
                        {totalPages}
                    </button>
                </>
            )}

            <button
                type="button"
                className={classNames("page-link", !canGoNext && "page-link--disabled")}
                onClick={canGoNext ? handlePageClick(nextPage ?? "") : undefined}
                disabled={!canGoNext}
                aria-disabled={!canGoNext}
            >
                {nextLabel} &raquo;
            </button>
        </div>
    );
}
