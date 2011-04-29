/**
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

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

public class ImageUtil implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    public enum Type {

        GIF,
        JPEG,
        JPEG2000,
        PNG,
        IMG,
        BMP,
        TIFF,
        JBIG2S
    };
    /** Some PNG specific values. */
    private final int[] PNGID = {137, 80, 78, 71, 13, 10, 26, 10};
    private Type imageType = null;
    private RenderedImage renderedImage = null;

    /**
     * 
     * @param image 
     */
    public ImageUtil(byte[] image) {
        try {
            imageRender(image);
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * 
     * @param image
     * @param convertTo 
     */
    public ImageUtil(byte[] image, Type convertTo) {
        this(image);
        try {
            switch (convertTo) {
                case JPEG:
                    imageRender(toJpeg());
                    break;
                default:
                    break;
            }
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
    }

    /**
     * 
     * @param image
     * @param convertTo
     * @param width
     * @param height 
     */
    public ImageUtil(byte[] image, Type convertTo, int width, int height) {
        this(image, convertTo);
        try {
            imageRender(scale(width, height));
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * 
     * @param image 
     */
    private void imageRender(byte[] image) {
        try {
            findImageType(image);
            InputStream in = new ByteArrayInputStream(image);
            ImageDecoder dec = ImageCodec.createImageDecoder(imageType.toString(), in, null);
            renderedImage = dec.decodeAsRenderedImage();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * 
     * @throws CDCException
     */
    private void checkExistImage() throws CDCException {
        if (renderedImage == null) {
            throw new CDCException("Image is null.");
        }
    }

    /**
     * 
     * @param image
     * @throws IOException 
     */
    private void findImageType(byte[] image) throws IOException {
        InputStream is = null;
        imageType = null;
        try {
            is = new ByteArrayInputStream(image);
            int c1 = is.read();
            int c2 = is.read();
            int c3 = is.read();
            int c4 = is.read();
            if (c1 == 'G' && c2 == 'I' && c3 == 'F') {
                imageType = Type.GIF;
            } else if (c1 == 0xFF && c2 == 0xD8) {
                imageType = Type.JPEG;
            } else if (c1 == 0x00 && c2 == 0x00 && c3 == 0x00 && c4 == 0x0c) {
                imageType = Type.JPEG2000;
            } else if (c1 == 0xff && c2 == 0x4f && c3 == 0xff && c4 == 0x51) {
                imageType = Type.JPEG2000;
            } else if (c1 == PNGID[0] && c2 == PNGID[1] && c3 == PNGID[2] && c4 == PNGID[3]) {
                imageType = Type.PNG;
            } else if (c1 == 0xD7 && c2 == 0xCD) {
                imageType = Type.IMG;
            } else if (c1 == 'B' && c2 == 'M') {
                imageType = Type.BMP;
            } else if (c1 == 'M' && c2 == 'M' && c3 == 0 && c4 == 42 || c1 == 'I' && c2 == 'I' && c3 == 42 && c4 == 0) {
                imageType = Type.TIFF;
            } else if (c1 == 0x97 && c2 == 'J' && c3 == 'B' && c4 == '2') {
                int c5 = is.read();
                int c6 = is.read();
                int c7 = is.read();
                int c8 = is.read();
                if (c5 == '\r' && c6 == '\n' && c7 == 0x1a && c8 == '\n') {
                    imageType = Type.JBIG2S;
                }
            }
            if (imageType == null) {
                throw new IOException("the.byte.array.is.not.a.recognized.imageType");
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * 
     * @param width
     * @param height
     * @return
     * @throws CDCException 
     */
    public final byte[] scale(int width, int height) throws CDCException {
        checkExistImage();
        try {
            /*** ***/
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance((double) width / renderedImage.getWidth(), (double) height / renderedImage.getHeight());
            g.drawRenderedImage(renderedImage, at);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bi, imageType.toString(), out);
            out.close();
            if (out.size() > 0) {
                return out.toByteArray();
            } else {
                throw new CDCException("Not transform allowed...");
            }
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            throw new CDCException(ex.getLocalizedMessage());
        }
    }

    /**
     * 
     * @return
     * @throws IOException 
     */
    public String toBASE64Encoder() throws IOException {
        BASE64Encoder e64Encoder = new BASE64Encoder();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(renderedImage, imageType.toString(), out);
        out.close();
        return e64Encoder.encode(out.toByteArray());
    }

    /**
     * 
     * @return
     * @throws CDCException 
     */
    public final byte[] toJpeg() throws CDCException {
        checkExistImage();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(renderedImage, "jpeg", out);
            out.close();
            return out.toByteArray();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            throw new CDCException(ex.getLocalizedMessage());
        }
    }

    /**
     * 
     * @return
     * @throws CDCException 
     */
    public int getWidth() throws CDCException {
        checkExistImage();
        try {
            return renderedImage.getWidth();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            throw new CDCException(ex.getLocalizedMessage());
        }
    }

    /**
     * 
     * @return
     * @throws CDCException 
     */
    public int getHeight() throws CDCException {
        checkExistImage();
        try {
            return renderedImage.getHeight();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            throw new CDCException(ex.getLocalizedMessage());
        }
    }
}
