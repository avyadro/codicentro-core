/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 20/04/2011 at 10:38:19 AM
 * @place: Toluca, Estado de México, México
 * @company: Codicentro©
 * @web: http://www.codicentro.net
 * @className: ImageUtil.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package net.codicentro.core;

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDirectory;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
    public final static int TAG_COMPRESSION = 259;
    public final static int TAG_JPEG_INTERCHANGE_FORMAT = 513;
    public final static int COMP_JPEG_OLD = 6;
    public final static int COMP_JPEG_TTN2 = 7;
    private Image image;

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
    public ImageUtil(byte[] image) throws Exception {
        load(image);
    }

    /**
     *
     * @param image
     * @param width
     * @param height
     */
    public ImageUtil(byte[] image, int width, int height) throws Exception {
        this(image);
        scale(width, height);
    }

    public ImageUtil(int width, int height, byte[] image) throws IOException {
        InputStream in = new ByteArrayInputStream(image);
        this.image = ImageIO.read(in);
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
        Base64 e64Encoder = new Base64();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(type, out);
        out.close();
        return e64Encoder.encodeAsString(out.toByteArray());
    }

    /**
     *
     * @param data
     * @throws Exception
     */
    private void load(byte[] data) throws Exception {
        SeekableStream stream = new ByteArraySeekableStream(data);
        String decoder2use = ImageCodec.getDecoderNames(stream)[0];
        if (!TypeCast.isBlank(decoder2use) && decoder2use.equals("tiff")) {
            TIFFDirectory tdir = new TIFFDirectory(stream, 0);
            int compression = tdir.getField(TAG_COMPRESSION).getAsInt(0);
            if (compression == COMP_JPEG_OLD) {
//            // Special handling for old/unsupported JPEG-in-TIFF format:
//            // {@link: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4929147 }
                stream.seek(tdir.getField(TAG_JPEG_INTERCHANGE_FORMAT).getAsLong(0));
                decoder2use = "jpeg";
            }
        }
        ImageDecoder dec = ImageCodec.createImageDecoder(decoder2use, stream, null);
        RenderedImage img = dec.decodeAsRenderedImage();
        image = PlanarImage.wrapRenderedImage(img).getAsBufferedImage();
    }

    public static BufferedImage toBufferedImage(byte[] image) throws IOException {
        SeekableStream stream = new ByteArraySeekableStream(image);
        String decoder2use = ImageCodec.getDecoderNames(stream)[0];
        ImageDecoder dec = ImageCodec.createImageDecoder(decoder2use, stream, null);
        RenderedImage img = dec.decodeAsRenderedImage();
        return PlanarImage.wrapRenderedImage(img).getAsBufferedImage();
    }

    public Image getImage() {
        return image;
    }

    /**
     *
     * @param angle - an angle, in degrees
     */
    public void rotate(Double angle) {
        int height = image.getHeight(null);
        BufferedImage bi = toBufferedImage();
        Graphics2D g2 = bi.createGraphics();
        g2.rotate(Math.toRadians(angle), height / 2, height / 2);
        g2.drawImage(image, 0, 0, Color.WHITE, null);
        g2.dispose();
        image = bi;
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
     * @param image the <code>Image</code>
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
