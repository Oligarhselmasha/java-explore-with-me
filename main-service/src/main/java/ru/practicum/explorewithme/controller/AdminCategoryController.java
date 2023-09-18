package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.CategoryDto;
import ru.practicum.explorewithme.events.NewCategoryDto;
import ru.practicum.explorewithme.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postNewCategory(@Valid @RequestBody NewCategoryDto newCategoryDto
    ) {
        return categoryService.postNewCategory(newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable("catId") Long catId) {
        categoryService.removeCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto changeCategory(@Valid @RequestBody CategoryDto categoryDto,
                                      @PathVariable("catId") Long catId
    ) {
        return categoryService.changeCategory(categoryDto, catId);
    }
}
