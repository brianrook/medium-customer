package com.brianrook.medium.customer.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long customerId;
    @NotBlank
    @Size(min = 0, max = 100)
    private String firstName;
    @NotBlank
    @Size(min = 0, max = 100)
    private String lastName;
    @Pattern(regexp="\\(\\d{3}\\)\\d{3}-\\d{4}", message = "Phone number must match the pattern (###)###-####")
    @Size(max = 20)
    private String phoneNumber;
    @Email
    @Size(max = 150)
    private String email;
}
