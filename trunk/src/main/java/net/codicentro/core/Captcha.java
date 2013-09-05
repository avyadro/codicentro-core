/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 8/03/2011 at 08:50:17 AM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: Captcha.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core;

import com.sun.image.codec.jpeg.JPEGCodec;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import sun.misc.BASE64Encoder;

public class Captcha implements Serializable {

    private int width = 50;
    private int height = 25;
    private Object value = null;
    private Color background = null;
    private Font font = null;
    private Color fcolor = null;

    public Captcha() {
        /**
         * * DEFAULTS VALUE **
         */
        background = new Color(255, 255, 255);
        fcolor = new Color(0, 100, 0);
        font = new Font("SansSerif", 1, 17);
    }

    public Captcha(Object value) {
        this();
        this.value = value;
    }

    public Captcha(Object value, int width, int height) {
        this();
        this.value = value;
    }

    public String draw() throws IOException, CDCException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        g.setColor(background);
        g.fillRect(0, 0, width, height);
        g.setColor(fcolor);
        g.setFont(font);
        g.drawString(TypeCast.toString(value), 5, font.getSize());
        g.setColor(background);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JPEGCodec.createJPEGEncoder(out).encode(image);
        out.close();
        return new BASE64Encoder().encode(out.toByteArray());
    }
}
