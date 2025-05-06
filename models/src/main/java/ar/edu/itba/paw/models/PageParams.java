package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;

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
}
