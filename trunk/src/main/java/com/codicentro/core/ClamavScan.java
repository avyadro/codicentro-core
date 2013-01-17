/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 22/08/2011 at 10:12:41 AM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: ClamavScan.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClamavScan {

    private Logger logger = LoggerFactory.getLogger(ClamavScan.class);
    private String host;
    private int port = 3310;
    private byte[] toScan;
    private Socket protocol = null;
    private Socket data = null;
    private String virus = "";
    private int connectionTimeout = 90;

    public ClamavScan(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    /**
     *
     * @param port default 3310
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     *
     * @param is
     * @return
     */
    public boolean doScan(InputStream is) throws CDCException {
        try {
            toScan = is.toString().getBytes();
            protocol = new Socket();
            SocketAddress sockaddr = new InetSocketAddress(host, port);
            protocol.setSoTimeout(connectionTimeout * 1000);
            String responseValue = "";

            //First, try to connect to the clamd
            protocol.connect(sockaddr);
            byte[] b = {'S', 'T', 'R', 'E', 'A', 'M', '\n'};
            protocol.getOutputStream().write(b); // Write the initialisation command
            // Now, read byte per byte until we find a LF.
            byte[] rec = new byte[1];
            while (true) {
                protocol.getInputStream().read(rec);
                if (rec[0] == '\n') {
                    break;
                }
                responseValue += new String(rec);
            }
            logger.debug("response: " + responseValue);
            // In the response value, there's an integer. It's the TCP port that the clamd has allocated for us for data stream.
            int dataPort = -1;
            if (responseValue.contains(" ")) {
                dataPort = Integer.parseInt(responseValue.split(" ")[1]);
            }

            // Now, we connect to the data port obtained before.
            data = new Socket();
            SocketAddress sockaddrData = new InetSocketAddress(host, dataPort);
            data.setSoTimeout(connectionTimeout * 1000); // we leave 1m30 before closing connection is clamd does not issue a response.
            data.connect(sockaddrData);
            data.getOutputStream().write(toScan); // We write to the data stream the content of the file
            data.close(); // Then close the stream, so that clamd knows it's the end of the stream.
            responseValue = "";
            while (true) {
                try {
                    protocol.getInputStream().read(rec);
                } catch (IOException e3) {
                    break;
                }
                if (rec[0] == '\n') {
                    break;
                }
                responseValue += new String(rec);
            }
            logger.debug("response: " + responseValue);
            if (data != null) {
                data.close();
            }
            if (protocol != null) {
                protocol.close();
            }
            if (responseValue == null) {
                logger.error("response is null. Passing the file anyway...");
                return true;
            }
            if (responseValue.contains("ERROR")) {
                logger.error("response is erroneous (" + responseValue
                        + "). Passing the file anyway...");
            }
            if (responseValue.equals("stream: OK")) { // clamd writes this if the stream we sent does not contains viruses.            
                return true;
            }
            virus = responseValue; // Else there is an error, the response contains the name of the identified virus
            return false;
        } catch (SocketException e) {
            throw new CDCException("Codicentro-ClamavScan: " + e.getMessage() + " " + host + ":" + port, e);
        } catch (IOException e) {
            throw new CDCException("Codicentro-ClamavScan: " + e.getMessage(), e);
        }
    }

    public String getVirus() {
        return virus;
    }
}
