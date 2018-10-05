package com.sgr.service;

import com.sgr.domain.SmsInfo;

public interface SmsService {
    boolean sendMessage(SmsInfo sms) throws Exception;

    int getRemainedSmsCount(SmsInfo sms) throws Exception;
}
