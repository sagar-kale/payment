package com.sgr.validator;

import com.instamojo.wrapper.model.PaymentOrder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @Author sagar kale
 */

@Component
public class OrderValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return PaymentOrder.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PaymentOrder paymentOrder = (PaymentOrder) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "order.email.notEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "order.phone.notEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "amount", "order.amount.notEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "order.name.notEmpty");
        if (!paymentOrder.validate()) {
            if (paymentOrder.isEmailInvalid()) {
                errors.rejectValue("email", "order.email.invalid");
            }
            if (paymentOrder.isPhoneInvalid()) {
                errors.rejectValue("phone", "order.phone.invalid");
            }
            if (paymentOrder.isAmountInvalid() || paymentOrder.getAmount() < 9.0D) {
                errors.rejectValue("amount", "order.amount.invalid");
            }
            if (paymentOrder.getName().length() > 100) {
                errors.rejectValue("name", "order.name.invalid");
            }


        /*if (userService.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "Duplicate.user.username");
        }*/
        }
    }
}
