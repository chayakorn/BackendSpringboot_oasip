package oasip.us1.backend.dtos;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class UserMatchingPasswordDto implements Serializable {
    @Email(message = "email must be a valid form.")
    @NotBlank(message = "email can not be null or blank.")
    @NotNull(message = "email can not be null.")
    @Size(max = 50, message = "email must be between 1-100 characters.")
    private final String email;
    @NotBlank(message = "password can not be null or blank.")
    @NotNull(message = "password can not be null.")
    @Size(min = 8, max = 14, message = "password must be between 8-14 characters.")
    private final String password;
}
