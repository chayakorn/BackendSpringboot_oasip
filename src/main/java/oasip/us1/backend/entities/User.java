package oasip.us1.backend.entities;

import lombok.Data;
import oasip.us1.backend.enums.ROLE;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "user", indexes = {
        @Index(name = "email_UNIQUE", columnList = "email", unique = true),
        @Index(name = "name_UNIQUE", columnList = "name", unique = true)
})
@Data
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    private Integer id;

    @NotBlank(message = "name can not be null or blank.")
    @Size(max = 100, message = "name must be between 1-100 characters.")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Email(message = "email must be a valid form.")
    @NotBlank(message = "email can not be null or blank.")
    @Size(max = 50, message = "email must be between 1-100 characters.")
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @NotNull(message = "password can not be null.")
    @Size(max = 90, message = "encoding password must be between 1-100 characters.")
    @Column(name = "password", nullable = false, length = 90)
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "role can not be null.")
    @Column(name = "role", nullable = false)
    private ROLE role = ROLE.student;

    @Column(name = "createdOn", nullable = false, insertable = false, updatable = false)
    private Instant createdOn;

    @Column(name = "updatedOn", nullable = false, insertable = false, updatable = false)
    private Instant updatedOn;
}