package cn.joyconn.utils.QRCodeUtil;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class QRCodeUtils {
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int [] COLOR_PIXELS = new int []{0xFFFFB6C1,0xFFFFC0CB,0xFFDC143C,0xFFFFF0F5,0xFFDB7093,0xFFFF69B4,0xFFFF1493,0xFFC71585,0xFFDA70D6,0xFFD8BFD8,0xFFDDA0DD,0xFFEE82EE,0xFFFF00FF,0xFFFF00FF,0xFF8B008B,0xFF800080,0xFFBA55D3,0xFF9400D3,0xFF9932CC,0xFF4B0082,0xFF8A2BE2,0xFF9370DB,0xFF7B68EE,0xFF6A5ACD,0xFF483D8B,0xFFE6E6FA,0xFFF8F8FF,0xFF0000FF,0xFF0000CD,0xFF191970,0xFF00008B,0xFF000080,0xFF4169E1,0xFF6495ED,0xFFB0C4DE,0xFF778899,0xFF708090,0xFF1E90FF,0xFFF0F8FF,0xFF4682B4,0xFF87CEFA,0xFF87CEEB,0xFF00BFFF,0xFFADD8E6,0xFFB0E0E6,0xFF5F9EA0,0xFFF0FFFF,0xFFE1FFFF,0xFFAFEEEE,0xFF00FFFF,0xFF00FFFF,0xFF00CED1,0xFF2F4F4F,0xFF008B8B,0xFF008080,0xFF48D1CC,0xFF20B2AA,0xFF40E0D0,0xFF7FFFAA,0xFF00FA9A,0xFFF5FFFA,0xFF00FF7F,0xFF3CB371,0xFF2E8B57,0xFFF0FFF0,0xFF90EE90,0xFF98FB98,0xFF8FBC8F,0xFF32CD32,0xFF00FF00,0xFF228B22,0xFF008000,0xFF006400,0xFF7FFF00,0xFF7CFC00,0xFFADFF2F,0xFF556B2F,0xFF6B8E23,0xFFFAFAD2,0xFFFFFFF0,0xFFFFFFE0,0xFFFFFF00,0xFF808000,0xFFBDB76B,0xFFFFFACD,0xFFEEE8AA,0xFFF0E68C,0xFFFFD700,0xFFFFF8DC,0xFFDAA520,0xFFFFFAF0,0xFFFDF5E6,0xFFF5DEB3,0xFFFFE4B5,0xFFFFA500,0xFFFFEFD5,0xFFFFEBCD,0xFFFFDEAD,0xFFFAEBD7,0xFFD2B48C,0xFFDEB887,0xFFFFE4C4,0xFFFF8C00,0xFFFAF0E6,0xFFCD853F,0xFFFFDAB9,0xFFF4A460,0xFFD2691E,0xFF8B4513,0xFFFFF5EE,0xFFA0522D,0xFFFFA07A,0xFFFF7F50,0xFFFF4500,0xFFE9967A,0xFFFF6347,0xFFFFE4E1,0xFFFA8072,0xFFFFFAFA,0xFFF08080,0xFFBC8F8F,0xFFCD5C5C,0xFFFF0000,0xFFA52A2A,0xFFB22222,0xFF8B0000,0xFF800000,0xFFFFFFFF,0xFFF5F5F5,0xFFDCDCDC,0xFFD3D3D3,0xFFC0C0C0,0xFFA9A9A9,0xFF808080,0xFF696969};


    private static BufferedImage toBufferedImage(BitMatrix matrix ,int RGB ) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if(RGB==0) {
                    image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
                }else{
                    image.setRGB(x, y, matrix.get(x, y) ? RGB : WHITE);
                }
            }
        }
        return image;
    }
    private static BufferedImage toBufferedColorImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Random random = new Random(System.currentTimeMillis());
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if( matrix.get(x, y) ){
                    int num = random.nextInt(COLOR_PIXELS.length-1);
                    image.setRGB(x, y, COLOR_PIXELS[num]);
                }else{
                    image.setRGB(x, y, WHITE);
                }
            }
        }
        return image;
    }
    /**
     *   此方法使用时需验证 背景图需要多大的需要详细读读此方法的中的着色算法
     * @param matrix
     * @param width
     * @param height
     * @param scaleImagePath 背景图参照色 （）
     * @return
     */
    private static BufferedImage toBufferedScaleImage(BitMatrix matrix, int width, int height,String scaleImagePath) {
        BufferedImage scaleImage = null;
        if(scaleImagePath!=null&&!"".equals(scaleImagePath)) {
            try {
                File  scaleImageFile= new File(scaleImagePath);
                if (Objects.nonNull(scaleImageFile) && scaleImageFile.exists()) {
                    scaleImage = ImageIO.read(scaleImageFile);
                }
            }catch (Exception ex){

            }
        }
        // 二维矩阵转为一维像素数组
        int halfW = matrix.getWidth() / 2;
        int halfH = matrix.getHeight() / 2;
        int IMAGE_HALF_WIDTH = width/2;
        int FRAME_WIDTH=2;
        int[] pixels = new int[width * height];
        int[][] srcPixels = new int[width][height];
        for (int i = 0; i < scaleImage.getWidth(); i++) {
            for (int j = 0; j < scaleImage.getHeight(); j++) {
                srcPixels[i][j] = scaleImage.getRGB(i, j);//图片的像素点
            }
        }


        for (int y = 0; y < matrix.getHeight(); y++) {
            for (int x = 0; x < matrix.getWidth(); x++) {
                // 左上角颜色,根据自己需要调整颜色范围和颜色
                /*if (x > 0 && x < 170 && y > 0 && y < 170) {
                    Color color = new Color(231, 144, 56);
                    int colorInt = color.getRGB();
                    pixels[y * width + x] = matrix.get(x, y) ? colorInt
                            : 16777215;
                } else*/
                // 读取图片
                if (x > halfW - IMAGE_HALF_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH
                        && y < halfH + IMAGE_HALF_WIDTH) {
                    pixels[y * width + x] = srcPixels[x - halfW
                            + IMAGE_HALF_WIDTH][y - halfH + IMAGE_HALF_WIDTH];
                }
                // 在图片四周形成边框
                else if ((x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW - IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        + IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW + IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        + IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        - IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH + IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        + IMAGE_HALF_WIDTH + FRAME_WIDTH)) {
                    pixels[y * width + x] = 0xfffffff;
                } else {
                    // 二维码颜色（RGB）
                    int num1 = (int) (50 - (50.0 - 13.0) / matrix.getHeight()
                            * (y + 1));
                    int num2 = (int) (165 - (165.0 - 72.0) / matrix.getHeight()
                            * (y + 1));
                    int num3 = (int) (162 - (162.0 - 107.0)
                            / matrix.getHeight() * (y + 1));
                    Color color = new Color(num1, num2, num3);
                    int colorInt = color.getRGB();
                    // 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                    pixels[y * width + x] = matrix.get(x, y) ? colorInt : 16777215;// 0x000000:0xffffff
                }
            }
        }
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        image.getRaster().setDataElements(0, 0, width, height, pixels);
        return image;
    }


    /**
     * 二维码绘制logo
     * @param QRcode 二维码图片流
     * @param logoImg logo图片文件
     * */
    private static BufferedImage encodeImgLogo(BufferedImage QRcode,File logoImg){

        try{
            //获取画笔
            Graphics2D g = QRcode.createGraphics();
            //读取logo图片
            BufferedImage logo = ImageIO.read(logoImg);
            //设置二维码大小，太大，会覆盖二维码，此处20%
            int logoWidth = logo.getWidth(null) > QRcode.getWidth()*2 /10 ? (QRcode.getWidth()*2 /10) : logo.getWidth(null);
            int logoHeight = logo.getHeight(null) > QRcode.getHeight()*2 /10 ? (QRcode.getHeight()*2 /10) : logo.getHeight(null);
            //设置logo图片放置位置
            //中心
            int x = (QRcode.getWidth() - logoWidth) / 2;
            int y = (QRcode.getHeight() - logoHeight) / 2;
            //右下角，15为调整值
//          int x = QRcode.getWidth()  - logoWidth-15;
//          int y = QRcode.getHeight() - logoHeight-15;
            //开始合并绘制图片
            g.setColor(Color.WHITE);
//            Shape shape= new RoundRectangle2D.Float(x, y, logoWidth, logoHeight,logoWidth/4,logoHeight/4);
//            g.draw(shape);
            g.fillRoundRect(x- logoWidth/10, y-logoHeight/10, logoWidth+logoWidth/10, logoHeight+logoHeight/10,logoWidth/4,logoHeight/4);

            g.drawImage(logo, x, y, logoWidth, logoHeight, null);
            //g.drawRoundRect(x, y, logoWidth, logoHeight, 15 ,15);

            //logo边框大小
            g.setStroke(new BasicStroke(2));
            //logo边框颜色
            g.setColor(Color.WHITE);
            g.drawRect(x, y, logoWidth, logoHeight);
            g.dispose();
            logo.flush();
            QRcode.flush();
        }catch(Exception e){
            System.out.println("二维码绘制logo失败");
        }
        return QRcode;
    }
    /**
     * 二维码绘制说明文字
     * @param QRcodeImage 二维码图片流
     * @param note 说明文字
     * */
    private static BufferedImage encodeImgNotes(BufferedImage QRcodeImage, int width, int height,String note,int fontSize,Color fontColor){
        return encodeImgNotes(QRcodeImage,width,height,note,fontSize,fontColor,true);
    }
    /**
     * 二维码绘制说明文字
     * @param QRcodeImage 二维码图片流
     * @param note 说明文字
     * @param noteRight 文字靠右
     * */
    private static BufferedImage encodeImgNotes(BufferedImage QRcodeImage, int width, int height,String note,int fontSize,Color fontColor,boolean noteRight){

        try{
            // 新的图片，把带logo的二维码下面加上文字
            String []notes = note.split("\n");
            int outHeight = Double.valueOf(height*0.1).intValue();
            if(outHeight>0){
                BufferedImage outImage = new BufferedImage(width, height+outHeight*notes.length, BufferedImage.TYPE_INT_RGB);
                Graphics2D outg = outImage.createGraphics();
                outg.setColor(Color.WHITE);
                outg.fillRect( 0, 0,outImage.getWidth(), outImage.getHeight());
                // 画二维码到新的面板
                outg.drawImage(QRcodeImage, 0, 0, QRcodeImage.getWidth(), QRcodeImage.getHeight(), null);
                // 画文字到新的面板
                outg.setColor(fontColor);
                outg.setFont(new Font("宋体", Font.BOLD, fontSize)); // 字体、字型、字号
                int strWidth = 0;
                int index = 0;
                for(String s : notes){
                    strWidth = outg.getFontMetrics().stringWidth(s);
                    if(noteRight){
                        outg.drawString(s, width-strWidth-30, height + outHeight*index+5); // 画文字
                    }else{
                        outg.drawString(s, 30, height + outHeight*index+5); // 画文字
                    }
                    index++;
                }

                outg.dispose();
                outImage.flush();
                QRcodeImage = outImage;
            }
        }catch(Exception e){
            System.out.println("二维码绘制logo失败");
        }
        return QRcodeImage;
    }

    /**
     * 生成普通二维码
     *
     * @param text  二维码对应的信息
     * @param width 二维码宽度
     * @param height 二维码高度
     * @param errorLvl 容错等级
     * @param margin 白边大小，取值范围0~4
     */
    public  BitMatrix createCode(String text, int width, int height, ErrorCorrectionLevel errorLvl, int margin) {
        HashMap hints = new HashMap();
        // 内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, margin); // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, errorLvl);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            return bitMatrix;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成普通二维码
     *
     * @param text  二维码对应的信息
     * @param width 二维码宽度
     * @param height 二维码高度
     * @param RGB   java 中的RGB颜色代码，如：0xFF000000;
     * @param errorLvl 容错等级
     * @param margin 白边大小，取值范围0~4
     */
    public  BufferedImage createCode(String text, int width, int height, ErrorCorrectionLevel errorLvl, int margin,int RGB) {
        HashMap hints = new HashMap();
        // 内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, margin); // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, errorLvl);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text,
                    BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage img = toBufferedImage(bitMatrix,RGB);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成彩色二维码
     *
     * @param text  二维码对应的信息
     * @param width 二维码宽度
     * @param height 二维码高度
     * @param errorLvl 容错等级
     * @param margin 白边大小，取值范围0~4
     */
    public  BufferedImage createColorCode(String text, int width, int height, ErrorCorrectionLevel errorLvl, int margin) {
        HashMap hints = new HashMap();
        // 内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, margin); // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, errorLvl);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text,
                    BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage img = toBufferedColorImage(bitMatrix);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成背景图二维码
     *
     * @param text  二维码对应的信息
     * @param width 二维码宽度
     * @param height 二维码高度
     * @param errorLvl 容错等级
     * @param margin 白边大小，取值范围0~4
     */
    public  BufferedImage createScaleCode(String text, int width, int height, ErrorCorrectionLevel errorLvl, int margin,String scalePath) {
        HashMap hints = new HashMap();
        // 内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, margin); // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, errorLvl);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text,
                    BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage img = toBufferedScaleImage(bitMatrix,  width,  height,scalePath);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成带logo的二维码图片
     *
     * @param text  二维码对应的信息
     * @param width 二维码宽度
     * @param height 二维码高度
     * @param errorLvl 容错等级
     * @param RGB   java 中的RGB颜色代码，如：0xFF000000;
     * @param margin 白边大小，取值范围0~4
     * @param logoPath  logo路径
     * @param note  在二维码图片中追加文本说明信息
     * @param fontSize 字体大小
     */
    public  BufferedImage drawLogoQRCode(String text, int width, int height,int RGB, ErrorCorrectionLevel errorLvl, int margin,String logoPath,String note,boolean noteRight,int fontSize,Color fontColor ) {
        try {

            BufferedImage image = createCode(text,width,height,  errorLvl,  margin,RGB);
            if(logoPath!=null&&!"".equals(logoPath)) {
                try {
                    File logoFile = new File(logoPath);
                    if (Objects.nonNull(logoFile) && logoFile.exists()) {
                        image = encodeImgLogo(image, logoFile);
                    }
                }catch (Exception ex){

                }
            }

            // 自定义文本描述
            if (StringUtils.isNotEmpty(note)) {
                image = encodeImgNotes(image,  width,  height, note, fontSize,fontColor,noteRight);
            }

            image.flush();
            return image;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }


    /**
     * 生成带logo的彩色二维码图片
     *
     * @param text  二维码对应的信息
     * @param width 二维码宽度
     * @param height 二维码高度
     * @param errorLvl 容错等级
     * @param margin 白边大小，取值范围0~4
     * @param logoPath  logo路径
     * @param note  在二维码图片中追加文本说明信息
     * @param fontSize 字体大小
     */
    public  BufferedImage drawLogoColorQRCode(String text, int width, int height, ErrorCorrectionLevel errorLvl, int margin,String logoPath,String note,boolean noteRight,int fontSize,Color fontColor) {
        try {

            BufferedImage image = createColorCode(text,width,height,  errorLvl,  margin);
            if(logoPath!=null&&!"".equals(logoPath)) {
                try {
                    File logoFile = new File(logoPath);
                    if (Objects.nonNull(logoFile) && logoFile.exists()) {
                        image = encodeImgLogo(image, logoFile);
                    }
                }catch (Exception ex){

                }
            }
            // 自定义文本描述
            if (StringUtils.isNotEmpty(note)) {
                image = encodeImgNotes(image,  width,  height, note, fontSize,fontColor,noteRight);
            }
            image.flush();
            return image;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * 生成带logo的彩色二维码图片
     *
     * @param text  二维码对应的信息
     * @param width 二维码宽度
     * @param height 二维码高度
     * @param errorLvl 容错等级
     * @param margin 白边大小，取值范围0~4
     * @param logoPath  logo路径
     * @param note  在二维码图片中追加文本说明信息
     * @param fontSize 字体大小
     */
    public  BufferedImage drawLogoScaleQRCode(String text, int width, int height, ErrorCorrectionLevel errorLvl, int margin,String scalePath,String logoPath,String note,boolean noteRight,int fontSize,Color fontColor) {
        try {

            BufferedImage image = createScaleCode(text,width,height,  errorLvl,  margin,scalePath);
            if(logoPath!=null&&!"".equals(logoPath)) {
                try {
                    File logoFile = new File(logoPath);
                    if (Objects.nonNull(logoFile) && logoFile.exists()) {
                        image = encodeImgLogo(image, logoFile);
                    }
                }catch (Exception ex){

                }
            }
            // 自定义文本描述
            if (StringUtils.isNotEmpty(note)) {
                image = encodeImgNotes(image,  width,  height, note, fontSize,fontColor,noteRight);
            }
            image.flush();
            return image;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
