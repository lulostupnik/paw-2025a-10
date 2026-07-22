import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import Pagination from "@/components/listing/Pagination";

function renderPagination(overrides: Partial<React.ComponentProps<typeof Pagination>> = {}) {
    const props: React.ComponentProps<typeof Pagination> = {
        totalPages: 5,
        currentPage: 1,
        pageSize: 10,
        nextPage: "/next",
        lastPage: "/last",
        prevPage: "/prev",
        firstPage: "/first",
        onPageChange: vi.fn(),
        previousLabel: "Prev",
        nextLabel: "Next",
        ...overrides,
    };
    return render(<Pagination {...props} />);
}

describe("Pagination page-window logic", () => {
    it("renders nothing when there is a single page", () => {
        const { container } = renderPagination({ totalPages: 1 });
        expect(container.firstChild).toBeNull();
    });

    it("renders nothing when the page size is non-positive", () => {
        const { container } = renderPagination({ totalPages: 5, pageSize: 0 });
        expect(container.firstChild).toBeNull();
    });

    it("shows every page and no ellipsis when totalPages fits the window (<=7)", () => {
        renderPagination({ totalPages: 5, currentPage: 3 });
        for (const page of ["1", "2", "3", "4", "5"]) {
            expect(screen.getByRole("button", { name: page })).toBeInTheDocument();
        }
        expect(screen.queryAllByText("...")).toHaveLength(0);
    });

    it("shows first, last and two ellipses when the current page is in the middle of many pages", () => {
        renderPagination({ totalPages: 20, currentPage: 10 });
        expect(screen.getByRole("button", { name: "1" })).toBeInTheDocument();
        expect(screen.getByRole("button", { name: "20" })).toBeInTheDocument();
        expect(screen.queryAllByText("...")).toHaveLength(2);
        expect(screen.getByRole("button", { name: "10" })).toHaveClass("active");
    });

    it("omits the leading ellipsis when the current page is at the start", () => {
        renderPagination({ totalPages: 20, currentPage: 1 });
        expect(screen.queryAllByText("...")).toHaveLength(1);
        expect(screen.getByRole("button", { name: "1" })).toHaveClass("active");
        expect(screen.getByRole("button", { name: "20" })).toBeInTheDocument();
    });
});
