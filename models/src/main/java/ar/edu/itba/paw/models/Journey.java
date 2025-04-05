package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class Journey{
    private final long id;
    private final User user;
    private final String destinationCity;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final University destinationUniversity; // FIXME: Cambiar por String -> lo obtenemos del toString();
    private final String description;
}

