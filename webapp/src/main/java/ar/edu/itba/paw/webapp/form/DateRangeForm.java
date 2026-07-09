package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;

/** Forms con rango de fechas validable por {@code @ValidDateRange}. */
public interface DateRangeForm {

    LocalDate getStartDate();

    LocalDate getEndDate();
}
