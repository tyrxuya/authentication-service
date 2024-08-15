package com.tinqinacademy.authentication.persistence.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoleType {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    UNKNOWN("");

    private final String role;

    RoleType(String role) {
        this.role = role;
    }

    public static RoleType fromRole(String role) {
        return Arrays.stream(RoleType.values())
                .filter(roleType -> roleType.role.equals(role))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
