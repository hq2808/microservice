package com.example.CustomerService.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private Long id;

    @NotBlank(message = "User id must not be blank")
    private String userId;

    @NotBlank(message = "fullName must not be blank")
    private String fullName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email must not be blank")
    private String email;

    private String phoneNumber;
    private String address;
}