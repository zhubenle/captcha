package com.t0mpi9.captcha;

import org.junit.Test;

import java.io.FileOutputStream;

/**
 * <br/>
 * Created on 2018/10/24 13:58.
 *
 * @author zhubenle
 */
public class CaptchaClientTest {

    @Test
    public void testCaptcha() throws Exception{
        CaptchaClient captchaClient = new CaptchaClient();
        captchaClient.generate(new FileOutputStream("/Users/benlezhu/Downloads/MIX_GIF.gif"));
    }
}
