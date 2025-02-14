package com.example.ReadMailSubjectAddInDB.Dao;

import com.example.ReadMailSubjectAddInDB.Entity.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmailDao {


    private final JdbcTemplate jdbcTemplate;

    public EmailDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Email> getAllEmails() {
        String sql = "select * from emails";

        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(Email.class));
    }

    public void saveEmail(String subject, String sender, String content) {
        String sql = "INSERT INTO emails (subject, sender, content) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, subject,sender,content);
    }


}
