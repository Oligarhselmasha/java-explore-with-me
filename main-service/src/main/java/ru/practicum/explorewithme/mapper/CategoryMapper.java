package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.entity.Category;
import ru.practicum.explorewithme.events.CategoryDto;
import ru.practicum.explorewithme.events.NewCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto (Category category);

    Category toCategory (NewCategoryDto newCategoryDto);

    Category toCategoryFromCategoryDto (CategoryDto categoryDto);
}
