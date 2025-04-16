package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class Journey{
    private final long id;
    private final User user;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final University destinationUniversity; // FIXME: Cambiar por String -> lo obtenemos del toString();
    private final String description;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{journeyId: ");
        sb.append(id);
        sb.append(", user: ");
        sb.append(user);
        sb.append(", destinationUniversity: ");
        sb.append(destinationUniversity);
        sb.append(", startDate: \"");
        sb.append(startDate);
        sb.append("\", endDate: \"");
        sb.append(endDate);
        sb.append("\", description: \"");
        sb.append(description);
        sb.append("\"}");
        return sb.toString();
    }
}

