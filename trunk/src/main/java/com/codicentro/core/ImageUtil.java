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

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.ImgWMF;
import com.lowagie.text.Jpeg;
import com.lowagie.text.Jpeg2000;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.BmpImage;
import com.lowagie.text.pdf.codec.GifImage;
import com.lowagie.text.pdf.codec.JBIG2Image;
import com.lowagie.text.pdf.codec.PngImage;
import com.lowagie.text.pdf.codec.TiffImage;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

public class ImageUtil implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
    private byte[] image = null;
    private String imageFormat = null;

    /**
     * 
     * @param bytes
     */
    public ImageUtil(byte[] image) {
        try {
            this.image = image;
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     *
     * @return
     * @throws CDCException
     */
    public void toJpeg() throws CDCException {
        checkExistImage();
        try {
            Jpeg jpg = (Jpeg) getInstance(image);
            image = jpg.getOriginalData();
            imageFormat = "jpeg";
        } catch (ClassCastException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            throw new CDCException("Can not convert image to jpeg.");
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            throw new CDCException(ex.getLocalizedMessage());
        }
    }

    /**
     * 
     * @throws CDCException
     */
    private void checkExistImage() throws CDCException {
        if (image == null) {
            throw new CDCException("Image is null.");
        }
    }

    /**
     * 
     * @param width
     * @param height
     * @throws CDCException
     */
    public void scale(int width, int height) throws CDCException {
        checkExistImage();
        try {
            /*** ***/
            BufferedImage oldImage = ImageIO.read(new ByteArrayInputStream(image));
            /*** ***/
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = newImage.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance((double) width / oldImage.getWidth(), (double) height / oldImage.getHeight());
            g.drawRenderedImage(oldImage, at);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(newImage, imageFormat, out);
            out.close();
            image = out.toByteArray();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            throw new CDCException(ex.getLocalizedMessage());
        }
    }

    /**
     * 
     * @return
     */
    public String toBASE64Encoder() {
        BASE64Encoder e64Encoder = new BASE64Encoder();
        return e64Encoder.encode(image);
    }

    /**
     * 
     * @param imgb
     * @return
     * @throws BadElementException
     * @throws MalformedURLException
     * @throws IOException
     */
    private Image getInstance(byte imgb[]) throws BadElementException, MalformedURLException, IOException {
        InputStream is = null;
        try {
            is = new java.io.ByteArrayInputStream(imgb);
            int c1 = is.read();
            int c2 = is.read();
            int c3 = is.read();
            int c4 = is.read();
            is.close();

            is = null;
            if (c1 == 'G' && c2 == 'I' && c3 == 'F') {
                imageFormat = "gif";
                GifImage gif = new GifImage(imgb);
                return gif.getImage(1);
            }
            if (c1 == 0xFF && c2 == 0xD8) {
                imageFormat = "jpeg";
                return new Jpeg(imgb);
            }
            if (c1 == 0x00 && c2 == 0x00 && c3 == 0x00 && c4 == 0x0c) {
                imageFormat = "jpeg2000";
                return new Jpeg2000(imgb);
            }
            if (c1 == 0xff && c2 == 0x4f && c3 == 0xff && c4 == 0x51) {
                imageFormat = "jpeg2000";
                return new Jpeg2000(imgb);
            }
            if (c1 == PngImage.PNGID[0] && c2 == PngImage.PNGID[1] && c3 == PngImage.PNGID[2] && c4 == PngImage.PNGID[3]) {
                imageFormat = "png";
                return PngImage.getImage(imgb);
            }
            if (c1 == 0xD7 && c2 == 0xCD) {
                imageFormat = "img";
                return new ImgWMF(imgb);
            }
            if (c1 == 'B' && c2 == 'M') {
                imageFormat = "bmp";
                return BmpImage.getImage(imgb);
            }
            if (c1 == 'M' && c2 == 'M' && c3 == 0 && c4 == 42 || c1 == 'I' && c2 == 'I' && c3 == 42 && c4 == 0) {
                imageFormat = "tiff";
                RandomAccessFileOrArray ra = null;
                try {
                    ra = new RandomAccessFileOrArray(imgb);
                    Image img = TiffImage.getTiffImage(ra, 1);
                    if (img.getOriginalData() == null) {
                        img.setOriginalData(imgb);
                    }
                    return img;
                } finally {
                    if (ra != null) {
                        ra.close();
                    }
                }

            }
            if (c1 == 0x97 && c2 == 'J' && c3 == 'B' && c4 == '2') {
                is = new java.io.ByteArrayInputStream(imgb);
                is.skip(4);
                int c5 = is.read();
                int c6 = is.read();
                int c7 = is.read();
                int c8 = is.read();
                if (c5 == '\r' && c6 == '\n' && c7 == 0x1a && c8 == '\n') {
                    int file_header_flags = is.read();
                    int number_of_pages = -1;
                    if ((file_header_flags & 0x2) == 0x2) {
                        number_of_pages = is.read() << 24 | is.read() << 16 | is.read() << 8 | is.read();
                    }
                    is.close();
                    // a jbig2 file with a file header.  the header is the only way we know here.
                    // embedded jbig2s don't have a header, have to create them by explicit use of Jbig2Image?
                    // nkerr, 2008-12-05  see also the getInstance(URL)
                    RandomAccessFileOrArray ra = null;
                    try {
                        ra = new RandomAccessFileOrArray(imgb);
                        imageFormat = "jbig2s";
                        Image img = JBIG2Image.getJbig2Image(ra, 1);
                        if (img.getOriginalData() == null) {
                            img.setOriginalData(imgb);
                        }
                        return img;
                    } finally {
                        if (ra != null) {
                            ra.close();
                        }
                    }
                }
            }
            throw new IOException("the.byte.array.is.not.a.recognized.imageformat");
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
