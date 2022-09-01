package oasip.us1.backend.dtos;

import lombok.Data;
import oasip.us1.backend.enums.ROLE;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

@Data
public class UserDto implements Serializable {
    private Integer id;
    @NotBlank(message = "name can not be null or blank.")
    @Size(max = 100, message = "name must be between 1-100 characters.")
    private String name;
    @Email(message = "email must be a valid form.")
    @NotBlank(message = "email can not be null or blank.")
    @Size(max = 50, message = "email must be between 1-100 characters.")
    private String email;
    @NotNull(message = "role can not be null.")
    private ROLE role;
    private Instant createdOn;
    private Instant updatedOn;
}
