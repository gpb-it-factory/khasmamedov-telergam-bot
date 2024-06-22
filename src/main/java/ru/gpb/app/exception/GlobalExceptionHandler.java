package ru.gpb.app.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.gpb.app.dto.Error;

import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<Error> errorResponseEntityBuilder(String message,
                                                            String messageType,
                                                            String errorCode,
                                                            HttpStatus status) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Error(message,
                        messageType,
                        errorCode,
                        UUID.randomUUID()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleOfValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error: ", ex);

        return errorResponseEntityBuilder(
                "Ошибка валидации данных",
                "ValidationError",
                "400",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneralException(Exception ex) {
        log.error("General error: ", ex);
        return errorResponseEntityBuilder(
                "Произошло что-то ужасное, но станет лучше, честно",
                "GeneralError",
                "123",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
