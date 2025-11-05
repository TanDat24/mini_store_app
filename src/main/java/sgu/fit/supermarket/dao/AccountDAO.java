package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.AccountDTO;
import java.util.List;

public interface AccountDAO {
    AccountDTO login(String username, String password);
    AccountDTO findByUsername(String username);
    List<AccountDTO> findAll();
    AccountDTO findById(int accountId);
    boolean insert(AccountDTO account);
    boolean update(AccountDTO account);
    boolean delete(int accountId);
}

