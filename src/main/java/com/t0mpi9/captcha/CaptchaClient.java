package com.t0mpi9.captcha;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.t0mpi9.captcha.config.AbstractCaptchaConfig;
import com.t0mpi9.captcha.config.DefaultCaptchaConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <br/>
 * Created on 2018/9/12 17:44.
 *
 * @author zhubenle
 */
public class CaptchaClient {

    private final static int MAX_RGB = 255;
    private final static int OVAL_COUNT = 2;


    private AbstractCaptchaConfig captchaConfig;

    public CaptchaClient() {
        this.captchaConfig = new DefaultCaptchaConfig();
    }

    public CaptchaClient(AbstractCaptchaConfig captchaConfig) {
        this.captchaConfig = captchaConfig;
    }

    /**
     * 使用指定源生成验证码
     *
     * @return 生成的验证码
     */
    private String generateCaptcha() {
        String sources = captchaConfig.captcha();
        int captchaSize = captchaConfig.captchaSize();
        int codesLen = sources.length();
        StringBuilder verifyCode = new StringBuilder(captchaSize);
        for (int i = 0; i < captchaSize; i++) {
            verifyCode.append(sources.charAt(ThreadLocalRandom.current().nextInt(codesLen - 1)));
        }

        return verifyCode.toString();
    }

    /**
     * 输出指定验证码图片流
     * @param os
     *         流
     *
     * @throws IOException
     *         异常
     */
    public String generate(OutputStream os) throws IOException {
        String code;
        try {
            int w = captchaConfig.imageWidth();
            int h = captchaConfig.imageHigh();
            code = generateCaptcha();
            Type type = captchaConfig.type();


            int codeSize = code.length();
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            ThreadLocalRandom random = ThreadLocalRandom.current();
            Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 设置边框色
            g2.setColor(captchaConfig.borderColor());
            g2.fillRect(0, 0, w, h);

            Color c = getRandomColor(200, 250);
            // 设置背景色
            g2.setColor(c);
            g2.fillRect(0, 2, w, h - 4);

            char[] charts = code.toCharArray();
            for (char chart : charts) {
                // 设置背景色
                g2.setColor(c);
                g2.setFont(getRandomFont(h, type));
                g2.fillRect(0, 2, w, h - 4);
            }

            // 1.绘制干扰线
            drawLine(w, h, type, g2);

            // 2.添加噪点
            drawYawp(w, h, type, image);

            // 3.使图片扭曲
            shear(g2, w, h, c);

            char[] chars = code.toCharArray();

            int drawCharsY = h - random.nextInt(5);

            if (Type.MIX_GIF.equals(type) || Type.GIF.equals(type) || Type.GIF_3D.equals(type)) {
                drawGif(os, w, h, type, codeSize, image, g2, chars);
            } else {
                drawJpg(os, w, h, type, codeSize, image, g2, chars);
            }
        } finally {
            os.close();
        }
        return code.toUpperCase();
    }

    /**
     * 画jpg静态图片
     */
    private void drawJpg(OutputStream os, int w, int h, Type type, int codeSize, BufferedImage image, Graphics2D g2, char[] chars) throws IOException {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int drawCharsY = h - random.nextInt(5);
        for (int i = 0; i < codeSize; i++) {
            g2.setColor(getRandomColor(100, 160));
            g2.setFont(getRandomFont(h, type));

            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * random.nextDouble() * (random.nextBoolean() ? 1 : -1), (w / (float) codeSize) * i + (h - 4) / 2.0, h / 2.0);
            g2.setTransform(affine);
            g2.drawOval(random.nextInt(w), random.nextInt(h), 5 + random.nextInt(10), 5 + random.nextInt(10));
            g2.drawLine(random.nextInt(w), random.nextInt(h), random.nextInt(w), random.nextInt(h));
            g2.drawChars(chars, i, 1, ((w - 10) / codeSize) * i + 5, drawCharsY);
        }

        g2.dispose();
        ImageIO.write(image, "jpg", os);
    }

    /**
     * 画gif动态图片
     */
    private void drawGif(OutputStream os, int w, int h, Type type, int codeSize, BufferedImage image, Graphics2D g2, char[] chars) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int drawCharsY = h - random.nextInt(5);
        AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
        // 生成字符
        gifEncoder.start(os);
        gifEncoder.setQuality(captchaConfig.gifQuality());
        gifEncoder.setDelay(captchaConfig.gifDelay());
        gifEncoder.setRepeat(0);

        AlphaComposite ac3;
        for (int i = 0; i < codeSize; i++) {
            g2.setColor(getRandomColor(100, 160));
            g2.setFont(getRandomFont(h, type));

            double rd = random.nextDouble();
            boolean rb = random.nextBoolean();
            for (int j = 0; j < codeSize; j++) {
                AffineTransform affine = new AffineTransform();
                affine.setToRotation(Math.PI / 4 * rd * (rb ? 1 : -1), (w / (float) codeSize) * i + (h - 6) / 2.0, h / 2.0);
                g2.setTransform(affine);
                g2.drawChars(chars, i, 1, ((w - 10) / codeSize) * i + 5, drawCharsY);

                ac3 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha(j, i, codeSize));
                g2.setComposite(ac3);
                g2.drawOval(random.nextInt(w), random.nextInt(h), 5 + random.nextInt(10), 5 + random.nextInt(10));
                g2.drawLine(random.nextInt(w), random.nextInt(h), random.nextInt(w), random.nextInt(h));
                gifEncoder.addFrame(image);
                image.flush();
            }
        }
        gifEncoder.setDelay(captchaConfig.lastGifDelay());
        gifEncoder.addFrame(image);
        image.flush();
        gifEncoder.finish();
        g2.dispose();
    }

    /**
     * 处理噪声
     */
    private void drawYawp(int w, int h, Type type, BufferedImage image) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        float yawpRate = 0.05f;
        if (!(Type.MIX2.equals(type) || Type.MIX_GIF.equals(type) || Type.STATIC_3D.equals(type) || Type.GIF_3D.equals(type))) {
            // 噪声率
            yawpRate = getRandomDrawPoint();
        }
        int area = (int) (yawpRate * w * h);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }
    }

    /**
     * 画干扰线
     */
    private void drawLine(int w, int h, Type type, Graphics2D g2) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // 设置线条的颜色
        g2.setColor(getRandomColor(160, 200));
        for (int i = 0; i < getRandomDrawLine(); i++) {
            int x = random.nextInt(w - 1);
            int y = random.nextInt(h - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }
    }

    /**
     * 获取随机颜色
     */
    private Color getRandomColor(int fc, int bc) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (fc > MAX_RGB) {
            fc = MAX_RGB;
        }
        if (bc > MAX_RGB) {
            bc = MAX_RGB;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private int getRandomIntColor() {
        int[] rgb = new int[3];
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = ThreadLocalRandom.current().nextInt(MAX_RGB);
        }
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    /**
     * 随机字体、随机风格、随机大小
     *
     * @param h
     *         验证码图片高
     *
     * @return 字体
     */
    private Font getRandomFont(int h, Type type) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // 字体
        String name = captchaConfig.fontNames()[random.nextInt(captchaConfig.fontNames().length)];
        // 字体样式
        int style = captchaConfig.fontStyles()[random.nextInt(captchaConfig.fontStyles().length)];
        // 字体大小
        int size = getRandomFontSize(h);

        if (Type.GIF_3D.equals(type) || Type.STATIC_3D.equals(type)) {
            return getFont(size, style);
        } else if (Type.MIX2.equals(type) || Type.MIX_GIF.equals(type)) {
            if (random.nextBoolean()) {
                return new Font(name, style, size);
            } else {
                return getFont(size, style);
            }
        } else {
            return new Font(name, style, size);
        }
    }

    private Font getFont(int fontSize, int fontStyle) {
        return captchaConfig.font3D()[ThreadLocalRandom.current().nextInt(captchaConfig.font3D().length)].deriveFont(fontStyle, fontSize);
    }

    /**
     * 干扰线按范围获取随机数
     */
    private int getRandomDrawLine() {
        if (captchaConfig.randomLineMax() == captchaConfig.randomLineMin()) {
            return captchaConfig.randomLineMax();
        }
        int min = Math.min(captchaConfig.randomLineMin(), captchaConfig.randomLineMax());
        int max = Math.max(captchaConfig.randomLineMin(), captchaConfig.randomLineMax());
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * 噪点数率按范围获取随机数
     */
    private float getRandomDrawPoint() {
        float min = 0.05f;
        float max = 0.1f;
        return min + ((max - min) * ThreadLocalRandom.current().nextFloat());
    }

    /**
     * 获取字体大小按范围随机
     *
     * @param h
     *         验证码图片高
     */
    private int getRandomFontSize(int h) {
        int min = h - 10;
        // int max = 46;
        return ThreadLocalRandom.current().nextInt(5, 10) + min;
    }

    /**
     * 字符和干扰线扭曲
     *
     * @param g
     *         绘制图形的java工具类
     * @param w1
     *         验证码图片宽
     * @param h1
     *         验证码图片高
     * @param color
     *         颜色
     */
    private void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    /**
     * x轴扭曲
     *
     * @param g
     *         绘制图形的java工具类
     * @param w1
     *         验证码图片宽
     * @param h1
     *         验证码图片高
     * @param color
     *         颜色
     */
    private void shearX(Graphics g, int w1, int h1, Color color) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int period = random.nextInt(2);

        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            g.setColor(color);
            g.drawLine((int) d, i, 0, i);
            g.drawLine((int) d + w1, i, w1, i);
        }
    }

    /**
     * y轴扭曲
     *
     * @param g
     *         绘制图形的java工具类
     * @param w1
     *         验证码图片宽
     * @param h1
     *         验证码图片高
     * @param color
     *         颜色
     */
    private void shearY(Graphics g, int w1, int h1, Color color) {
        int period = ThreadLocalRandom.current().nextInt(40) + 10;

        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            g.setColor(color);
            g.drawLine(i, (int) d, i, 0);
            g.drawLine(i, (int) d + h1, i, h1);
        }
    }

    /**
     * 获取透明度,从0到1,自动计算步长
     *
     * @return float 透明度
     */
    private float getAlpha(int i, int j, int verifySize) {
        int num = i + j;
        float r = (float) 1 / verifySize, s = (verifySize + 1) * r;
        return num > verifySize ? (num * r - s) : num * r;
    }

    /**
     * 3D: 3D中空自定义字体
     * GIF：普通动态GIF
     * GIF3D：3D动态GIF
     * mix2: 普通字体和3D字体混合
     * mixGIF: 混合动态GIF
     */
    public enum Type {
        /**
         *
         */
        STATIC_3D("3D中空自定义字体"),
        GIF("普通动态GIF"),
        GIF_3D("3D动态GIF"),
        MIX2("普通字体和3D字体混合"),
        MIX_GIF("混合动态GIF");
        private String desc;

        Type(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }
}
