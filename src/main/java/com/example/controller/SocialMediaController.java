package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.Map; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring.The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

@RestController
// @RequestMapping("/api") this made an Excetion[400]but was[404]error
 
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;
   
    

    @Autowired
    // constractor 
    public SocialMediaController(AccountService accountService, MessageService messageService){
        this.accountService = accountService;
        this.messageService = messageService;
    }

    /* 1: Our API should be able to process new User registrations.
     * register a new account if the user meet the username and password requrements
     * @parm account the account data for registration
     * @return The response status should be 200 OK, which is the default, 
     * if not successful due to a duplicate username, the response status should be 409
     * if  not successful for some other reason, the response status should be 400
     */
    @PostMapping("/register")

    public ResponseEntity<?> registerAccount(@RequestBody Account account){

        try {
            Account createdAccount = accountService.saveAccount(account); // saveAccount to save in Account class
            return ResponseEntity.ok(createdAccount);
        } catch (IllegalArgumentException ex) {

            // lets check if the exception message matches a specific issue
            if (ex.getMessage().contains("duplicate")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("the user name is alrady exist");
                
            }
            // the rest validation
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }

    }

    /*2: Our API should be able to process User logins
     * The login will be successful if and only if the username and password provided in the request body JSON match a real account existing on the database
     * If successful, the response body should contain a JSON of the account,body, including its accountId, status should be 200 OK
     * If the login is not successful, the response status should be 401 
     */
    @PostMapping("/login") 
    public ResponseEntity<?> login(@RequestBody Account account){
        
        Optional<Account> authenticatedAccount = accountService.validateLogin(account.getUsername(), account.getPassword());

        if (authenticatedAccount.isPresent()){
            return ResponseEntity.ok(authenticatedAccount.get()); // 200 ok
            
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Collections.singletonMap("error", "invalid username or pasword"));
        }
        
        }

    /* 3 message endpoint 
     * @param message The message data
     * @return creat message with the status of 200 OK if successful, or 400 if validation fails.
     */
    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(@RequestBody Message message){

        try {
            Message createdMessage = messageService.saveMessage(message);
            return ResponseEntity.ok(createdMessage);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("error", ex.getMessage()));
        }

    }

    /* 4: Our API should be able to retrieve all messages.
     * As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages.
     */
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages(){

        List<Message> messages = messageService.getAllMessages();
        System.out.println("Retrieved messages: " + messages);
        return ResponseEntity.ok(messages);
        
    }

    /* 5 get the messageId 
     * if the message found we get 200 ok if not null
     */
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable Integer messageId){

        Optional<Message> message = messageService.getMessageById(messageId);
       
        return message.isPresent() ? ResponseEntity.ok(message.get()) : ResponseEntity.ok().build();
    }

    /* 6: Our API should be able to delete a message identified by a message ID.
     * The deletion of an existing message should remove an existing message from the database
     */

    
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessageById(@PathVariable Integer messageId){

        try {
            boolean messageExisted = messageService.deleteMessageById(messageId);
            if (messageExisted) {
                return ResponseEntity.ok(Collections.singletonMap("rowsUpdated", 1)); // 200 ok
                
            } else {
                return ResponseEntity.ok().build(); // 200 with empty ok

                
            }
            
        } catch (Exception e) {

            System.err.println("Error occurred while deleting message: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
             .body(Collections.singletonMap("error", e.getMessage()));
            
        }

       
    }

    /* 7 update a message text identified by a message ID
     * @param messageId the id to be updated
     * @param message object with the new message
     * @return 200 ok succesful, 400 bad reques
     */

    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<?> updateMessageText(@PathVariable Integer messageId, @RequestBody Map<String, String> request){

        String newMessageText = request.get("messageText");

        if (newMessageText == null || newMessageText.isEmpty() || newMessageText.length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("error", "Message text cannot be null or empty and no more than255 characters"));
            
        }

        boolean updateSuccessful = messageService.updateMessageText(messageId, newMessageText);

        if (updateSuccessful) {

            return ResponseEntity.ok(Collections.singletonMap("rowsUpdated", 1));
            
        }else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Invalid message id or message"));
        }

     }
    /* 8: Our API should be able to retrieve all messages written by a particular user
     * @param  accoundId whose message are to reterive
     * @return return 200 ok if succesful if not empty
     */
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByUserId(@PathVariable Integer accountId){
        List<Message> messages = messageService.getMessagesByUserId(accountId);
        return ResponseEntity.ok(messages); // 200 ok message 
    }

}
    



