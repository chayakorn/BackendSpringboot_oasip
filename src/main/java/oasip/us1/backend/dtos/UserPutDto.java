package oasip.us1.backend.dtos;

import lombok.Data;
import oasip.us1.backend.enums.ROLE;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class UserPutDto implements Serializable {
    @NotNull(message = "name can not be null.")
    @NotBlank(message = "name can not be null or blank.")
    @Size(max = 100, message = "name must be between 1-100 characters.")
    private String name;

    @NotNull(message = "email can not be null.")
    @NotBlank(message = "email can not be null or blank.")
    @Size(max = 50, message = "email must be between 1-100 characters.")
    private String email;

    @NotNull(message = "role can not be null")
    private ROLE role;
}
