/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 20/04/2011 at 10:38:19 AM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: ImageUtil.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core;

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

public class ImageUtil implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
    private Image image = null;

    public enum Type {

        GIF,
        JPG,
        PNG,
        IMG,
        BMP,
        TIFF
    };

    /**
     *
     * @param image
     */
    public ImageUtil(byte[] image) {
        try {
            load(image);
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     *
     * @param image
     * @param width
     * @param height
     */
    public ImageUtil(byte[] image, int width, int height) {
        this(image);
        scale(width, height);
    }

    /**
     *
     * @param width
     * @param height
     */
    public final void scale(int width, int height) {
        scale(width, height, Image.SCALE_SMOOTH);
    }

    /**
     *
     * @param width
     * @param height
     * @param ascale, Image scaling algorithm
     */
    public final void scale(int width, int height, int ascale) {
        image = image.getScaledInstance(width, height, ascale);
    }

    /**
     *
     * @param format
     * @param output
     * @return
     * @throws IOException
     */
    public boolean write(Type format, File output) throws IOException {
        return ImageIO.write(toBufferedImage(), format.toString(), output);
    }

    /**
     *
     * @param format
     * @param output
     * @return
     * @throws IOException
     */
    public boolean write(Type format, ImageOutputStream output) throws IOException {
        return ImageIO.write(toBufferedImage(), format.toString(), output);
    }

    /**
     *
     * @param format
     * @param output
     * @return
     * @throws IOException
     */
    public boolean write(Type format, OutputStream output) throws IOException {
        return ImageIO.write(toBufferedImage(), format.toString(), output);
    }

    /**
     *
     * @param type
     * @return
     * @throws IOException
     */
    public String toBASE64Encoder(Type type) throws IOException {
        BASE64Encoder e64Encoder = new BASE64Encoder();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(type, out);
        out.close();
        return e64Encoder.encode(out.toByteArray());
    }

    /**
     *
     * @param data
     * @throws Exception
     */
    private void load(byte[] data) throws Exception {
        SeekableStream stream = new ByteArraySeekableStream(data);
        String[] names = ImageCodec.getDecoderNames(stream);
        ImageDecoder dec = ImageCodec.createImageDecoder(names[0], stream, null);
        RenderedImage im = dec.decodeAsRenderedImage();
        image = PlanarImage.wrapRenderedImage(im).getAsBufferedImage();
    }

    /**
     *
     */
    private BufferedImage toBufferedImage() {
        if (image instanceof BufferedImage) {
            // Return image unchanged if it is already a BufferedImage.
            return (BufferedImage) image;
        }
        // Ensure image is loaded.
        image = new ImageIcon(image).getImage();
        int type = hasAlpha() ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bufferedImage;
    }

    /**
     * Determines if an image has an alpha channel.
     *
     * @param image the
     * <code>Image</code>
     * @return true if the image has an alpha channel
     */
    private boolean hasAlpha() {
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException ex) {
        }
        return pg.getColorModel().hasAlpha();
    }
}
