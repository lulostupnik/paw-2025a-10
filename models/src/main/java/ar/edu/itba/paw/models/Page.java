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
    private final int totalPages;
}