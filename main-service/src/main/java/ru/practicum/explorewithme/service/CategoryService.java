package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.entity.Category;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.events.CategoryDto;
import ru.practicum.explorewithme.events.NewCategoryDto;
import ru.practicum.explorewithme.exceptions.ConflictException;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public CategoryDto changeCategory(CategoryDto categoryDto, Long catId) {
        String name = categoryDto.getName();
        List<Category> categories = categoryRepository.findAll();
        List<String> names = new ArrayList<>();
        categories.forEach(category -> names.add(category.getName()));
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
        List<Category> categories = categoryRepository.findAll();
        List<String> names = new ArrayList<>();
        categories.forEach(category -> names.add(category.getName()));
        if (names.contains(name)) {
            throw new ConflictException("category already exist");
        }
        Category category = categoryMapper.toCategory(newCategoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }
}
