package com.tinqinacademy.authentication.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {
    @Id
    private String token;

    @Column(nullable = false)
    private Date expiration;
}
