package com.sgr.controller;

import com.instamojo.wrapper.model.Payment;
import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.response.PaymentOrderDetailsResponse;
import com.sgr.builder.PaymentOrderBuilder;
import com.sgr.domain.RandomIdGenerator;
import com.sgr.service.InstaMojoService;
import com.sgr.validator.OrderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class PayController {
    @Autowired
    private InstaMojoService instaMojoService;
    @Autowired
    private OrderValidator orderValidator;

    @GetMapping("/order")
    public String retrieveOrderPage(PaymentOrder paymentOrder) {
        return "order";
    }

    @PostMapping("/order")
    public String orderProceed(@Valid PaymentOrder paymentOrder, BindingResult bindingResult, HttpServletResponse response) {
        PrintWriter writer = null;
        response.setContentType("text/html");
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        paymentOrder.setRedirectUrl(PaymentOrderBuilder.PAYMENT_ORDER_REDIRECT_URL);
        paymentOrder.setWebhookUrl(PaymentOrderBuilder.PAYMENT_ORDER_WEBHOOK_URL);
        paymentOrder.setDescription(PaymentOrderBuilder.PAYMENT_ORDER_DESCRIPTION + " by " + paymentOrder.getName());
        paymentOrder.setId(new RandomIdGenerator().generateId());
        paymentOrder.setCurrency(PaymentOrderBuilder.PAYMENT_ORDER_CURRENCY);
        paymentOrder.setTransactionId(PaymentOrderBuilder.getTransactionId());
        if (paymentOrder.getAmount() == 0 || paymentOrder.getAmount() > 1000)
            paymentOrder.setAmount(1000D);

        orderValidator.validate(paymentOrder, bindingResult);

        if (bindingResult.hasErrors()) {
            return "order";
        }
        String paymentUrl = instaMojoService.createPaymentOrder(paymentOrder);
        writer.println("<center><h1>Please don't refresh and reload this page</h1></center>");
        /*  writer.println("<script>location='" + paymentUrl + "'</script>"); */
        return "redirect:" + paymentUrl;
    }

    @GetMapping(value = "/redirect")
    public String redirect(@RequestParam(name = "id") String id, @RequestParam(name = "transaction_id") String transactionId, @RequestParam("payment_id") String paymentId, Model model) {
        PaymentOrderDetailsResponse paymentOrderDetailsResponse = instaMojoService.retrievePaymentOrderDetailsByTransactionId(transactionId);
        model.addAttribute("orderStatus", paymentOrderDetailsResponse);
        System.out.println("ID ::::: " + paymentOrderDetailsResponse.getId());
        Payment pay = new Payment();
        for (Payment payment : paymentOrderDetailsResponse.getPayments()) {
            System.out.println("Payment ::: " + payment);
            pay.setId(payment.getId());
            pay.setStatus(payment.getStatus());
        }
        model.addAttribute("payment", pay);


        return "result";
    }

    @PostMapping(value = "/webhook")
    public void webhookHandle(@RequestBody MultiValueMap<String, String> webhook, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Webhook Entry :::: " + webhook);

        System.out.println(request.getParameterMap().toString());
        response.setStatus(200);
        try {
            response.getWriter().println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
