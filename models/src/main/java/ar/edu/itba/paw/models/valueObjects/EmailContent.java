package ar.edu.itba.paw.models.valueObjects;


import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import lombok.Builder;
import lombok.Value;
import lombok.NonNull;

import java.util.Locale;


//@LULO-PREGUNTAR: tecnicamente los [] son mutables, pero CREO que no pasa nada

@Value
@Builder
public class EmailContent {
    @NonNull
    String message;


//    @NonNull
//    String subjectKey;   @LULO: estaria bien ponerlo aca o tiene que ir en el email-service?

//    @NonNull
//    Object[] subjectArgs;
//
//    @NonNull
//    Image image;

}
