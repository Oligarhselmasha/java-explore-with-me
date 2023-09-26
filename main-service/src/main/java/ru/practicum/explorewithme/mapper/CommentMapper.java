package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.dto.comments.CommentFullDto;
import ru.practicum.explorewithme.dto.comments.ShortCommentDto;
import ru.practicum.explorewithme.entity.Comment;

import static ru.practicum.explorewithme.variables.Constants.DATE_PATTERN;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "event", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toComment(ShortCommentDto shortCommentDto);

    @Mapping(target = "event", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(source = "created", target = "created", dateFormat = DATE_PATTERN)
    CommentFullDto toCommentFullDto(Comment comment);
}
