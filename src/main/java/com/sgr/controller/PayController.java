package com.sgr.controller;

import com.instamojo.wrapper.model.Payment;
import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.response.PaymentOrderDetailsResponse;
import com.sgr.builder.PaymentOrderBuilder;
import com.sgr.service.InstaMojoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class PayController {
    @Autowired
    private InstaMojoService instaMojoService;

    @GetMapping("/greeting")
    public String view(Model model) {
        return "order";
    }

    @GetMapping("/order")
    public void greeting(@RequestParam(name = "orderId") String id, @RequestParam(name = "name") String name, HttpServletResponse response) {
        PrintWriter writer = null;
        response.setContentType("text/html");
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PaymentOrderBuilder builder = new PaymentOrderBuilder();
        PaymentOrder order = builder.build();
        order.setName(name);
        order.setId(id);
        String paymentUrl = instaMojoService.createPaymentOrder(order);
        writer.println("<center><h1>Please don't refresh and reload this page</h1></center>");
        writer.println("<script>location='" + paymentUrl + "'</script>");
        writer.close();
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
