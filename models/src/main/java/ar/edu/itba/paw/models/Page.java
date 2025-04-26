package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Page<T> {

    private final List<T> content;
    private final int currentPage;
    // private final int pageSize; // Esto ya lo tiene List, no?
    // private final int totalPages;
}