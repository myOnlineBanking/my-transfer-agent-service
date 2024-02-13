package com.cs.api.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransfertDto {
    private double amount;
    private Date date;
    private String accountFrom;
    private String accountTo;
    private String transferType;
    private String costType;
    private Date holdingDate;
}
