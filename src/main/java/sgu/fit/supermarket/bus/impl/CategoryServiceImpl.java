package sgu.fit.supermarket.bus.impl;

import sgu.fit.supermarket.bus.CategoryService;
import sgu.fit.supermarket.dao.CategoryDAO;
import sgu.fit.supermarket.dao.impl.CategoryDAOImpl;
import sgu.fit.supermarket.dto.CategoryDTO;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryServiceImpl() {
        this.categoryDAO = new CategoryDAOImpl();
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        try {
            return categoryDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CategoryDTO getCategoryById(int categoryId) {
        try {
            if (categoryId <= 0) return null;
            return categoryDAO.findById(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<CategoryDTO> searchCategoriesByName(String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return getAllCategories();
            }
            return categoryDAO.findByName(name.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean addCategory(CategoryDTO category) {
        try {
            if (category == null) return false;
            if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
                return false;
            }
            return categoryDAO.insert(category);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCategory(CategoryDTO category) {
        try {
            if (category == null || category.getCategoryId() <= 0) return false;
            if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
                return false;
            }
            CategoryDTO existing = categoryDAO.findById(category.getCategoryId());
            if (existing == null) return false;
            return categoryDAO.update(category);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCategory(int categoryId) {
        try {
            if (categoryId <= 0) return false;
            CategoryDTO existing = categoryDAO.findById(categoryId);
            if (existing == null) return false;
            return categoryDAO.delete(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
