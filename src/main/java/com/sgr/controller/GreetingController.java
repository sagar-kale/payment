package com.sgr.controller;

import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.response.PaymentOrderDetailsResponse;
import com.sgr.api.InstaMojoService;
import com.sgr.builder.PaymentOrderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@CrossOrigin
public class GreetingController {
    @Autowired
    InstaMojoService instaMojoService;

    @GetMapping("/greeting")
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
        return "result";
    }

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String webhookHandle(MultiValueMap<String, String> multiValueMap, Model model) {
        System.out.println(multiValueMap);
        return "result";
    }

}
