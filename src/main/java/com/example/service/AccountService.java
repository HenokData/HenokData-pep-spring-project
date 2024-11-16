package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service

public class AccountService {

    private final AccountRepository accountRepository;


    @Autowired
    // constructor 
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    /* note
     * @parm the account to be saved
     * @return return the saved account
     * @trow throw error or illegal argument if the requrement is not reached
     */

    public Account saveAccount(Account account){
        if (!isValidUsername(account.getUsername())) {
            throw new IllegalArgumentException("duplicate");
        }
        if (!isValidPassword(account.getPassword())) {
            throw new IllegalArgumentException("password must be unq and greter than 4 charactors");
            
        }
        return accountRepository.save(account);

    }
    /* VALIDATE THE USER NAME
     * check if the user name is not blank and it is uniqe
     * @parm username to validate
     * @return return true if vaild or if not  false 
     */

    private boolean isValidUsername(String username){
        return StringUtils.hasText(username) && accountRepository.findByUsername(username) == null; 

    }

    /* VALIDATE THE
     * chek if the password character is more than 4 characters
     * @parm password to be vaildate
     * @return return true if vaild or false if not vaild
     */

    private boolean isValidPassword(String password){

        return StringUtils.hasText(password) && password.length() > 4;

    }

    /* 2 check the existance and vaildation of the username and the pasword 
     * retereve account by the username
     * @parm check the username
     * @return optional contal found account, if not found return empty
     */

     public Optional<Account> findAccountByUsername(String username) {
        return Optional.ofNullable(accountRepository.findByUsername(username));
    } 

    /* check login cardentals 
    * @parm username username to serach 
    * @parm pasword pasword to much or vaildat
    * @return optiona contials if the account is correct 
    */
    public Optional<Account> validateLogin(String username, String password){

    Optional<Account> account = findAccountByUsername(username);
    return account.filter(acc -> acc.getPassword().equals(password));

    }  

}
