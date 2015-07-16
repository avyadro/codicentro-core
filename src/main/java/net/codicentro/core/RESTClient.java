/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Jul 16, 2015 at 9:31:06 AM
 * @place: Ciudad de México, México
 * @company: Grupo Financiero Actinver S.A. de C.V.
 * @web: http://www.actinver.com
 * @className: RESTClient.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package net.codicentro.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTClient {

    private final static Logger logger = LoggerFactory.getLogger(RESTClient.class);
    private final static String USER_AGENT = "Java";
    private final static String ENCODING = "ISO-8859-1";

    private static HttpURLConnection connection;

    private static String encodeParams(Map<String, Object> params) throws MalformedURLException, IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }
        String parameters = "";
        for (String key : params.keySet()) {
            parameters += (TypeCast.isBlank(parameters) ? "" : "&") + URLEncoder.encode(key, ENCODING) + "=" + URLEncoder.encode(TypeCast.toString(params.get(key)), ENCODING);
        }
        return parameters;
    }

    private static StringBuilder send() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response;
    }

    public static String get(String url) throws ProtocolException, IOException {
        return get(url, null);
    }

    public static String get(String url, Map<String, Object> params) throws ProtocolException, IOException {
        String parameters = encodeParams(params);
        if (parameters != null) {
            url += url.contains("?") ? "&" + parameters : "?" + parameters;
        }

        connection = (HttpURLConnection) new URL(url).openConnection();
        //add request header
        connection.setRequestProperty("User-Agent", USER_AGENT);
        // optional default is GET
        connection.setRequestMethod("GET");
        logger.info("\nSending 'GET' request to URL : " + url);
        logger.info("Response Code : " + connection.getResponseCode());
        return send().toString();
    }

    public static String post(String url) throws IOException {
        return post(url, null);
    }

    public static String post(String url, Map<String, Object> params) throws IOException {
        String parameters = encodeParams(null);
        connection = (HttpURLConnection) new URL(url).openConnection();
        //add reuqest header
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (parameters != null) {
            // Send post request
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close();
        }
        logger.info("\nSending 'POST' request to URL : " + url);
        logger.info("Response Code : " + connection.getResponseCode());
        return send().toString();
    }

    public static String put(String url) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static String delete(String url) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static String options(String url) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String head(String url) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static String trace(String url) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static String connect(String url) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
