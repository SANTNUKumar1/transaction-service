package com.paypal.transaction_service.client;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.paypal.transaction_service.dto.dto.CaptureRequest;
import com.paypal.transaction_service.dto.dto.CreditRequest;
import com.paypal.transaction_service.dto.dto.DebitRequest;
import com.paypal.transaction_service.dto.dto.HoldRequest;
import com.paypal.transaction_service.dto.dto.HoldResponse;
import com.paypal.transaction_service.dto.dto.WalletResponse;


@FeignClient(name = "wallet-service", url = "http://localhost:8088/api/v1/wallets")
public interface WalletClient {

    @PostMapping("/debit")
    WalletResponse debit(@RequestBody DebitRequest request);

    @PostMapping("/credit")
    WalletResponse credit(@RequestBody CreditRequest request);

    @PostMapping("/hold")
    HoldResponse placeHold(@RequestBody HoldRequest request);

    @PostMapping("/capture")
    WalletResponse capture(@RequestBody CaptureRequest request);

    @PostMapping("/release/{holdReference}")
    HoldResponse release(@PathVariable String holdReference);

    @GetMapping("/{userId}")
    WalletResponse getWallet(@PathVariable Long userId);
}