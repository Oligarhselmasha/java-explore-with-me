package ru.practicum.explorewithme.dto.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentFullDto {

    private Long id;

    private String created;

    private Long event;

    private Long author;

    private String commentary;

}
