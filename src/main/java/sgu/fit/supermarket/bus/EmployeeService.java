package sgu.fit.supermarket.bus;

import sgu.fit.supermarket.dto.EmployeeDTO;
import java.util.List;

public interface EmployeeService {
    /**
     * Tìm nhân viên theo ID
     * @param employeeId ID của nhân viên
     * @return EmployeeDTO nếu tìm thấy, null nếu không
     */
    EmployeeDTO findById(int employeeId);

    /**
     * Lấy danh sách tất cả nhân viên
     */
    List<EmployeeDTO> findAll();

    /** Tìm kiếm theo tên hoặc số điện thoại */
    List<EmployeeDTO> search(String keyword);

    /** Thêm nhân viên */
    boolean add(EmployeeDTO employee);

    /** Cập nhật nhân viên */
    boolean update(EmployeeDTO employee);

    /** Xóa nhân viên */
    boolean delete(int employeeId);
}
