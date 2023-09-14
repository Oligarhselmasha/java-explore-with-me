package ru.practicum.explorewithme.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipationRequestDto {
    private String created;

    private Long event;

    private Long id;

    private Long requester;

    private String status;
}
