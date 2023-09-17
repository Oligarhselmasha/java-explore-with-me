package ru.practicum.explorewithme.errorsHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.exceptions.ConflictException;
import ru.practicum.explorewithme.exceptions.MissingException;
import ru.practicum.explorewithme.exceptions.UnCorrectableException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.explorewithme.variables.Constants.DATE_PATTERN;

@RestControllerAdvice
public class ErrorHandler {

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIValidationException(final MethodArgumentNotValidException ex) {
        return new ErrorResponse("BAD_REQUEST"
                , "Incorrectly made request."
                , ex.getMessage()
                , LocalDateTime.now().format(df)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIUnCorrectableException(final UnCorrectableException ex) {
        return new ErrorResponse("BAD_REQUEST"
                , "Incorrectly made request."
                , ex.getMessage()
                , LocalDateTime.now().format(df)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIConflictException(final ConflictException ex) {
        return new ErrorResponse("FORBIDDEN"
                , "For the requested operation the conditions are not met."
                , ex.getMessage()
                , LocalDateTime.now().format(df)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIConflictException(final MissingException ex) {
        return new ErrorResponse("NOT_FOUND"
                , "The required object was not found."
                , ex.getMessage()
                , LocalDateTime.now().format(df)
        );
    }
}