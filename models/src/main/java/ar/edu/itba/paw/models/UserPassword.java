package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;


@Getter
@RequiredArgsConstructor
public class UserPassword{
//    private final long id;
    private final String email;
//    private final String username;
//    private final String firstname;
//    private final String lastname;
    private final String password;
    private final String role;
}
