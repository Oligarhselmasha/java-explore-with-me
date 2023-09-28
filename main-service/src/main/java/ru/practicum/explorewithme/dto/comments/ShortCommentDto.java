package ru.practicum.explorewithme.dto.comments;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ShortCommentDto {

    @NotBlank
    @NotNull
    @Size(max = 2000)
    private String comment;
}
