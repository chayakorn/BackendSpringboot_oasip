package oasip.us1.backend.dtos;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.time.Instant;

@Data
public class EventPostDto {

    @Column(name = "bookingName", nullable = false, length = 100)
    @NotBlank(message = "bookingName can't be null or blank")
    @Size(max = 100 , message = "bookingName must be between 1-100 characters")
    private String bookingName;

    @Email(message = "email must be valid form")
    @NotBlank(message = "email is mendatory")
    @Size(max = 100 , message = "email must be between 1-100 characters")
    private String bookingEmail;

    @Column(name = "eventStartTime", nullable = false)
    @NotNull(message = "eventStartTime can't be null")
    @Future(message = "eventStartTime must be future")
    private Instant eventStartTime;

    @NotNull(message = "eventCategoryId can not be null")
    private Integer eventCategoryId;

    @NotNull(message = "eventDuration can not be null")
    private Integer eventDuration;

    @Column(name = "eventNotes", length = 500)
    private String eventNotes;
}
