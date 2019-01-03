package top.mortise.utils.picHelper;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * Created by Eric.Zhang on 2017/5/11.
 */
public class ImageUtil {
    /**
     * 旋转图像。
     * @param bufferedImage 图像。
     * @param degree 旋转角度。
     * @return 旋转后的图像。
     */
    public static BufferedImage rotateImage(
            final BufferedImage bufferedImage, final int degree) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int type = bufferedImage.getColorModel().getTransparency();

        BufferedImage image = new BufferedImage(width, height, type);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        graphics2D.rotate(Math.toRadians(degree), width / 2, height / 2);
        graphics2D.drawImage(bufferedImage, 0, 0, null);

        try {
            return image;
        } finally {
            if (graphics2D != null) {
                graphics2D.dispose();
            }
        }
    }

    /**
     * 将图像按照指定的比例缩放。
     * 比如需要将图像放大20%，那么调用时scale参数的值就为20；如果是缩小，则scale值为-20。
     * @param bufferedImage 图像。
     * @param scale 缩放比例。
     * @return 缩放后的图像。
     */
    public static BufferedImage resizeImageScale(
            final BufferedImage bufferedImage, final int scale) {
        if (scale == 0) {
            return bufferedImage;
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int newWidth = 0;
        int newHeight = 0;

        double nowScale = (double) Math.abs(scale) / 100;
        if (scale > 0) {
            newWidth = (int) (width * (1 + nowScale));
            newHeight = (int) (height * (1 + nowScale));
        } else if (scale < 0) {
            newWidth = (int) (width * (1 - nowScale));
            newHeight = (int) (height * (1 - nowScale));
        }

        return resizeImage(bufferedImage, newWidth, newHeight);
    }

    /**
     * 将图像缩放到指定的宽高大小。
     * @param bufferedImage 图像。
     * @param width 新的宽度。
     * @param height 新的高度。
     * @return 缩放后的图像。
     */
    public static BufferedImage resizeImage(
            final BufferedImage bufferedImage,
            final int width, final int height) {
        int type = bufferedImage.getColorModel().getTransparency();
        BufferedImage image = new BufferedImage(width, height, type);

        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        graphics2D.drawImage(bufferedImage, 0, 0, width, height, 0, 0,
                bufferedImage.getWidth(), bufferedImage.getHeight(), null);

        try {
            return image;
        } finally {
            if (graphics2D != null) {
                graphics2D.dispose();
            }
        }
    }

    /**
     * 将图像等比例缩放到指定的宽。
     * @param bufferedImage 图像。
     * @param width 新的宽度。
     * @return 缩放后的图像。
     */
    public static BufferedImage resizeImageByWidth(
            final BufferedImage bufferedImage,
            final int width) {
        int newWidth = bufferedImage.getWidth();
        int height = bufferedImage.getHeight() * width / newWidth;
        return  resizeImage(bufferedImage,width,height);
    }
    /**
     * 将图像等比例缩放到指定的高。
     * @param bufferedImage 图像。
     * @param height 新的高度。
     * @return 缩放后的图像。
     */
    public static BufferedImage resizeImageByHeight(
            final BufferedImage bufferedImage,
            final int height) {
        int newHeight = bufferedImage.getHeight();
        int width = bufferedImage.getWidth()* height / newHeight;
        return  resizeImage(bufferedImage,width,height);
    }
    /**
     * 将图像水平翻转。
     * @param bufferedImage 图像。
     * @return 翻转后的图像。
     */
    public static BufferedImage flipImage(
            final BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int type = bufferedImage.getColorModel().getTransparency();

        BufferedImage image = new BufferedImage(width, height, type);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.drawImage(bufferedImage, 0, 0, width, height,
                width, 0, 0, height, null);

        try {
            return image;
        } finally {
            if (graphics2D != null) {
                graphics2D.dispose();
            }
        }
    }

    /**
     * 获取图片的类型。如果是 gif、jpg、png、bmp 以外的类型则返回null。
     * @param imageBytes 图片字节数组。
     * @return 图片类型。
     * @throws IOException IO异常。
     */
    public static String getImageType(final byte[] imageBytes)
            throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(imageBytes);
        ImageInputStream imageInput = ImageIO.createImageInputStream(input);
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInput);
        String type = null;
        if (iterator.hasNext()) {
            ImageReader reader = iterator.next();
            type = reader.getFormatName().toUpperCase();
        }

        try {
            return type;
        } finally {
            if (imageInput != null) {
                imageInput.close();
            }
        }
    }

    /**
     * 验证图片大小是否超出指定的尺寸。未超出指定大小返回true，超出指定大小则返回false。
     * @param imageBytes 图片字节数组。
     * @param width 图片宽度。
     * @param height 图片高度。
     * @return 验证结果。未超出指定大小返回true，超出指定大小则返回false。
     * @throws IOException IO异常。
     */
    public static boolean checkImageSize(
            final byte[] imageBytes, final int width, final int height)
            throws IOException {
        BufferedImage image = byteToImage(imageBytes);
        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();
        if (sourceWidth > width || sourceHeight > height) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 将图像字节数组转化为BufferedImage图像实例。
     * @param imageBytes 图像字节数组。
     * @return BufferedImage图像实例。
     * @throws IOException IO异常。
     */
    public static BufferedImage byteToImage(
            final byte[] imageBytes) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(input);

        try {
            return image;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    /**
     * 将BufferedImage持有的图像转化为指定图像格式的字节数组。
     * @param bufferedImage 图像。
     * @param formatName 图像格式名称。
     * @return 指定图像格式的字节数组。
     * @throws IOException IO异常。
     */
    public static byte[] imageToByte(
            final BufferedImage bufferedImage, final String formatName)
            throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, formatName, output);

        try {
            return output.toByteArray();
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /**
     * 将图像以指定的格式进行输出，调用者需自行关闭输出流。
     * @param bufferedImage 图像。
     * @param output 输出流。
     * @param formatName 图片格式名称。
     * @throws IOException IO异常。
     */
    public static void writeImage(final BufferedImage bufferedImage,
                                  final OutputStream output, final String formatName)
            throws IOException {
        ImageIO.write(bufferedImage, formatName, output);
    }

    /**
     * 将图像以指定的格式进行输出，调用者需自行关闭输出流。
     * @param imageBytes 图像的byte数组。
     * @param output 输出流。
     * @param formatName 图片格式名称。
     * @throws IOException IO异常。
     */
    public static void writeImage(final byte[] imageBytes,
                                  final OutputStream output, final String formatName)
            throws IOException {
        BufferedImage image = byteToImage(imageBytes);
        ImageIO.write(image, formatName, output);
    }
}
