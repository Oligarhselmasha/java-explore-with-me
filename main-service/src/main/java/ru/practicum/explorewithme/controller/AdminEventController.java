package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.*;
import ru.practicum.explorewithme.service.EventService;
import ru.practicum.explorewithme.service.UserService;
import ru.practicum.explorewithme.users.NewUserRequest;
import ru.practicum.explorewithme.users.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;
    private final UserService userService;

    @PostMapping("/categories")
    public CategoryDto postNewCategory(@Valid @RequestBody NewCategoryDto newCategoryDto
    ) {
        return eventService.postNewCategory(newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public void removeCategory(@PathVariable("catId") Long catId) {
        eventService.removeCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto changeCategory(@Valid @RequestBody CategoryDto categoryDto,
                                      @PathVariable("catId") Long catId
    ) {
        return eventService.changeCategory(categoryDto, catId);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(required = false) Long[] users,
                                        @RequestParam(required = false) String[] states,
                                        @RequestParam(required = false) Long[] categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(required = false) Integer from,
                                        @RequestParam(required = false) Integer size) {
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto changeEventByAdmin(@Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                                           @PathVariable("eventId") Long eventId
    ) {
        return eventService.changeEventByAdmin(updateEventAdminRequest, eventId);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) Long[] ids,
                                  @RequestParam(required = false) Integer from,
                                  @RequestParam(required = false) Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    public UserDto postNewUser(@Valid @RequestBody NewUserRequest newUserRequest
    ) {
        return userService.postNewUser(newUserRequest);
    }

    @DeleteMapping("/users/{userId}")
    public void removeUser(@PathVariable("userId") Long userId
    ) {
        userService.removeUser(userId);
    }

    @PostMapping("/compilations")
    public CompilationDto postNewCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto
    ) {
        return eventService.postNewCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    public void removeCompilation(@PathVariable("compId") Long compId
    ) {
        eventService.removeCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto changeCompilation(@PathVariable("compId") Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest
    ) {
        return eventService.changeCompilation(updateCompilationRequest, compId);
    }
}
