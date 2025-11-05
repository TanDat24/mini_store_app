package sgu.fit.supermarket.bus;

import sgu.fit.supermarket.dto.AccountDTO;
import java.util.List;

public interface AccountService {
    /**
     * Đăng nhập với username và password
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return AccountDTO nếu đăng nhập thành công, null nếu thất bại
     */
    AccountDTO login(String username, String password);
    
    /**
     * Lấy thông tin đầy đủ của user sau khi đăng nhập (Account + Employee + Role)
     * @param username Tên đăng nhập
     * @return Object[] chứa [AccountDTO, EmployeeDTO, RoleDTO] hoặc null nếu không tìm thấy
     */
    Object[] getUserInfo(String username);
    
    /**
     * Lấy thông tin đầy đủ của user từ account
     * @param account AccountDTO
     * @return Object[] chứa [AccountDTO, EmployeeDTO, RoleDTO] hoặc null nếu không tìm thấy
     */
    Object[] getUserInfoByAccount(AccountDTO account);

    // CRUD for Account management
    List<AccountDTO> getAllAccounts();
    AccountDTO getAccountById(int accountId);
    boolean addAccount(AccountDTO account);
    boolean updateAccount(AccountDTO account);
    boolean deleteAccount(int accountId);
}

