package com.tinqinacademy.authentication.persistence.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoleType {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    UNKNOWN("");

    private final String type;

    RoleType(String type) {
        this.type = type;
    }

    public static RoleType fromType(String type) {
        return Arrays.stream(RoleType.values())
                .filter(roleType -> roleType.type.equals(type))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
