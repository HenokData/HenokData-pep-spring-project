package com.example.service;


import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Optional; 

@Service

public class MessageService {

    // fileds
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    @Autowired

    // constructor 
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository){
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    /* 3 validate and save the new message
     * @parm the message to save
     * @param saved message
     * @thow is the its illegal 
     */
    public Message saveMessage(Message message){

        if (!isValidMessageText(message.getMessageText())) {
            throw new IllegalArgumentException("message text must not be blank and must be under 255 characters");
            
        }
        if (!isExistingUser(message.getPostedBy())) {
            throw new IllegalArgumentException("the user posting message does not exist");
        }

        return messageRepository.save(message);

    }

    /* 4: Our API should be able to retrieve all messages
     * list to simply be empty if there are no messages. The response status should always be 200,
     * @return the list of messages
     */
    public List<Message> getAllMessages(){
        return messageRepository.findAll(); 
    }

    /* 5: Our API should be able to retrieve a message by its ID
     * The response status should always be 200, which is the default
     * @parm messageId to reterive the message id
     * @return optional contan the messageid if found otherwise empty if not found
     */
    public Optional<Message> getMessageById(Integer messageId){

        return messageRepository.findById(messageId);

    }

    /* 6: Our API should be able to delete a message identified by a message ID
     * @parm messageid that the message to be deleted
     * @return return true if the message exist and deleted otherwise false
     */
    public boolean deleteMessageById(Integer messageId){
        if (messageRepository.existsById(messageId)){
            messageRepository.deleteById(messageId);
                return true;
            }
        
        return false;
 }

    /* 7 update a message text identified by a message ID
     * @pram messageId to be update the message
     * @pram the new message 
     * @return true if the update is succesful otherwise false
     */

    public boolean updateMessageText(Integer messageId, String newMessageText){

        if (!isValidMessageText(newMessageText)) {
            return false; //invalide
            
        }
        Optional<Message> messageOptional = messageRepository.findById(messageId);

        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            message.setMessageText(newMessageText);
            messageRepository.save(message);// saved updated message
            return true;
            
        }
        return false; // if the message not found
        
    
    }
    /* 8 retrieve all messages written by a particular user
     * @param accountId the id to reterive the message
     * @return list of messages posted by the user
     */
    public List<Message> getMessagesByUserId(Integer accountId){
        return messageRepository.findByPostedBy(accountId);

    }
    
    /* Checks if the message text is valid
     * 
     */

   
    private boolean isValidMessageText(String messageText){

        return StringUtils.hasText(messageText) && messageText.length() <= 255;
    }

    private boolean isExistingUser(Integer userId){

        Optional<Account> account = accountRepository.findById(userId);
        return account.isPresent();

        
    }

}
