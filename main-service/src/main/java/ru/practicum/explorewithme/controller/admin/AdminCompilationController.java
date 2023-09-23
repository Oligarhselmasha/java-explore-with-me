package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.CompilationDto;
import ru.practicum.explorewithme.events.NewCompilationDto;
import ru.practicum.explorewithme.events.UpdateCompilationRequest;
import ru.practicum.explorewithme.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postNewCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto
    ) {
        return compilationService.postNewCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCompilation(@PathVariable("compId") Long compId
    ) {
        compilationService.removeCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto changeCompilation(@PathVariable("compId") Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest
    ) {
        return compilationService.changeCompilation(updateCompilationRequest, compId);
    }
}
