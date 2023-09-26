package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.entity.Category;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.dto.events.CategoryDto;
import ru.practicum.explorewithme.dto.events.NewCategoryDto;
import ru.practicum.explorewithme.exceptions.ConflictException;
import ru.practicum.explorewithme.exceptions.MissingException;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public CategoryDto changeCategory(CategoryDto categoryDto, Long catId) {
        String name = categoryDto.getName();
        Set<String> names = getSetNames();
        if (names.contains(name) && !Objects.equals(categoryRepository.findByName(name).getId(), catId)) {
            throw new ConflictException("category already exist");
        }
        Category category = categoryRepository.findById(catId).orElseThrow();
        category.setName(name);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public void removeCategory(Long catId) {
        List<Event> events = eventRepository.findByCategory_Id(catId);
        if (!events.isEmpty()) {
            throw new ConflictException("This category contains events");
        }
        categoryRepository.deleteById(catId);
    }

    public CategoryDto postNewCategory(NewCategoryDto newCategoryDto) {
        String name = newCategoryDto.getName();
        Set<String> names = getSetNames();
        if (names.contains(name)) {
            throw new ConflictException("category already exist");
        }
        Category category = categoryMapper.toCategory(newCategoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }
    public CategoryDto getCategorie(Long catId) {
        return categoryMapper.toCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new MissingException("Not found category")));
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {

        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryDto)
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }


    private Set<String> getSetNames() {
        return categoryRepository.findAll().stream()
                .map(Category::getName)
                .collect(Collectors.toSet());
    }
}
