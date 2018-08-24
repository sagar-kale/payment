package com.sgr.util;

import com.instamojo.wrapper.api.Instamojo;
import com.instamojo.wrapper.api.InstamojoImpl;
import com.instamojo.wrapper.exception.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Api {
    private static final Logger LOGGER = LoggerFactory.getLogger(Api.class);
    public static Instamojo api = null;

    public static Instamojo getApi() {
        try {
            // gets the reference to the instamojo api
            if (api == null)
                api = InstamojoImpl.getApi(ConfigConstants.TEST_CLIENT_ID, ConfigConstants.TEST_CLIENT_SECRET, ConfigConstants.INSTAMOJO_TEST_API_ENDPOINT, ConfigConstants.INSTAMOJO_TEST_AUTH_ENDPOINT);
        } catch (ConnectionException e) {
            LOGGER.error(e.toString(), e);
        }
        return api;

    }
}
