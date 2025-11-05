package sgu.fit.supermarket.bus.impl;

import sgu.fit.supermarket.bus.AccountService;
import sgu.fit.supermarket.bus.EmployeeService;
import sgu.fit.supermarket.bus.RoleService;
import sgu.fit.supermarket.dao.AccountDAO;
import sgu.fit.supermarket.dao.impl.AccountDAOImpl;
import sgu.fit.supermarket.dto.AccountDTO;
import sgu.fit.supermarket.dto.EmployeeDTO;
import sgu.fit.supermarket.dto.RoleDTO;
import java.util.List;

public class AccountServiceImpl implements AccountService {
    private AccountDAO accountDAO;
    private EmployeeService employeeService;
    private RoleService roleService;
    
    public AccountServiceImpl() {
        this.accountDAO = new AccountDAOImpl();
        this.employeeService = new EmployeeServiceImpl();
        this.roleService = new RoleServiceImpl();
    }
    
    @Override
    public AccountDTO login(String username, String password) {
        try {
            return accountDAO.login(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Object[] getUserInfo(String username) {
        try {
            AccountDTO account = accountDAO.findByUsername(username);
            if (account == null) {
                return null;
            }
            return getUserInfoByAccount(account);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Object[] getUserInfoByAccount(AccountDTO account) {
        try {
            if (account == null) {
                return null;
            }
            
            EmployeeDTO employee = employeeService.findById(account.getEmployeeId());
            if (employee == null) {
                return null;
            }
            
            RoleDTO role = roleService.findById(employee.getRoleId());
            if (role == null) {
                return null;
            }
            
            return new Object[]{account, employee, role};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // CRUD implementations
    @Override
    public List<AccountDTO> getAllAccounts() {
        try { return accountDAO.findAll(); } catch (Exception e) { e.printStackTrace(); return null; }
    }

    @Override
    public AccountDTO getAccountById(int accountId) {
        try { if (accountId <= 0) return null; return accountDAO.findById(accountId); } catch (Exception e) { e.printStackTrace(); return null; }
    }

    @Override
    public boolean addAccount(AccountDTO account) {
        try {
            if (account == null) return false;
            if (account.getUsername() == null || account.getUsername().trim().isEmpty()) return false;
            if (account.getPassword() == null || account.getPassword().isEmpty()) return false;
            return accountDAO.insert(account);
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean updateAccount(AccountDTO account) {
        try {
            if (account == null || account.getAccountId() <= 0) return false;
            if (account.getUsername() == null || account.getUsername().trim().isEmpty()) return false;
            if (account.getPassword() == null || account.getPassword().isEmpty()) return false;
            AccountDTO existing = accountDAO.findById(account.getAccountId());
            if (existing == null) return false;
            return accountDAO.update(account);
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean deleteAccount(int accountId) {
        try {
            if (accountId <= 0) return false;
            AccountDTO existing = accountDAO.findById(accountId);
            if (existing == null) return false;
            return accountDAO.delete(accountId);
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}

