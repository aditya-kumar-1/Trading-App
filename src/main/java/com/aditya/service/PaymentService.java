package com.aditya.service;

import com.aditya.domain.PaymentMethod;
import com.aditya.model.PaymentOrder;
import com.aditya.model.User;
import com.aditya.response.PaymentResponse;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

public interface PaymentService {
    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id) throws Exception;

    Boolean ProccedPaymentOrder (PaymentOrder paymentOrder,
                                 String paymentId) throws RazorpayException;
    PaymentResponse createRazorpayPaymentLing(User user, Long amount,Long orderId) throws Exception;

    PaymentResponse createStripePaymentLing(User user, Long amount,Long orderId) throws StripeException;
}
