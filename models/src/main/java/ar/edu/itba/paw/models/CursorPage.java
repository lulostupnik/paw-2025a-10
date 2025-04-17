package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CursorPage<T, C> {
    private final List<T> items;
    private final C nextCursor;
    // private final boolean hasNext;
}
