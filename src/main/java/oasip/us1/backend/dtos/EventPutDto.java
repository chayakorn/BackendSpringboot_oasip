package oasip.us1.backend.dtos;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Data
public class EventPutDto implements Serializable {

    @NotNull(message = "eventStartTime can't be null")
    @Future(message = "eventStartTime must be future")
    private Instant eventStartTime;

    @Size(max = 500,message = "eventNotes must be between 0-500 characters")
    private String eventNotes;

}
