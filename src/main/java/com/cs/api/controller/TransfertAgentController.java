package com.cs.api.controller;

import com.cs.api.dto.OperationDto;
import com.cs.api.dto.TransfertDTO;
import com.cs.api.service.TransfertAgentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer-agent")
@CrossOrigin("*")
public class TransfertAgentController {

      @Autowired
      private TransfertAgentService transfertAgentService;

      @PostMapping("/transfer")
      public void transfertFromAccountToAnother(@RequestBody TransfertDTO transfertDTO) {
            transfertAgentService.transfertFromAccountToAnother(transfertDTO);
      }

      @PostMapping("/virment")
      public void virment(@RequestBody OperationDto operationDto) {
            transfertAgentService.virment(operationDto);
      }

      @PostMapping("/retirer")
      public void retirer(@RequestBody OperationDto operationDto) {
            transfertAgentService.retirer(operationDto);
      }
}
