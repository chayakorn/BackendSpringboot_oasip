package oasip.us1.backend.services;

import oasip.us1.backend.dtos.*;
import oasip.us1.backend.entities.Event;
import oasip.us1.backend.repositories.EventRepository;
import oasip.us1.backend.repositories.EventcategoryRepository;
import oasip.us1.backend.utils.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventcategoryRepository eventcategoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;


    public EventPageDto getAllEventByCatId(int page, int pageSize, String sortBy, boolean isAsc, Collection<String> catid, String uap) {
        if (uap.equals("p")) {
            return modelMapper.map(eventRepository.findByCategoryIdPast(catid,
                            PageRequest.of(page, pageSize, isAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.from(ZoneOffset.UTC)).format(Instant.now())),
                    EventPageDto.class);
        }
        if (uap.equals("u")) {
            return modelMapper.map(eventRepository.findByCategoryIdUpcomming(catid,
                            PageRequest.of(page, pageSize, isAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.from(ZoneOffset.UTC)).format(Instant.now())),
                    EventPageDto.class);
        }
        return modelMapper.map(eventRepository.findByCategoryId(catid,
                        PageRequest.of(page, pageSize, isAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())),
                EventPageDto.class);
    }

    public ResponseEntity getEventById(int id, WebRequest request) {
        Map<String, String> fieldError = new HashMap<>();
        if (eventRepository.findById(id).isEmpty()) {
            fieldError.put("eventBookingId", "eventBooking not found");
            ErrorDTO errorDTO = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.NOT_FOUND.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorDTO, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(modelMapper.map(eventRepository.getById(id), EventDto.class), HttpStatus.OK);
    }

    public ResponseEntity getCategoryByEventId(int eventid, WebRequest request) {
        Map<String, String> fieldError = new HashMap<>();
        if (eventRepository.findById(eventid).isEmpty()) {
            fieldError.put("eventBookingId", "eventBooking not found");
            ErrorDTO errorDTO = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.NOT_FOUND.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorDTO, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(modelMapper.map(eventRepository.getById(eventid).getEventCategoryId(), EventcategoryDto.class), HttpStatus.OK);

    }

    public ResponseEntity getEventByCatAndDate(int catid, String date, String offSet, boolean negative, WebRequest request) {
        Map<String, String> fieldError = new HashMap<>();
        if (date.trim().isBlank()) {
            fieldError.put("date", "date can not be null");
            ErrorDTO errorDTO = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.NOT_FOUND.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorDTO, HttpStatus.BAD_REQUEST);
        }
        if (negative) {
            return new ResponseEntity(listMapper.mapList(eventRepository.findByEventStartTimeAndEventCategoryIdForMinus(catid, date, offSet),
                    EventDto.class, modelMapper), HttpStatus.OK);
        }
        return new ResponseEntity(listMapper.mapList(eventRepository.findByEventStartTimeAndEventCategoryIdForPlus(catid, date, offSet),
                EventDto.class, modelMapper), HttpStatus.OK);
    }

    public ResponseEntity getEventByDate(String date, String offSet, boolean negative, int page, int pageSize, String sortBy,
                                         boolean isAsc, WebRequest request) {
        Map<String, String> fieldError = new HashMap<>();
        if (date.trim().isBlank()) {
            fieldError.put("date", "date can not be null");
            ErrorDTO errorDTO = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.NOT_FOUND.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorDTO, HttpStatus.BAD_REQUEST);
        }
        if (negative) {
            return new ResponseEntity(modelMapper.map(eventRepository.findByDateForMinus(date, offSet,
                            PageRequest.of(page, pageSize, isAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())),
                    EventPageDto.class), HttpStatus.OK);
        }
        return new ResponseEntity(modelMapper.map(eventRepository.findByDateForPlus(date, offSet,
                        PageRequest.of(page, pageSize, isAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending())),
                EventPageDto.class), HttpStatus.OK);
    }

    public ResponseEntity save(EventPostDto event, BindingResult bindingResult, WebRequest request) {
        Map<String, String> fieldError = new HashMap<>();

        bindingResult.getAllErrors().forEach((error) -> {
            fieldError.put(((FieldError) error).getField(), error.getDefaultMessage());
        });
        if (!fieldError.containsKey("eventDuration") && !fieldError.containsKey("eventStartTime") &&
                !fieldError.containsKey("eventCategoryId")) {
            if (!eventRepository.findByEventStartTimeBetween(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId
                            .from(ZoneOffset.UTC)).format(event.getEventStartTime().plus(1, ChronoUnit.SECONDS)),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.from(ZoneOffset.UTC))
                            .format(event.getEventStartTime().plus(event.getEventDuration(),
                                    ChronoUnit.MINUTES).minus(1, ChronoUnit.SECONDS)),
                    event.getEventCategoryId()).isEmpty() && event.getEventCategoryId() != null) {
                fieldError.put("TimeOverlap", "your selected time is not available");
            }
        }
        if (fieldError.size() == 0) {
            Event insertevent = mapEventForInsert(event);
            return ResponseEntity.status(201).body(modelMapper.map(eventRepository.saveAndFlush(insertevent), EventDto.class));
        }
        ErrorDTO errorBody = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.BAD_REQUEST.value(),
                ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
        return new ResponseEntity(errorBody, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity update(EventPutDto updateEvent, int bookingid, WebRequest request) {
        Map<String, String> fieldError = new HashMap<>();
        if (eventRepository.findById(bookingid).isEmpty()) {
            fieldError.put("eventBookingId", "eventBooking not found");
            ErrorDTO errorDTO = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.NOT_FOUND.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorDTO, HttpStatus.NOT_FOUND);
        }
        Event event = eventRepository.findById(bookingid).get();
        if (updateEvent.getEventStartTime() == null) {
            fieldError.put("eventStartTime", "eventStartTime can not be null");
        }
        if (fieldError.isEmpty()) {
            if (updateEvent.getEventStartTime().isBefore(Instant.now())) {
                fieldError.put("eventStartTime", "eventStartTime can not be past");
            }
            if (!eventRepository.findByEventStartTimeBetweenForPut(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId
                            .from(ZoneOffset.UTC)).format(updateEvent.getEventStartTime()),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.from(ZoneOffset.UTC))
                            .format(updateEvent.getEventStartTime().plus(event.getEventDuration(), ChronoUnit.MINUTES)),
                    event.getEventCategoryId().getId(), event.getId()).isEmpty()) {
                fieldError.put("TimeOverlap", "your selected time is not available");
            }
        }
        if (updateEvent.getEventNotes() != null && updateEvent.getEventNotes().length() > 500) {
            fieldError.put("eventNotes", "eventNotes must be between 0-500");
        }
        if (fieldError.isEmpty()) {
            event = eventRepository.findById(bookingid).map(eventbooking1 -> mapEvent(eventbooking1, updateEvent, bookingid)).get();
            return ResponseEntity.status(200).body(modelMapper.map(eventRepository.saveAndFlush(event), EventDto.class));
        }
        ErrorDTO errorBody = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.BAD_REQUEST.value(),
                ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
        return new ResponseEntity(errorBody, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity delete(int id, WebRequest request) {
        Map<String, String> fieldError = new HashMap<>();
        if (eventRepository.findById(id).isEmpty()) {
            fieldError.put("eventBookingId", "eventBooking not found");
            ErrorDTO errorDTO = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.NOT_FOUND.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorDTO, HttpStatus.NOT_FOUND);
        }
        eventRepository.deleteById(id);
        return new ResponseEntity("Deleted: " + id, HttpStatus.OK);
    }

    private Event mapEvent(Event existingEvent, EventPutDto updateEvent, int bookingid) {
        existingEvent.setId(bookingid);
        existingEvent.setEventNotes(updateEvent.getEventNotes());
        existingEvent.setEventStartTime(updateEvent.getEventStartTime());
        return existingEvent;
    }

    private Event mapEventForInsert(EventPostDto insertDto) {
        Event eventbooking = new Event();
        eventbooking.setBookingName(insertDto.getBookingName());
        eventbooking.setEventDuration(insertDto.getEventDuration());
        eventbooking.setEventNotes(insertDto.getEventNotes());
        eventbooking.setEventStartTime(insertDto.getEventStartTime());
        eventbooking.setEventCategoryId(eventcategoryRepository.findById(insertDto.getEventCategoryId()).get());
        eventbooking.setBookingEmail(insertDto.getBookingEmail());
        return eventbooking;
    }
}
