package oasip.us1.backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Data
public class EventDto implements Serializable {
    private Integer id;
    private String bookingName;
    private String bookingEmail;
    private Instant eventStartTime;
    private Integer eventCategoryId;
    private String categoryName;
    private Instant eventDuration;
    private String eventNotes;
    @JsonIgnore
    private EventcategoryDto eventCategory;

    public String getCategoryName() {
        return eventCategory.getEventCategoryName();
    }
}
