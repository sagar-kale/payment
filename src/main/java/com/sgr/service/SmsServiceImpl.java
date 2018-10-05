package com.sgr.service;

import com.sgr.domain.SmsInfo;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

@Service("smsService")
public class SmsServiceImpl implements SmsService {

    // Your authentication key
    private static final String authkey = "104598AMDTqss756b9ef92";

    private static final String route = "4"; // 4 because i want to use only
    // transactional sms
    private static final String country = "91";

    // encoding message
    private String encoded_message = null;

    // Send SMS API
    private String mainUrl = "https://control.msg91.com/api/sendhttp.php?";

    // To check remaining transactional sms
    private static final String ChECK_SMS_URL = "https://control.msg91.com/api/balance.php?authkey=" + authkey
            + "&type=4";

    private URLConnection myURLConnection = null;
    private URL myURL = null;
    private BufferedReader reader = null;


    @Override
    public boolean sendMessage(SmsInfo sms) throws Exception {
        boolean isSuccess = false;
        String process = "";
        // Prepare parameter string
        encoded_message = URLEncoder.encode(sms.getMessage(), "UTF-8");
        StringBuilder sbPostData = new StringBuilder(mainUrl);
        sbPostData.append("authkey=" + authkey);
        sbPostData.append("&mobiles=" + sms.getMobiles());
        sbPostData.append("&message=" + encoded_message);
        sbPostData.append("&route=" + route);
        sbPostData.append("&sender=" + sms.getSenderId());
        sbPostData.append("&scountry=" + country);
        sbPostData.append("&response=json");
        // sbPostData.append("&flash=1"); for flash sms only

        // final string
        String finalUrl = sbPostData.toString();
        // prepare connection
        myURL = new URL(finalUrl);
        myURLConnection = myURL.openConnection();
        myURLConnection.connect();
        reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
        // reading response
        String response;
        JSONObject obj;
        while ((response = reader.readLine()) != null) {
            // print response
            if (response.contains("error"))
                return isSuccess;
            obj = new JSONObject(response);
            process = (String) obj.get("type");
            System.out.println("in sentmsg" + obj);
        }
        // finally close connection
        reader.close();
        if (process.equals("success"))
            isSuccess = true;
        return isSuccess;

    }

    @Override
    public int getRemainedSmsCount(SmsInfo sms) throws Exception {

        myURL = new URL(ChECK_SMS_URL);
        myURLConnection = myURL.openConnection();
        myURLConnection.connect();
        reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
        // reading response
        String response;
        int res = 0;
        while ((response = reader.readLine()) != null) {
            // print response
            if (response.contains("error")) {
                res = -1;
                break;
            }
            if (StringUtils.isNumeric(response))
                res = Integer.parseInt(response);
            System.out.println("remained sms=" + response);
        }
        // finally close connection
        reader.close();
        return res;
    }

}
