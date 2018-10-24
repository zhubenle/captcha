package com.t0mpi9.captcha.config;


import com.t0mpi9.captcha.CaptchaClient;

/**
 * <br/>
 * Created on 2018/9/13 14:59.
 *
 * @author zhubenle
 */
public class DefaultCaptchaConfig extends AbstractCaptchaConfig{

    @Override
    public int imageWidth() {
        return 150;
    }

    @Override
    public int imageHigh() {
        return 35;
    }

    @Override
    public CaptchaClient.Type type() {
        return CaptchaClient.Type.MIX_GIF;
    }

    @Override
    public int lastGifDelay() {
        return super.lastGifDelay() * 10;
    }
}
