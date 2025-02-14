package com.example.ReadMailSubjectAddInDB.Controller;

import com.example.ReadMailSubjectAddInDB.Dao.EmailDao;
import com.example.ReadMailSubjectAddInDB.Entity.Email;
import com.example.ReadMailSubjectAddInDB.Service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/email")
public class EmailController {


 private final EmailService emailService;
 private final EmailDao emailDao;

    public EmailController(EmailService emailService, EmailDao emailDao) {
        this.emailService = emailService;
        this.emailDao = emailDao;
    }


    @GetMapping("/fetch")
    public String fetchAndReadMailUsingKeyword() {
        emailService.fetchAndReadMailUsingKeyword();
        return "Emails fetched and stored successfully!";

    }


    @GetMapping("/all")
    public List<Email> getAllEmails() {
        return emailDao.getAllEmails();
    }


}
