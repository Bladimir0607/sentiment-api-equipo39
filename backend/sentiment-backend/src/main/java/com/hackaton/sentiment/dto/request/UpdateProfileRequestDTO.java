package com.hackaton.sentiment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequestDTO {
    @Email
    private String email;

    @Size(min = 2, max = 100)
    private String fullName;
}
