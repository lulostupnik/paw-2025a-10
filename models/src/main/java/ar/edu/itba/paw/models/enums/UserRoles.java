package ar.edu.itba.paw.models.enums;

public enum UserRoles {
    USER("user"),
    ADMIN("admin");

    private final String roleName;

    UserRoles(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }
}
