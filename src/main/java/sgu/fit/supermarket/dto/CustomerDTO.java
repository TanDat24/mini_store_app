package sgu.fit.supermarket.dto;

public class CustomerDTO {
    private int customerId;
    private String fullName;
    private String phone;
    private int points;

    public CustomerDTO() {
    }

    public CustomerDTO(int customerId, String fullName, String phone, int points) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.points = points;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
                "customerId=" + customerId +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", points=" + points +
                '}';
    }
}
