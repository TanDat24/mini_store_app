package sgu.fit.supermarket.dto;

public class AccountDTO {
    private int accountId;
    private String username;
    private String password;
    private int employeeId;

    public AccountDTO() {
    }

    public AccountDTO(int accountId, String username, String password, int employeeId) {
        this.accountId = accountId;
        this.username = username;
        this.password = password;
        this.employeeId = employeeId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                "accountId=" + accountId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", employeeId=" + employeeId +
                '}';
    }
}
