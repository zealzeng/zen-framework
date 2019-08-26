/**
 * 
 */
package org.zenframework.sms;

import com.alibaba.fastjson.JSONObject;
import org.zenframework.util.HttpUtils;
import org.zenframework.util.JSONUtils;
import org.zenframework.util.StringUtils;
import org.zenframework.common.Result;
import org.zenframework.web.error.WebError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zeal
 *
 */
public class YunPianServiceImpl implements SMService {
	
	private static final Logger logger = LogManager.getLogger(YunPianServiceImpl.class);
	
	private String apiKey = null;
	
	private String singleURL = "https://sms.yunpian.com/v2/sms/single_send.json";
	
	public YunPianServiceImpl() {
	}
	
	public YunPianServiceImpl(String apiKey) {
		this.apiKey = apiKey;
	}
	

	/**
	 * Send single message
	 */
	@Override
	public Result<String> send(String mobile, String message, String callbackURL) {
        Result<String> httpResult = null;
        try {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("apikey", this.apiKey);
            paramMap.put("mobile", mobile);
            paramMap.put("text", message);
//            paramMap.put("extend", "");
//            paramMap.put("register", "");
//            paramMap.put("uid", "");
            if (StringUtils.isNotEmpty(callbackURL)) {
                paramMap.put("callback_url", callbackURL);
            }
            httpResult = HttpUtils.post(this.singleURL, paramMap);
            if (logger.isDebugEnabled()) {
            	logger.debug(httpResult);
            }
            String response = httpResult.getResultEntity();
            JSONObject map = (JSONObject) JSONUtils.parse(response);
            Integer code = map.getInteger("code");
            String msg = map.getString("msg");
            
            if (httpResult.getResultCode() == 200) {
                if (code != null && code.intValue() == 0) {
                	return new Result<>(WebError.NO_ERROR, msg, null);
                }
                else {
                	return new Result<>(WebError.UNKNOWN_ERROR, StringUtils.isEmpty(msg) ? ("Failed to send message to " + mobile) : msg, null);
                }
            }
            else {
            	String error  = StringUtils.isEmpty(msg) ? ("Failed to send message to " + mobile) : msg;
                logger.error(error);
                return new Result<String>(WebError.UNKNOWN_ERROR, error, null);
            }
        }
        catch (Exception e) {
        	String error = "Failed to send message to " + mobile + "." + e.toString();
            logger.error(error, e);
            return new Result<String>(WebError.UNKNOWN_ERROR, error, null);
        }

	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSingleURL() {
		return singleURL;
	}

	public void setSingleURL(String singleURL) {
		this.singleURL = singleURL;
	}
	
	public static void main(String[] args) throws Exception {
		String CAPTCHA_MASSAGE_MODULE = "测试错误的情况";
		String apiKey = "";
		YunPianServiceImpl sms = new YunPianServiceImpl(apiKey);
		String mobile = "18107379966";
		String text = String.format(CAPTCHA_MASSAGE_MODULE, "1234");
		System.out.println(sms.send(mobile, text, null));
	}

}
