package study.wild.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.wild.domain.Category;
import study.wild.dto.CategoryDto;
import study.wild.dto.PostDto;
import study.wild.exception.CategoryNotFoundException;
import study.wild.exception.NonEmptyCategoryException;
import study.wild.repository.CategoryRepository;
import study.wild.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

// TODO: CategoryPostService 분리할 것
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final PostRepository postRepository;

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.save(categoryDto.toEntity());
        return CategoryDto.from(category);
    }

    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
        category.setName(categoryDto.name());
        return CategoryDto.from(category);
    }

    public List<CategoryDto> getCategoryAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryDto::from)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoriesByPost(PostDto postDto) {
        if (postDto.categoryId() == null) {
            return CategoryDto.from(Category.defaultCategory());
        }
        return CategoryDto.from(
                categoryRepository.findById(postDto.categoryId())
                        .orElseThrow(CategoryNotFoundException::new)
        );
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        if (hasPostInCategory(categoryId)) {
            throw new NonEmptyCategoryException();
        }
        categoryRepository.deleteById(categoryId);
    }

    public boolean hasPostInCategory(Long categoryId) {
        return !postRepository.findPostByCategoryIdAndDeleted(categoryId, false).isEmpty();
    }
}
