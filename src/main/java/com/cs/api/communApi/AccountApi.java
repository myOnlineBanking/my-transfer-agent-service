package com.cs.api.communApi;

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
public class AccountApi {
    private String id;
    private double balance;
    private boolean accepted ;
    private String accountNumber ;
    private Date creationDate;
    private String currency;
    private boolean deleted ;
    private boolean enabled ;
    private String type ;
    private String userId ;
}
