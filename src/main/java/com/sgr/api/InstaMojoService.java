package com.sgr.api;

import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.response.PaymentOrderDetailsResponse;

import java.util.List;

public interface InstaMojoService {

    String createPaymentOrder(PaymentOrder paymentOrder);

    PaymentOrderDetailsResponse retrievePaymentOrderDetailsByTransactionId(String transactionId);
}
