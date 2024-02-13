package com.cs.api.service;

import com.cs.api.communApi.AccountApi;
import com.cs.api.dto.ETransferType;
import com.cs.api.dto.OperationDto;
import com.cs.api.dto.TransferParamRequest;
import com.cs.api.dto.TransferParamResponse;
import com.cs.api.dto.TransfertDTO;
import com.cs.api.dto.TransfertExpress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
@Transactional
public class TransfertAgentService {

    @Autowired
    WebClient.Builder webClient;

    private final String ACCOUNT_SERVICE_URL = "https://my-account-service.herokuapp.com/Account/";
    private final String PARAMETRE_SERVICE_URL = "https://my-parametrage-service.herokuapp.com/Parameter/calculateCost";

    public String transfertFromAccountToAnother(TransfertDTO transfertDTO) {
        String message = "";
        try {

            WebClient client = WebClient.create();

            TransferParamResponse responseParam = client.post()
                    .uri(PARAMETRE_SERVICE_URL)
                    .body(Mono
                            .just(new TransferParamRequest(transfertDTO.getAmount(), ETransferType.ACCOUNT_TO_ACCOUNT)),
                            TransferParamRequest.class)
                    .retrieve()
                    .bodyToMono(TransferParamResponse.class).block();

            if (responseParam.isValidTransfer() == true) {

                AccountApi accountApiSource = client.get()
                        .uri(ACCOUNT_SERVICE_URL + "get?id=" + transfertDTO.getAccountId()).retrieve()
                        .bodyToMono(AccountApi.class).block();

                AccountApi accountApiDestantion = client.get()
                        .uri(ACCOUNT_SERVICE_URL + "get?id=" + transfertDTO.getDestinationAccountId()).retrieve()
                        .bodyToMono(AccountApi.class).block();

                if (!accountApiSource.getId().equals("") && !accountApiDestantion.getId().equals("")) {
                    if (transfertDTO.getAmount() <= accountApiSource.getBalance()) {
                        double soustraction = 0;
                        if (transfertDTO.getSoustraction() == "FROM_ME") {
                            soustraction = responseParam.getOperationFees() + transfertDTO.getAmount();
                            accountApiSource.setBalance(accountApiSource.getBalance() - soustraction);
                        }
                        if (transfertDTO.getSoustraction() == "FROM_OTHER") {
                            soustraction = responseParam.getOperationFees() + transfertDTO.getAmount();
                            accountApiDestantion.setBalance(accountApiDestantion.getBalance() - soustraction);
                        }

                        if (transfertDTO.getSoustraction() == "FROM_BOTH") {
                            soustraction = responseParam.getOperationFees() + transfertDTO.getAmount();
                            accountApiSource.setBalance(accountApiSource.getBalance() - (soustraction / 2));
                            accountApiDestantion.setBalance(accountApiDestantion.getBalance() - (soustraction / 2));
                        }

                        client.put().uri(ACCOUNT_SERVICE_URL + "update")
                                .body(Mono.just(accountApiSource), AccountApi.class).retrieve()
                                .bodyToMono(AccountApi.class).block();

                        client.put().uri(ACCOUNT_SERVICE_URL + "update")
                                .body(Mono.just(accountApiDestantion), AccountApi.class).retrieve()
                                .bodyToMono(AccountApi.class).block();

                        message = "success";
                    } else
                        message = "le montant est insuffissant";
                } else
                    message = "account not found";
            } else
                message = "le transfert n'est pas valide";

        } catch (Exception e) {
            message = "error: " + e.getMessage();
        }
        return message;
    }

    public String virment(OperationDto operationDto) {
        String message = "";
        try {
            WebClient client = WebClient.create();
            AccountApi accountApi = client.get().uri(ACCOUNT_SERVICE_URL + operationDto.getAccountId()).retrieve()
                    .bodyToMono(AccountApi.class).block();
            if (!accountApi.getId().equals("")) {
                accountApi.setBalance(accountApi.getBalance() + operationDto.getAmount());
                client.put().uri(ACCOUNT_SERVICE_URL + "update")
                        .body(Mono.just(accountApi), AccountApi.class).retrieve()
                        .bodyToMono(AccountApi.class).block();
                message = "success";
            } else
                message = "account not found";

        } catch (Exception e) {
            message = "error:  " + e.getMessage();
        }
        return message;
    }

    public String retirer(OperationDto operationDto) {
        String message = "";
        try {
            WebClient client = WebClient.create();
            AccountApi accountApi = client.get().uri(ACCOUNT_SERVICE_URL + operationDto.getAccountId()).retrieve()
                    .bodyToMono(AccountApi.class).block();
            if (!accountApi.getId().equals("")) {
                if (operationDto.getAmount() <= accountApi.getBalance()) {
                    if ((accountApi.getBalance() - operationDto.getAmount() >= 0)) {
                        accountApi.setBalance(accountApi.getBalance() - operationDto.getAmount());
                        client.put().uri(ACCOUNT_SERVICE_URL + "update")
                                .body(Mono.just(accountApi), AccountApi.class).retrieve()
                                .bodyToMono(AccountApi.class).block();
                        message = "success";
                    } else
                        message = "le reste est inferieure le 0";
                } else
                    message = "le montant est insuffissant";
            } else
                message = "account not found ";

        } catch (Exception e) {
            message = "error: " + e.getMessage();
        }
        return message;
    }

    // this Transfer Express will expired after 3 days of his creation if monny not
    // taked, and monny should back
    public String transfertFromAccountToCash(TransfertExpress transfertExpress) {
        String message = "";
        try {
            WebClient client = WebClient.create();

            TransferParamResponse responseParam = client.post()
                    .uri(PARAMETRE_SERVICE_URL)
                    .body(Mono.just(
                            new TransferParamRequest(transfertExpress.getAmount(), ETransferType.ACCOUNT_TO_CASH)),
                            TransferParamRequest.class)
                    .retrieve()
                    .bodyToMono(TransferParamResponse.class).block();

            if (responseParam.isValidTransfer()) {
                AccountApi accountApiSource = client.get().uri(ACCOUNT_SERVICE_URL + transfertExpress.getAccountId())
                        .retrieve()
                        .bodyToMono(AccountApi.class).block();
                accountApiSource.setBalance(accountApiSource.getBalance() - transfertExpress.getAmount());
                // TODO: Send 2 Emails: one for Account's Awner , other for Email's Customer

                // TODO: Update currennt Account
                client.put().uri(ACCOUNT_SERVICE_URL + accountApiSource.getId());
            } else
                message = "le transfert n'est pas valide";

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return message;

    }

}
