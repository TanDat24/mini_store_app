package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.CategoryDTO;
import java.util.List;

public interface CategoryDAO {
    List<CategoryDTO> findAll();
    CategoryDTO findById(int categoryId);
    List<CategoryDTO> findByName(String name);
    boolean insert(CategoryDTO category);
    boolean update(CategoryDTO category);
    boolean delete(int categoryId);
}
