package org.zenframework.captcha;

/**
 * Captcha generator
 * Created by Zeal on 2019/1/20 0020.
 */
public interface CaptchaGenerator {

    /**
     * Generate captcha
     * @return
     */
    String generateCaptcha(int captchaLength);

}
