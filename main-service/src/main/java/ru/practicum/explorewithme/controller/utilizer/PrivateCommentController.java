package ru.practicum.explorewithme.controller.utilizer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.comments.CommentFullDto;
import ru.practicum.explorewithme.dto.comments.ShortCommentDto;
import ru.practicum.explorewithme.dto.comments.UpdateCommentDto;
import ru.practicum.explorewithme.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{userId}/comments/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto postNewUsersComment(@PathVariable("userId") Long userId,
                                              @PathVariable("eventId") Long eventId,
                                              @Valid @RequestBody ShortCommentDto newCommentDto
    ) {
        return commentService.postNewUsersComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{userId}/comments")
    public CommentFullDto changeUsersComment(@PathVariable("userId") Long userId,
                                             @Valid @RequestBody UpdateCommentDto changeCommentDto
    ) {
        return commentService.changeUsersComment(userId, changeCommentDto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUsersComment(@PathVariable("commentId") Long commentId,
                                   @PathVariable("userId") Long userId) {
        commentService.removeComment(commentId, userId);
    }

    @GetMapping("/{userId}/comments")
    public List<CommentFullDto> getUsersComments(@PathVariable("userId") Long userId,
                                                 @RequestParam(defaultValue = "0", required = false) Integer from,
                                                 @RequestParam(defaultValue = "10", required = false) Integer size) {
        return commentService.getUsersComments(userId, from, size);
    }

    @GetMapping("/comments/{commentId}")
    public CommentFullDto getUsersComment(@PathVariable("commentId") Long commentId) {
        return commentService.getUsersComment(commentId);
    }
}
