package com.t0mpi9.captcha.config;

import cn.iautos.captcha.CaptchaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * <br/>
 * Created on 2018/9/13 14:42.
 *
 * @author zhubenle
 */
public abstract class AbstractCaptchaConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractCaptchaConfig.class);

    private Font[] font3D;
    private String captcha;
    private String[] fontNames;
    private int[] fontStyles;
    private Color borderColor;
    private int gifQuality;
    private int gifDelay;
    private int lastGifDelay;
    private int captchaSize;
    private int randomLineMin;
    private int randomLineMax;

    public AbstractCaptchaConfig() {
        this.captcha = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        this.fontNames = new String[]{"Algerian", "Arial", "Arial Black", "Agency FB", "Calibri", "Cambria", "Gadugi", "Georgia", "Consolas", "Comic Sans MS", "Courier New",
                "Gill sans", "Time News Roman", "Tahoma", "Quantzite", "Verdana"};
        this.fontStyles = new int[]{Font.BOLD, Font.ITALIC, Font.ROMAN_BASELINE, Font.PLAIN, Font.BOLD + Font.ITALIC};
        this.borderColor = Color.BLACK;
        this.gifQuality = 200;
        this.gifDelay = 250;
        this.lastGifDelay = gifDelay * 2;
        this.captchaSize = 4;
        this.randomLineMin = 20;
        this.randomLineMax = 155;
        try {
            Font asimovOu = Font.createFont(Font.TRUETYPE_FONT, CaptchaClient.class.getResourceAsStream("/AsimovOu.otf"));
            Font euroBold = Font.createFont(Font.TRUETYPE_FONT, CaptchaClient.class.getResourceAsStream("/Euro Bold.ttf"));
            Font kgSecondChancesSketch = Font.createFont(Font.TRUETYPE_FONT, CaptchaClient.class.getResourceAsStream("/KGSecondChancesSketch.ttf"));
            Font princetownStd = Font.createFont(Font.TRUETYPE_FONT, CaptchaClient.class.getResourceAsStream("/PrincetownStd.otf"));
            Font adieresis = Font.createFont(Font.TRUETYPE_FONT, CaptchaClient.class.getResourceAsStream("/Adieresis.ttf"));
            Font frederickatheGreat = Font.createFont(Font.TRUETYPE_FONT, CaptchaClient.class.getResourceAsStream("/FrederickatheGreat-Regular.ttf"));
            Font jazzScript2 = Font.createFont(Font.TRUETYPE_FONT, CaptchaClient.class.getResourceAsStream("/JazzScript2-Caps.ttf"));
            Font arvo = Font.createFont(Font.TRUETYPE_FONT, CaptchaClient.class.getResourceAsStream("/Arvo-BoldItalic.ttf"));
            this.font3D = new Font[]{asimovOu, euroBold, kgSecondChancesSketch, princetownStd, adieresis, frederickatheGreat, jazzScript2, arvo};

        } catch (Exception e) {
            LOGGER.error("载入3D字体异常字体异常", e);
        }
    }

    /**
     * 创建验证码的字符
     *
     * @return 字符
     */
    public String captcha() {
        return captcha;
    }

    /**
     * 普通字体列表
     *
     * @return 列表
     */
    public String[] fontNames() {
        return fontNames;
    }

    public Font[] font3D() {
        return font3D;
    }

    /**
     * 字体样式列表
     *
     * @return 列表
     */
    public int[] fontStyles() {
        return fontStyles;
    }

    public Color borderColor() {
        return borderColor;
    }

    public int gifQuality() {
        return gifQuality;
    }

    public int gifDelay() {
        return gifDelay;
    }

    public int lastGifDelay() {
        return lastGifDelay;
    }

    public int randomLineMin() {
        return randomLineMin;
    }

    public int randomLineMax() {
        return randomLineMax;
    }

    /**
     * 创建验证码的字符个数
     *
     * @return 字符数
     */
    public int captchaSize() {
        return captchaSize;
    }

    /**
     * 验证码图片宽度
     *
     * @return 宽度
     */
    public abstract int imageWidth();

    /**
     * 验证码图片高度
     *
     * @return 高度
     */
    public abstract int imageHigh();

    /**
     * 生成验证码的类型
     *
     * @return 类型
     */
    public abstract CaptchaClient.Type type();
}
