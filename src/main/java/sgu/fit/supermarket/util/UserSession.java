package sgu.fit.supermarket.util;

import sgu.fit.supermarket.dto.AccountDTO;
import sgu.fit.supermarket.dto.EmployeeDTO;
import sgu.fit.supermarket.dto.RoleDTO;

public class UserSession {
    private static UserSession instance;
    private AccountDTO account;
    private EmployeeDTO employee;
    private RoleDTO role;
    
    private UserSession() {
    }
    
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    public void setUser(AccountDTO account, EmployeeDTO employee, RoleDTO role) {
        this.account = account;
        this.employee = employee;
        this.role = role;
    }
    
    public void clear() {
        this.account = null;
        this.employee = null;
        this.role = null;
    }
    
    public AccountDTO getAccount() {
        return account;
    }
    
    public EmployeeDTO getEmployee() {
        return employee;
    }
    
    public RoleDTO getRole() {
        return role;
    }
    
    public boolean isLoggedIn() {
        return account != null && employee != null && role != null;
    }
}

