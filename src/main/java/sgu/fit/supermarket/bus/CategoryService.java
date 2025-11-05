package sgu.fit.supermarket.bus;

import sgu.fit.supermarket.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryById(int categoryId);
    List<CategoryDTO> searchCategoriesByName(String name);
    boolean addCategory(CategoryDTO category);
    boolean updateCategory(CategoryDTO category);
    boolean deleteCategory(int categoryId);
}
