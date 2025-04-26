package ar.edu.itba.paw.models.valueObjects;


import ar.edu.itba.paw.models.Image;
import lombok.Builder;
import lombok.Value;
import lombok.NonNull;

import java.util.Locale;


//@SOTUYO-PREGUNTAR: tecnicamente los [] son mutables, pero CREO que no pasa nada

@Value
@Builder
public class EmailContent {
    @NonNull
    String message;

    @NonNull
    String subjectKey;

    @NonNull
    Object[] subjectArgs;

    @NonNull
    Image image;

}
