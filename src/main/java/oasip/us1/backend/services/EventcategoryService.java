package oasip.us1.backend.services;

import oasip.us1.backend.dtos.ErrorDTO;
import oasip.us1.backend.dtos.EventCategoryPutDto;
import oasip.us1.backend.dtos.EventDto;
import oasip.us1.backend.dtos.EventcategoryDto;
import oasip.us1.backend.entities.EventCategory;
import oasip.us1.backend.repositories.EventRepository;
import oasip.us1.backend.repositories.EventcategoryRepository;
import oasip.us1.backend.utils.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventcategoryService {
    @Autowired
    private EventcategoryRepository eventcategoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ListMapper listMapper;


    public List<EventCategory> getAllEventCategory(){
        return eventcategoryRepository.findAll();
    }
    public ResponseEntity getEventCategoryById(int id,WebRequest request){
        Map<String,String> fieldError = new HashMap<>();
        if (eventcategoryRepository.findById(id).isEmpty()){
            fieldError.put("categoryId","categoryNotfound");
            ErrorDTO errorDTO = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.NOT_FOUND.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorDTO,HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(modelMapper.map(eventcategoryRepository.findById(id).get(), EventcategoryDto.class),HttpStatus.OK);
    }

    public ResponseEntity getEventByCategoryId(int id,WebRequest request){
        Map<String,String> fieldError = new HashMap<>();
        if (eventcategoryRepository.findById(id).isEmpty()){
            fieldError.put("categoryId","categoryNotfound");
            ErrorDTO errorDTO = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.NOT_FOUND.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorDTO,HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(listMapper.mapList(eventRepository.findEventByEventCategoryId(eventcategoryRepository.getById(id)),
                EventDto.class,modelMapper),HttpStatus.OK);
    }

    public ResponseEntity update(EventCategoryPutDto updateEventCategory, BindingResult result, int id , WebRequest request){
        Map<String,String> fieldError = new HashMap<>();
        result.getAllErrors().forEach((err)->{
            fieldError.put(((FieldError) err).getField(),err.getDefaultMessage());
        });
        eventcategoryRepository.findAll().forEach((eventcategory)->{
            if(eventcategory.getEventCategoryName().toLowerCase().trim()
                    .equals(updateEventCategory.getEventCategoryName().toLowerCase().trim()) && eventcategory.getId() != id){
                fieldError.put("eventCategoryName","eventCategoryName must be unique");
            }
        });
        if (fieldError.size()> 0){
            ErrorDTO errorBody = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.BAD_REQUEST.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorBody,HttpStatus.BAD_REQUEST);
        }
        EventCategory event = eventcategoryRepository.findById(id).get();
        updateEventCategory.setEventCategoryName(updateEventCategory.getEventCategoryName().trim());
        modelMapper.map(updateEventCategory,event);
        return new ResponseEntity(modelMapper.map(eventcategoryRepository.saveAndFlush(event),EventcategoryDto.class),HttpStatus.OK);
    }
    public ResponseEntity delete(int id,WebRequest request){
        Map<String,String> fieldError = new HashMap<>();
        if (eventcategoryRepository.findById(id).isEmpty()){
            fieldError.put("categoryId","categoryNotfound");
            ErrorDTO errorDTO = new ErrorDTO(Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toString(), HttpStatus.NOT_FOUND.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorDTO,HttpStatus.NOT_FOUND);
        }
        eventcategoryRepository.deleteById(id);
        return new ResponseEntity("Deleted id "+id, HttpStatus.OK);
    }
}
