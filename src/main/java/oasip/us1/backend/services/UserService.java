package oasip.us1.backend.services;

import oasip.us1.backend.dtos.*;
import oasip.us1.backend.entities.User;
import oasip.us1.backend.enums.ROLE;
import oasip.us1.backend.repositories.UserRepository;
import oasip.us1.backend.utils.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    private Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(16, 16, 1, 65536, 1);


    public ResponseEntity getAllUsers() {
        return new ResponseEntity(listMapper.mapList(userRepository.findAllByOrderByNameAsc(), UserDto.class, modelMapper), HttpStatus.OK);
    }

    public ResponseEntity getUserById(Integer id, WebRequest request) {
        Map<String, String> fieldError = new HashMap<>();
        if (userRepository.findById(id).isEmpty()) {
            fieldError.put("userId", "User ID: " + id + "does not exist.");
            ErrorDTO errorBody = new ErrorDTO(OffsetDateTime.now(ZoneOffset.UTC).toString(), HttpStatus.BAD_REQUEST.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorBody, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(modelMapper.map(userRepository.findById(id), UserDto.class), HttpStatus.OK);
    }

    public ResponseEntity save(UserPostDto newUser, BindingResult bindingResult, WebRequest request) {
//        Error handling
        Map<String, String> fieldError = new HashMap<>();
        bindingResult.getAllErrors().forEach((error) -> {
            fieldError.put(((FieldError) error).getField(), error.getDefaultMessage());
        });

//        Cleaning data
        if (fieldError.isEmpty()) {
            // trim the name and email so that there are no spaces.
            newUser.setName(newUser.getName().trim());
            newUser.setEmail(newUser.getEmail().trim());
        }

//        Validation data
        if (!userRepository.findAllByName(newUser.getName()).isEmpty()) {
            fieldError.put("name", "name should be unique.");
        }
        if (!userRepository.findAllByEmail(newUser.getEmail()).isEmpty()) {
            fieldError.put("email", "email should be unique.");
        }
        if (!newUser.getPassword().equals(null)) {
            newUser.setPassword(encoder.encode(newUser.getPassword()));
        }
        if (!EnumSet.allOf(ROLE.class).stream().map(ROLE::name).collect(Collectors.toList()).contains(newUser.getRole())) {
            if (newUser.getRole().equals("")) {
                newUser.setRole(ROLE.student.toString());
            } else {
                fieldError.put("role", "The role can only be admin, lecturer, or student.");
            }
        }

        if (fieldError.isEmpty()) {
            System.out.println(newUser.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED.value()).body(modelMapper.map(userRepository.saveAndFlush(modelMapper.map(newUser, User.class)), UserDto.class));
        }

        ErrorDTO errorBody = new ErrorDTO(OffsetDateTime.now(ZoneOffset.UTC).toString(), HttpStatus.BAD_REQUEST.value(),
                ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);

        return new ResponseEntity(errorBody, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity delete(Integer id, WebRequest request) {
        Map<String, String> fieldError = new HashMap<>();
        if (userRepository.findById(id).isEmpty()) {
            ErrorDTO errorBody = new ErrorDTO(OffsetDateTime.now(ZoneOffset.UTC).toString(), HttpStatus.NOT_FOUND.value(),
                    ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);
            return new ResponseEntity(errorBody, HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
        return new ResponseEntity("User ID: " + id + " has been deleted.", HttpStatus.OK);
    }

    public ResponseEntity update(Integer userId, UserPutDto updateUser, BindingResult bindingResult, WebRequest request) {
//        Error handling
        Map<String, String> fieldError = new HashMap<>();
        bindingResult.getAllErrors().forEach((error) -> {
            fieldError.put(((FieldError) error).getField(), error.getDefaultMessage());
        });

        if (userRepository.findById(userId).isEmpty()) {
            fieldError.put("id", "User's ID did not found.");
        }

//        Cleaning data
        if (updateUser.getRole().equals("")) {
            updateUser.setRole(ROLE.student);
        }
        updateUser.setName(updateUser.getName().trim());
        updateUser.setEmail(updateUser.getEmail().trim());

//        Validation data
        if (!userRepository.findAllByNameExceptMyself(updateUser.getName(), userId).isEmpty()) {
            fieldError.put("name", "name should be unique.");
        }
        if (!userRepository.findAllByNameExceptMyself(updateUser.getEmail(), userId).isEmpty()) {
            fieldError.put("email", "email should be unique.");
        }

        if (fieldError.isEmpty()) {
            User user = userRepository.findById(userId).map(usr -> mapUser(usr, updateUser)).get();
            userRepository.saveAndFlush(user);
            return ResponseEntity.status(HttpStatus.OK.value()).body(updateUser);
        }

        ErrorDTO errorBody = new ErrorDTO(OffsetDateTime.now(ZoneOffset.UTC).toString(), HttpStatus.BAD_REQUEST.value(),
                ((ServletWebRequest) request).getRequest().getRequestURI(), "Validation failed", fieldError);

        return new ResponseEntity(errorBody, HttpStatus.BAD_REQUEST);
    }

    private User mapUser(User existingUser, UserPutDto updateUser) {
        existingUser.setName(updateUser.getName());
        existingUser.setEmail(updateUser.getEmail());
        existingUser.setRole(updateUser.getRole());
        return existingUser;
    }

    public ResponseEntity matchingPassword(UserMatchingPasswordDto userMatchingPasswordDto, BindingResult bindingResult, WebRequest request) {
        Map<String, String> fieldError = new HashMap<>();
        bindingResult.getAllErrors().forEach((error) -> {
            fieldError.put(((FieldError) error).getField(), error.getDefaultMessage());
        });

        if (fieldError.isEmpty()) {
            User user = userRepository.findByEmail(userMatchingPasswordDto.getEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("A user with the specified email DOES NOT exist.");
            } else {
                if (!encoder.matches(userMatchingPasswordDto.getPassword(), user.getPassword())) {
                    return new ResponseEntity("Password NOT Matched.", HttpStatus.UNAUTHORIZED);
                }
                return ResponseEntity.status(HttpStatus.OK).body("Password Matched.");
            }
        }

        ErrorDTO errorBody = new ErrorDTO(OffsetDateTime.now(ZoneOffset.UTC).toString(), HttpStatus.BAD_REQUEST.value(),
                ((ServletWebRequest) request).getRequest().getRequestURI(), "Matching failed", fieldError);
        return new ResponseEntity(errorBody, HttpStatus.BAD_REQUEST);
    }
}
