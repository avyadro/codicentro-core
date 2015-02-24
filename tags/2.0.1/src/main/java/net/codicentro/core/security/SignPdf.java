/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 28/04/2011 at 01:10:52 PM
 * @place: Toluca, Estado de México, México
 * @company: Codicentro©
 * @web: http://www.codicentro.net
 * @className: SignPdf.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package net.codicentro.core.security;

import net.codicentro.core.TypeCast;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignPdf implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(SignPdf.class);
    private String password;
    private boolean visible = true;
    private KeyStore ks = null;

    /**
     *
     * @param ksPassword, the password used to check the integrity of the
     * keystore, the password used to unlock the keystore
     * @param password, the password for recovering the key
     */
    public SignPdf(
            String ksurl,
            String ksPassword,
            String password,
            boolean visible) {
        try {
            this.visible = visible;
            this.password = password;
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(new URL(ksurl).openStream(), ksPassword.toCharArray());
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     *
     * @param pdf
     * @return
     */
    public OutputStream sign(InputStream pdf, Map<String, Object> property) {
        try {
            PdfReader reader = new PdfReader(pdf);
            OutputStream writer = new ByteArrayOutputStream();
            PdfStamper stp = PdfStamper.createSignature(reader, writer, '\0');
            PdfSignatureAppearance sap = stp.getSignatureAppearance();
            String alias = (String) ks.aliases().nextElement();
            PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);
            sap.setCrypto(key, chain, null, PdfSignatureAppearance.VERISIGN_SIGNED);

            if (property.containsKey("REASON")) {
                sap.setReason((String) property.get("REASON"));
            }
            if (property.containsKey("LOCATION")) {
                sap.setLocation((String) property.get("LOCATION"));
            }
            if (property.containsKey("CONTACT")) {
                sap.setContact((String) property.get("CONTACT"));
            }

            Calendar c = Calendar.getInstance();
            c.setLenient(false);
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_MONTH, -1);
            sap.setSignDate(c);
            if (visible) {
                // llx - lower left x lly - lower left y urx - upper right x ury - upper right y
                float llx = !property.containsKey("LEFT") ? 0 : TypeCast.toFloat(property.get("LEFT"));
                float lly = !property.containsKey("TOP") ? 0 : TypeCast.toFloat(property.get("TOP"));
                float urx = !property.containsKey("WIDTH") ? 0 : TypeCast.toFloat(property.get("WIDTH"));
                float ury = !property.containsKey("HEIGHT") ? 0 : TypeCast.toFloat(property.get("HEIGHT"));
                sap.setVisibleSignature(new Rectangle(llx, lly, urx, ury), 1, null);
            }
            stp.close();
            return writer;
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
        return null;
    }
}
