package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.comments.CommentFullDto;
import ru.practicum.explorewithme.dto.comments.UpdateCommentDto;
import ru.practicum.explorewithme.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeComment(@PathVariable("commentId") Long commentId) {
        commentService.removeCommentByAdmin(commentId);
    }
    @DeleteMapping("/comments")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllComment() {
        commentService.removeAllCommentByAdmin();
    }

    @PatchMapping("/comments")
    public CommentFullDto changeUsersComment(@Valid @RequestBody UpdateCommentDto changeCommentDto
    ) {
        return commentService.changeUsersCommentByAdmin(changeCommentDto);
    }

    @GetMapping("/comments")
    public List<CommentFullDto> getAllComments(@RequestParam(defaultValue = "0", required = false) Integer from,
                                               @RequestParam(defaultValue = "10", required = false) Integer size) {
        return commentService.getAllComments(from, size);
    }
}

