package ar.edu.itba.paw.models;

import lombok.Getter;

@Getter
public class PageParams {
    private final int page;
    private final int size;

    public PageParams(int page, int size){
        if(page <= 0 || size <= 0){
            throw new RuntimeException("Pagination variables can't be negative");
        }
        this.page = page;
        this.size = size;
    }

    @Override
    public boolean equals(Object other){
        return ((other instanceof PageParams p) && p.getPage() == this.page && p.getSize() == this.size);
    }
}
