package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.AccountDTO;

public interface AccountDAO {
    AccountDTO login(String username, String password);
    AccountDTO findByUsername(String username);
}

