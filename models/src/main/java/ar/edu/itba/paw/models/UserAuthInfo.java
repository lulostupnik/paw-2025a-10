package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class UserAuthInfo  {
    private final String email;
    private final String password;
    private final String role;
    private final boolean blocked;
    private final boolean verified;
}

