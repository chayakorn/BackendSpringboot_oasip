package oasip.us1.backend.exceptions;

import oasip.us1.backend.dtos.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandle extends Exception {
//    @ResponseStatus
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ErrorDTO handleEnumException(HttpMessageNotReadableException hException, WebRequest request){
//        Map<String, String> fieldError = new HashMap<>();
//        fieldError.put("role", "The role can only be admin, lecturer, or student.");
//        ErrorDTO errorBody = new ErrorDTO(OffsetDateTime.now(ZoneOffset.UTC).toString(), HttpStatus.BAD_REQUEST.value(),
//                ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
//        return errorBody;
//    }
}
