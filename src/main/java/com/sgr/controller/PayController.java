package com.sgr.controller;

import com.instamojo.wrapper.model.Payment;
import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.response.PaymentOrderDetailsResponse;
import com.sgr.builder.PaymentOrderBuilder;
import com.sgr.domain.CurrentUser;
import com.sgr.domain.RandomIdGenerator;
import com.sgr.service.InstaMojoService;
import com.sgr.validator.OrderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Controller
public class PayController {
    @Autowired
    private InstaMojoService instaMojoService;
    @Autowired
    private OrderValidator orderValidator;

    @GetMapping("/order")
    public String retrieveOrderPage(PaymentOrder paymentOrder) {

        CurrentUser user = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        paymentOrder.setName(user.getFirstName() + " " + user.getLastName());
        paymentOrder.setEmail(user.getEmail());
        paymentOrder.setPhone(user.getPhone());
        return "user/order";
    }

    @GetMapping(value = "/process_payment", produces = MediaType.TEXT_HTML_VALUE)
    public void processPayment(HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Map<String, ?> map = RequestContextUtils.getInputFlashMap(httpServletRequest);
        PrintWriter writer = null;
        response.setContentType("text/html");
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (map == null) {
            String appUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + (httpServletRequest.getScheme().equalsIgnoreCase("http") ? ":8080/order" : "/order");
            writer.println("<script>location='" + appUrl + "'</script>");
            return;
        }
        String paymentUrl = (String) map.get("paymentUrl");
        StringBuilder builder = new StringBuilder();
        builder.append("<center><h1>Please don't refresh and reload this page</h1></center>");
        builder.append("<script>location='" + paymentUrl);
        builder.append("'</script>");
        System.out.println("html content ::: " + builder);


        writer.println(builder);
    }

    @PostMapping("/order")
    public String orderProceed(@Valid PaymentOrder paymentOrder, BindingResult bindingResult, RedirectAttributes
            redirectAttributes, Model model) {

        paymentOrder.setRedirectUrl(PaymentOrderBuilder.PAYMENT_ORDER_REDIRECT_URL);
        paymentOrder.setWebhookUrl(PaymentOrderBuilder.PAYMENT_ORDER_WEBHOOK_URL);
        paymentOrder.setDescription(PaymentOrderBuilder.PAYMENT_ORDER_DESCRIPTION + " by " + paymentOrder.getName());
        paymentOrder.setId(new RandomIdGenerator().generateId());
        paymentOrder.setCurrency(PaymentOrderBuilder.PAYMENT_ORDER_CURRENCY);
        paymentOrder.setTransactionId(PaymentOrderBuilder.getTransactionId());

        orderValidator.validate(paymentOrder, bindingResult);

        if (bindingResult.hasErrors()) {
            return "user/order";
        }
        if (paymentOrder.getAmount() == 0 || paymentOrder.getAmount() > 1000)
            paymentOrder.setAmount(1000D);
        String paymentUrl = instaMojoService.createPaymentOrder(paymentOrder);
        if (paymentUrl == null) {
            model.addAttribute("broke", "Something went wrong , Please be patient administrator will reach out to you");
            return "user/order";
        }
        System.out.println("Payment url :: " + paymentUrl);
        redirectAttributes.addFlashAttribute("paymentUrl", paymentUrl);

        return "redirect:process_payment";
    }

    @GetMapping(value = "/redirect")
    public String redirect(@RequestParam(name = "id") String id, @RequestParam(name = "transaction_id") String
            transactionId, @RequestParam("payment_id") String paymentId, RedirectAttributes redirectAttributes) {
        PaymentOrderDetailsResponse paymentOrderDetailsResponse = instaMojoService.retrievePaymentOrderDetailsByTransactionId(transactionId);
        redirectAttributes.addFlashAttribute("orderStatus", paymentOrderDetailsResponse);
        System.out.println("ID ::::: " + paymentOrderDetailsResponse.getId());
        Payment pay = new Payment();
        for (Payment payment : paymentOrderDetailsResponse.getPayments()) {
            System.out.println("Payment ::: " + payment);
            pay.setId(payment.getId());
            pay.setStatus(payment.getStatus());
        }
        redirectAttributes.addFlashAttribute("payment", pay);


        return "redirect:/order_status";
    }

    @GetMapping(value = "/order_status")
    public String order_result(RedirectAttributes redirectAttributes) {
        return "user/order_status";
    }

    @PostMapping(value = "/webhook")
    public void webhookHandle(@RequestBody MultiValueMap<String, String> webhook, HttpServletRequest
            request, HttpServletResponse response) {
        System.out.println("Webhook Entry :::: " + webhook);

        System.out.println(request.getParameterMap().toString());
        response.setStatus(200);
        try {
            response.getWriter().println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParams(MissingServletRequestParameterException ex) {
        return "redirect:home";
    }


}
