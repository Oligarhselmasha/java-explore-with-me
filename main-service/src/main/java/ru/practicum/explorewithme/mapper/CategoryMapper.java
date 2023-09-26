package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.entity.Category;
import ru.practicum.explorewithme.dto.events.CategoryDto;
import ru.practicum.explorewithme.dto.events.NewCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    Category toCategory(NewCategoryDto newCategoryDto);

}
