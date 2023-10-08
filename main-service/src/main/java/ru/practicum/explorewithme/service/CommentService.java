package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.comments.CommentFullDto;
import ru.practicum.explorewithme.dto.comments.ShortCommentDto;
import ru.practicum.explorewithme.dto.comments.UpdateCommentDto;
import ru.practicum.explorewithme.entity.Comment;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.User;
import ru.practicum.explorewithme.exceptions.MissingException;
import ru.practicum.explorewithme.exceptions.UnCorrectableException;
import ru.practicum.explorewithme.mapper.CommentMapper;
import ru.practicum.explorewithme.repository.CommentRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.explorewithme.variables.Status.PUBLISHED;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final CommentMapper commentMapper;

    public void removeCommentByAdmin(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new MissingException("Not found comment"));
        commentRepository.deleteById(commentId);
    }

    public void removeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new MissingException("Not found comment"));
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new UnCorrectableException("User isn't owner this event");
        }
        commentRepository.deleteById(commentId);
    }

    public CommentFullDto postNewUsersComment(Long userId, Long eventId, ShortCommentDto newCommentDto) {
        User author = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (!Objects.equals(event.getInitiator().getId(), userId) && event.getState().getState() != PUBLISHED) {
            throw new UnCorrectableException("It is impossible for a non-owner to leave a comment on an unpublished" +
                    " event");
        }
        Comment comment = commentMapper.toComment(newCommentDto);
        comment.setEvent(event);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return makeFullDto(commentRepository.save(comment), userId);
    }

    public CommentFullDto changeUsersComment(Long userId, UpdateCommentDto changeCommentDto) {
        Comment comment = commentRepository.findById(changeCommentDto.getId())
                .orElseThrow(() -> new MissingException("Not found comment"));
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new UnCorrectableException("User isn't owner this comment");
        }
        comment.setCommentary(changeCommentDto.getCommentary());
        return makeFullDto(commentRepository.save(comment), userId);
    }

    public List<CommentFullDto> getUsersComments(Long userId, Integer from, Integer size) {
        List<Comment> comments = commentRepository.findByAuthor_IdOrderByCreatedDesc(userId);
        return comments.stream()
                .map(comment -> makeFullDto(comment, userId))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    public CommentFullDto changeUsersCommentByAdmin(UpdateCommentDto changeCommentDto) {
        Comment comment = commentRepository.findById(changeCommentDto.getId())
                .orElseThrow(() -> new MissingException("Not found comment"));
        comment.setCommentary(changeCommentDto.getCommentary());
        return makeFullDto(commentRepository.save(comment), comment.getAuthor().getId());
    }

    public List<CommentFullDto> getAllComments(Integer from, Integer size) {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(comment -> makeFullDto(comment, comment.getAuthor().getId()))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    public void removeAllCommentByAdmin() {
        commentRepository.deleteAll();
    }

    private CommentFullDto makeFullDto(Comment comment, Long userId) {
        CommentFullDto commentFullDto = commentMapper.toCommentFullDto(comment);
        commentFullDto.setEvent(comment.getEvent().getId());
        commentFullDto.setAuthor(userId);
        return commentFullDto;
    }
}
