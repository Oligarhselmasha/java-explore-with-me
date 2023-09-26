package ru.practicum.explorewithme.errorsHandling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String status;

    private final String reason;

    private final String message;

    private final String timestamp;
}
