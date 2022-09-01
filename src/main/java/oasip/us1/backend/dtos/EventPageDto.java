package oasip.us1.backend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class EventPageDto {
    private List<EventDto> content;
    private int number;
    private int size;
    private int totalPages;
    private int numberOfElements;
    private int totalElements;
    private boolean last;
    private boolean first;
}

