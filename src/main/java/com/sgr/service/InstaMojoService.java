package com.sgr.service;

import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.response.PaymentOrderDetailsResponse;

public interface InstaMojoService {

    String createPaymentOrder(PaymentOrder paymentOrder);

    PaymentOrderDetailsResponse retrievePaymentOrderDetailsByTransactionId(String transactionId);
}
