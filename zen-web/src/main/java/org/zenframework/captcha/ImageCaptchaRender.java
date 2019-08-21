package org.zenframework.captcha;

import org.zenframework.util.IOUtils;
import org.zenframework.image.GifEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 验证码工具类：
 * 随机字体、字体样式、字体大小（验证码图片宽度 - 8 ~ 验证码图片宽度 + 10）
 * 彩色字符 每个字符的颜色随机，一定会不相同
 * 随机字符 阿拉伯数字 + 小写字母 + 大写字母
 * 3D中空自定义字体，需要单独使用，只有阿拉伯数字和大写字母
 *
 * @author cgtu, Zeal
 * @date 2017年5月9日 下午7:27:55
 */
public class ImageCaptchaRender {

    /**
     * @param type 场景类型，login：登录，
     * coupons：领券 登录清晰化，领券模糊化
     * 3D: 3D中空自定义字体
     * GIF：普通动态GIF
     * GIF3D：3D动态GIF
     * mix2: 普通字体和3D字体混合
     * mixGIF: 混合动态GIF
     */

    public static final String TYPE_LOGIN = "login";

    public static final String TYPE_COUPONS = "coupons";

    public static final String TYPE_3D = "3D";

    public static final String TYPE_GIF = "GIF";

    public static final String TYPE_GIF3D = "GIF3D";

    public static final String TYPE_MIX2 = "mix2";

    public static final String TYPE_MIX_GIF = "mixGIF";

    private static Logger logger = LogManager.getLogger(ImageCaptchaRender.class);

    // 字体类型
    private static String[] fontName =
            {
                    "Algerian", "Arial", "Arial Black", "Agency FB", "Calibri", "Cambria", "Gadugi", "Georgia", "Consolas", "Comic Sans MS", "Courier New",
                    "Gill sans", "Time News Roman", "Tahoma", "Quantzite", "Verdana"
            };

    // 字体样式
    private static int[] fontStyle =
            {
                    Font.BOLD, Font.ITALIC, Font.ROMAN_BASELINE, Font.PLAIN, Font.BOLD + Font.ITALIC
            };

    // 颜色
    private static Color[] colorRange =
            {
                    Color.WHITE, Color.CYAN, Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.YELLOW, Color.GREEN, Color.BLUE,
                    Color.DARK_GRAY, Color.BLACK, Color.RED
            };

//    /**
//     * 随机类
//     */
//    private static Random random = new Random();
//
//    // 验证码来源范围，去掉了0,1,I,O,l,o几个容易混淆的字符
//    public static final String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";

    private Font baseFont;

    public ImageCaptchaRender() {
        try (InputStream in = ImageCaptchaRender.class.getResourceAsStream("3d.font")) {
            //in = ImageCaptchaRender.class.getResourceAsStream("3d.font");
            String fontByteStr = IOUtils.toString(in, "UTF-8");
            baseFont = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(hex2byte(fontByteStr)));//imgFontByte.getFontByteStr())));
        } catch (Exception e) {
            logger.error("new img font font format failed. e: " + e.getMessage(), e);
        }
    }

    private void doDraw(BufferedImage image, OutputStream os, ThreadLocalRandom rand, Graphics2D g2, int verifySize, int w, int h, String type, double rd, boolean rb, char[] chars) throws IOException {
        for (int i = 0; i < verifySize; i++) {
            g2.setColor(getRandColor(100, 160));
            g2.setFont(getRandomFont(h, type));

            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * rd * (rb ? 1 : -1), (w / verifySize) * i + (h - 4) / 2, h / 2);
            g2.setTransform(affine);
            g2.drawOval(rand.nextInt(w), rand.nextInt(h), 5 + rand.nextInt(10), 5 + rand.nextInt(10));
            g2.drawChars(chars, i, 1, ((w - 10) / verifySize) * i + 5, h / 2 + (h - 4) / 2 - 10);
        }
        g2.dispose();
        ImageIO.write(image, "jpg", os);
    }

    /**
     * 输出指定验证码图片流
     *
     * @param w    验证码图片的宽
     * @param h    验证码图片的高
     * @param os   流
     * @param code 验证码
     * @param type 场景类型，login：登录，
     *             coupons：领券 登录清晰化，领券模糊化
     *             3D: 3D中空自定义字体
     *             GIF：普通动态GIF
     *             GIF3D：3D动态GIF
     *             mix2: 普通字体和3D字体混合
     *             mixGIF: 混合动态GIF
     * @throws IOException
     */
    public void outputImage(int w, int h, OutputStream os, String code, String type) throws IOException {
        int verifySize = code.length();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        //Random rand = new Random();
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color[] colors = new Color[5];
        Color[] colorSpaces = colorRange;
        float[] fractions = new float[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorSpaces[rand.nextInt(colorSpaces.length)];
            fractions[i] = rand.nextFloat();
        }
        Arrays.sort(fractions);

        g2.setColor(Color.GRAY);// 设置边框色
        g2.fillRect(0, 0, w, h);

        Color c = getRandColor(200, 250);
        g2.setColor(c);// 设置背景色
        g2.fillRect(0, 2, w, h - 4);

        char[] charts = code.toCharArray();
        for (int i = 0; i < charts.length; i++) {
            g2.setColor(c);// 设置背景色
            g2.setFont(getRandomFont(h, type));
            g2.fillRect(0, 2, w, h - 4);
        }

        // 1.绘制干扰线
        //Random random = new Random();
        //ThreadLocalRandom random = ThreadLocalRandom.current();
        g2.setColor(getRandColor(160, 200));// 设置线条的颜色
        int lineNumbers = 20;
        if (type.equals("login") || type.contains("mix") || type.contains("3D")) {
            lineNumbers = 20;
        } else if (type.equals("coupons")) {
            lineNumbers = getRandomDrawLine();
        } else {
            lineNumbers = getRandomDrawLine();
        }
        for (int i = 0; i < lineNumbers; i++) {
            int x = rand.nextInt(w - 1);
            int y = rand.nextInt(h - 1);
            int xl = rand.nextInt(6) + 1;
            int yl = rand.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }

        // 2.添加噪点
        float yawpRate = 0.05f;
        if (type.equals("login") || type.contains("mix") || type.contains("3D")) {
            yawpRate = 0.05f; // 噪声率
        } else if (type.equals("coupons")) {
            yawpRate = getRandomDrawPoint(); // 噪声率
        } else {
            yawpRate = getRandomDrawPoint(); // 噪声率
        }
        int area = (int) (yawpRate * w * h);
        for (int i = 0; i < area; i++) {
            int x = rand.nextInt(w);
            int y = rand.nextInt(h);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }

        // 3.使图片扭曲
        shear(g2, w, h, c);

        char[] chars = code.toCharArray();
        double rd = rand.nextDouble();
        boolean rb = rand.nextBoolean();

        if (TYPE_LOGIN.equals(type)) {
            this.doDraw(image, os, rand, g2, verifySize, w, h, type, rd, rb, chars);
//            for (int i = 0; i < verifySize; i++) {
//                g2.setColor(getRandColor(100, 160));
//                g2.setFont(getRandomFont(h, type));
//
//                AffineTransform affine = new AffineTransform();
//                affine.setToRotation(Math.PI / 4 * rd * (rb ? 1 : -1), (w / verifySize) * i + (h - 4) / 2, h / 2);
//                g2.setTransform(affine);
//                g2.drawOval(rand.nextInt(w), rand.nextInt(h), 5 + rand.nextInt(10), 5 + rand.nextInt(10));
//                g2.drawChars(chars, i, 1, ((w - 10) / verifySize) * i + 5, h / 2 + (h - 4) / 2 - 10);
//            }
//
//            g2.dispose();
//            ImageIO.write(image, "jpg", os);
        } else if (type.contains("GIF") || type.contains("mixGIF")) {
            GifEncoder gifEncoder = new GifEncoder(); // gif编码类，这个利用了洋人写的编码类
            // 生成字符
            gifEncoder.start(os);
            gifEncoder.setQuality(180);
            gifEncoder.setDelay(150);
            gifEncoder.setRepeat(0);

            AlphaComposite ac3;
            for (int i = 0; i < verifySize; i++) {
                g2.setColor(getRandColor(100, 160));
                g2.setFont(getRandomFont(h, type));
                for (int j = 0; j < verifySize; j++) {
                    AffineTransform affine = new AffineTransform();
                    affine.setToRotation(Math.PI / 4 * rd * (rb ? 1 : -1), (w / verifySize) * i + (h - 4) / 2, h / 2);
                    g2.setTransform(affine);
                    g2.drawChars(chars, i, 1, ((w - 10) / verifySize) * i + 5, h / 2 + (h - 4) / 2 - 10);

                    ac3 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha(j, i, verifySize));
                    g2.setComposite(ac3);
                    g2.drawOval(rand.nextInt(w), rand.nextInt(h), 5 + rand.nextInt(10), 5 + rand.nextInt(10));
                    gifEncoder.addFrame(image);
                    image.flush();
                }
            }
            gifEncoder.finish();
            g2.dispose();
        } else {
            this.doDraw(image, os, rand, g2, verifySize, w, h, type, rd, rb, chars);
//            for (int i = 0; i < verifySize; i++) {
//                g2.setColor(getRandColor(100, 160));
//                g2.setFont(getRandomFont(h, type));
//
//                AffineTransform affine = new AffineTransform();
//                affine.setToRotation(Math.PI / 4 * rd * (rb ? 1 : -1), (w / verifySize) * i + (h - 4) / 2, h / 2);
//                g2.setTransform(affine);
//                g2.drawOval(rand.nextInt(w), rand.nextInt(h), 5 + rand.nextInt(10), 5 + rand.nextInt(10));
//                g2.drawChars(chars, i, 1, ((w - 10) / verifySize) * i + 5, h / 2 + (h - 4) / 2 - 10);
//            }
//
//            g2.dispose();
//            ImageIO.write(image, "jpg", os);
        }
    }

    /**
     * 获取随机颜色
     *
     * @param fc
     * @param bc
     * @return
     */
    private Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    private int[] getRandomRgb() {
        int[] rgb = new int[3];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }

    /**
     * 随机字体、随机风格、随机大小
     *
     * @param h 验证码图片高
     * @return
     */
    private Font getRandomFont(int h, String type) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // 字体
        String name = fontName[random.nextInt(fontName.length)];
        // 字体样式
        int style = fontStyle[random.nextInt(fontStyle.length)];
        // 字体大小
        int size = getRandomFontSize(h);

        if (type.equals("login")) {
            return new Font(name, style, size);
        } else if (type.equals("coupons")) {
            return new Font(name, style, size);
        } else if (type.contains("3D")) {
            return get3dFont(size, style);
        } else if (type.contains("mix")) {
            int flag = random.nextInt(10);
            if (flag > 4) {
                return new Font(name, style, size);
            } else {
                return get3dFont(size, style);
            }
        } else {
            return new Font(name, style, size);
        }
    }

    /**
     * 干扰线按范围获取随机数
     *
     * @return
     */
    private int getRandomDrawLine() {
        int min = 20;
        int max = 155;
        //Random random = new Random();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 噪点数率按范围获取随机数
     *
     * @return
     */
    private float getRandomDrawPoint() {
        float min = 0.05f;
        float max = 0.1f;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return min + ((max - min) * random.nextFloat());
    }

    /**
     * 获取字体大小按范围随机
     *
     * @param h 验证码图片高
     * @return
     */
    private int getRandomFontSize(int h) {
        int min = h - 8;
        // int max = 46;
        //Random random = new Random();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(11) + min;
    }

    /**
     * 3D中空字体自定义属性类
     *
     * @author cgtu
     * @date 2017年5月15日 下午3:27:52
     */
    //static class ImgFontByte {
    private Font get3dFont(int fontSize, int fontStype) {
        try {
            Font font = baseFont;
            return font.deriveFont(fontStype, fontSize);
        } catch (Exception e) {
            return new Font("Arial", fontStype, fontSize);
        }
    }

    private byte[] hex2byte(String str) {
        if (str == null)
            return null;
        //str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;

        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0x" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字符和干扰线扭曲
     *
     * @param g     绘制图形的java工具类
     * @param w1    验证码图片宽
     * @param h1    验证码图片高
     * @param color 颜色
     */
    private void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    /**
     * x轴扭曲
     *
     * @param g     绘制图形的java工具类
     * @param w1    验证码图片宽
     * @param h1    验证码图片高
     * @param color 颜色
     */
    private void shearX(Graphics g, int w1, int h1, Color color) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int period = random.nextInt(2);

        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }
    }

    /**
     * y轴扭曲
     *
     * @param g     绘制图形的java工具类
     * @param w1    验证码图片宽
     * @param h1    验证码图片高
     * @param color 颜色
     */
    private void shearY(Graphics g, int w1, int h1, Color color) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int period = random.nextInt(40) + 10; // 50;

        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }
        }
    }

    /**
     * 获取透明度,从0到1,自动计算步长
     *
     * @param i
     * @param j
     * @return float 透明度
     */
    private float getAlpha(int i, int j, int verifySize) {
        int num = i + j;
        float r = (float) 1 / verifySize, s = (verifySize + 1) * r;
        return num > verifySize ? (num * r - s) : num * r;
    }

    /**
     * 生成指定验证码图像文件 - 本地测试生成图片查看效果
     *
     * @param w          验证码图片宽
     * @param h          验证码图片高
     * @param outputFile 文件流
     * @param code       随机验证码
     * @throws IOException
     */
    public void outputImage(int w, int h, File outputFile, String code) throws IOException {
        if (outputFile == null) {
            return;
        }
        File dir = outputFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            outputFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outputFile);
            // outputImage(w, h, fos, code, "login"); //测试登录，噪点和干扰线为0.05f和20
            // outputImage(w, h, fos, code, "coupons"); //测试领券，噪点和干扰线为范围随机值0.05f ~ 0.1f和20 ~ 155
            // outputImage(w, h, fos, code, "3D"); //测试领券，噪点和干扰线为范围随机值0.05f ~ 0.1f和20 ~ 155
            // outputImage(w, h, fos, code, "GIF"); //测试领券，噪点和干扰线为范围随机值0.05f ~ 0.1f和20 ~ 155
            // outputImage(w, h, fos, code, "GIF3D"); //测试领券，噪点和干扰线为范围随机值0.05f ~ 0.1f和20 ~ 155
            // outputImage(w, h, fos, code, "mix2"); //测试领券，噪点和干扰线为范围随机值0.05f ~ 0.1f和20 ~ 155
            outputImage(w, h, fos, code, "mixGIF"); // 测试领券，噪点和干扰线为范围随机值0.05f ~ 0.1f和20 ~ 155
            fos.close();
        } catch (IOException e) {
            throw e;
        }
    }


}
