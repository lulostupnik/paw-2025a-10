package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
public class Page<T> {

    private final List<T> content;
    private final int currentPage;
    private final int pageSize;
    private final long totalElements;

    public int getTotalPages() {
        return pageSize == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);
    }

}