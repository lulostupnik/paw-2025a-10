package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class User{
    private final long id;
    private final String email;
    private final String username;
    // private final String password;
    private final String firstname;
    private final String lastname;
    private final University university; // FIXME: ¿Cambiar por String? -> lo obtenemos del toString();
    private final Career career;
    private final long profilePictureId;
}