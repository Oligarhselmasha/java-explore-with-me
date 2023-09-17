package errorsHandling;

import exceptions.UnCorrectableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.explorewithme.variables.Constants.DATE_PATTERN;

@RestControllerAdvice
public class ErrorHandler {

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIUnCorrectableException(final UnCorrectableException ex) {
        return new ErrorResponse("BAD_REQUEST",
                "Incorrectly made request.",
                ex.getMessage(),
                LocalDateTime.now().format(df)
        );
    }
}