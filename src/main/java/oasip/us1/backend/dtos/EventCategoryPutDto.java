package oasip.us1.backend.dtos;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class EventCategoryPutDto {
    @NotNull(message = "eventCategoryName can not be null")
    @NotBlank(message = "eventCategoryName can not be blank")
    @Size(max = 100 , message = "eventCategoryName must be between 1-100 characters")
    private String eventCategoryName;

    @Size(max = 500 , message = "evetnCategoryDescription must be between 0-500 chracters")
    private String eventCategoryDescription;

    @NotNull(message = "eventDuraion can not be null")
    @Min(value = 1 ,message = "minimum eventDuration value is 1")
    @Max(value = 480 ,message = "max eventDuration value is 480")
    private Integer eventDuration;
}
