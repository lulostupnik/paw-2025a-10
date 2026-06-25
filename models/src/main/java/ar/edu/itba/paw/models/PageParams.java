package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.exceptions.InvalidPaginationParamsException;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class PageParams {
    private static final int MAX_PAGE_SIZE = 100;
    private final int page;
    private final int size;

    public PageParams(int page, int size){
        if(page <= 0 || size <= 0){
            throw new InvalidPaginationParamsException("Pagination variables can't be negative");
        }
        this.page = page;
        this.size = Math.min(size, MAX_PAGE_SIZE);
    }

    @Override
    public boolean equals(Object other){
        return ((other instanceof PageParams p) && p.getPage() == this.page && p.getSize() == this.size);
    }
    @Override
    public int hashCode() {
        return Objects.hash(page, size);
    }
}
