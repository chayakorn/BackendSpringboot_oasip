package oasip.us1.backend.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "eventCategory")
public class EventCategory {
    @Id
    @Column(name = "eventCategoryId", nullable = false)
    private Integer id;

    @NotNull(message = "eventCategoryName can not be null")
    @NotBlank(message = "eventCategoryName can not be blank")
    @Size(max = 100 , message = "eventCategoryName must be between 1-100 characters")
    @Column(name = "eventCategoryName", nullable = false, length = 100)
    private String eventCategoryName;

    @Size(max = 500 , message = "evetnCategoryDescription must be between 0-500 chracters")
    @Column(name = "eventCategoryDescription", length = 500)
    private String eventCategoryDescription;

    @NotNull(message = "eventDuraion can not be null")
    @Min(value = 1 ,message = "minimum eventDuration value is 1")
    @Max(value = 480 ,message = "max eventDuration value is 480")
    @Column(name = "eventDuration", nullable = false)
    private Integer eventDuration;

    @Column(name = "image")
    private String image;

//    @OneToMany(mappedBy = "eventCategoryId")
//    private Set<Event> events = new LinkedHashSet<>();
}