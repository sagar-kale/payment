package com.sgr.api;

import com.instamojo.wrapper.api.Instamojo;
import com.instamojo.wrapper.exception.ConnectionException;
import com.instamojo.wrapper.exception.InvalidPaymentOrderException;
import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.response.CreatePaymentOrderResponse;
import com.instamojo.wrapper.response.PaymentOrderDetailsResponse;
import com.sgr.util.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service(value = "instaMojoService")
public class InstaMojoServiceImpl implements InstaMojoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstaMojoServiceImpl.class);

    @Override
    public String createPaymentOrder(PaymentOrder order) {
        System.out.println("order id::: " + order.getId());
        System.out.println("order name::: " + order.getName());
        System.out.println("Webhook url::: " + order.getWebhookUrl());
        Instamojo api = null;
        String paymentUrl = null;
        api = Api.getApi();

        boolean isOrderValid = order.validate();

        if (isOrderValid) {
            try {
                CreatePaymentOrderResponse createPaymentOrderResponse = api.createNewPaymentOrder(order);
                // print the status of the payment order.
                System.out.println("Payment link #1 ::: " + createPaymentOrderResponse.getPaymentOptions().getPaymentUrl());
                System.out.println("Redirect link #2 ::: " + createPaymentOrderResponse.getPaymentOrder().getRedirectUrl());
                System.out.println("Webhook link #3 ::: " + createPaymentOrderResponse.getPaymentOrder().getWebhookUrl());
                System.out.println(createPaymentOrderResponse.getPaymentOrder().getStatus());
                paymentUrl = createPaymentOrderResponse.getPaymentOptions().getPaymentUrl();
            } catch (InvalidPaymentOrderException e) {
                LOGGER.error(e.toString(), e);

                if (order.isTransactionIdInvalid()) {
                    System.out.println("Transaction id is invalid. This is mostly due to duplicate transaction id.");
                }
                if (order.isCurrencyInvalid()) {
                    System.out.println("Currency is invalid.");
                }
            } catch (ConnectionException e) {
                LOGGER.error(e.toString(), e);
            }
        } else {
            // inform validation errors to the user.
            if (order.isTransactionIdInvalid()) {
                System.out.println("Transaction id is invalid.");
            }
            if (order.isAmountInvalid()) {
                System.out.println("Amount can not be less than 9.00.");
            }
            if (order.isCurrencyInvalid()) {
                System.out.println("Please provide the currency.");
            }
            if (order.isDescriptionInvalid()) {
                System.out.println("Description can not be greater than 255 characters.");
            }
            if (order.isEmailInvalid()) {
                System.out.println("Please provide valid Email Address.");
            }
            if (order.isNameInvalid()) {
                System.out.println("Name can not be greater than 100 characters.");
            }
            if (order.isPhoneInvalid()) {
                System.out.println("Phone is invalid.");
            }
            if (order.isRedirectUrlInvalid()) {
                System.out.println("Please provide valid Redirect url.");
            }

            if (order.isWebhookInvalid()) {
                System.out.println("Provide a valid webhook url");
            }
        }
        return paymentUrl;
    }

    @Override
    public PaymentOrderDetailsResponse retrievePaymentOrderDetailsByTransactionId(String transactionId) {

        Instamojo api = Api.getApi();
        PaymentOrderDetailsResponse paymentOrderDetailsResponse = null;
        try {
            paymentOrderDetailsResponse = api.getPaymentOrderDetailsByTransactionId(transactionId);
        } catch (ConnectionException e) {
            LOGGER.error(e.toString(), e);
        }
        System.out.println(paymentOrderDetailsResponse.getStatus());
        return paymentOrderDetailsResponse;
    }
}
