package com.cs.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransfertExpress {
    
    private String accountId;
    private String destinationEmail;
    private double amount;


}