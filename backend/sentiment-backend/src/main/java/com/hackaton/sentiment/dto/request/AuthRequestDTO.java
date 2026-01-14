package com.hackaton.sentiment.dto.request;



import lombok.Data;

@Data
public class AuthRequestDTO {
    private String username;
    private String password;
}