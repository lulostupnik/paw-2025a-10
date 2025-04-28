package ar.edu.itba.paw.models.valueObjects;


import lombok.Builder;
import lombok.Value;
import lombok.NonNull;

import java.util.Locale;


//@LULO-PREGUNTAR: tecnicamente los [] son mutables, pero CREO que no pasa nada

@Value
@Builder
public class EmailRecipient {

    @NonNull
    String toEmail;

    @NonNull
    Locale locale;


}
