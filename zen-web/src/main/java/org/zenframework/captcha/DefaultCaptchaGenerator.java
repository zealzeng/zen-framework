package org.zenframework.captcha;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Zeal on 2019/1/20 0020.
 */
public class DefaultCaptchaGenerator implements CaptchaGenerator {

    //去掉了0,1,I,O,l,o几个容易混淆的字符
    private String randomCodes = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";

    public String getRandomCodes() {
        return randomCodes;
    }

    public void setRandomCodes(String randomCodes) {
        this.randomCodes = randomCodes;
    }

    @Override
    public String generateCaptcha(int captchaSize) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(captchaSize);
        for (int i = 0; i < captchaSize; ++i) {
            char c = randomCodes.charAt(random.nextInt(randomCodes.length()));
            sb.append(c);
        }
        return sb.toString();
    }
}
