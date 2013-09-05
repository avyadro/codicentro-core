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
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClamavScan {

    private Logger logger = LoggerFactory.getLogger(ClamavScan.class);
    private String host;
    private int port = 3310;    
    private String virus;
    private int timeout = 90;

    public ClamavScan(String host) {
        this.host = host;
    }

    public ClamavScan(String host, int port) {
        this.host = host;
        this.port = port;
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
            virus = "";
            byte[] toScan = is.toString().getBytes();
            Socket protocol = new Socket();
            SocketAddress sockaddr = new InetSocketAddress(host, port);
            protocol.setSoTimeout(timeout * 1000);
            protocol.connect(sockaddr);// First, try to connect to the clamd
            byte[] b = {'S', 'T', 'R', 'E', 'A', 'M', '\n'};
            protocol.getOutputStream().write(b); // Write the initialisation command            
            byte[] rec = new byte[1];
            while (true) {// Now, read byte per byte until we find a LF.
                protocol.getInputStream().read(rec);
                if (rec[0] == '\n') {
                    break;
                }
                virus += new String(rec);
            }
            logger.debug("Response: " + virus);// In the response value, there's an integer. It's the TCP port that the clamd has allocated for us for data stream.
            int dataPort = -1;
            if (virus.contains(" ")) {
                dataPort = Integer.parseInt(virus.split(" ")[1]);
            }
            // Now, we connect to the data port obtained before.
            Socket data = new Socket();
            SocketAddress sockaddrData = new InetSocketAddress(host, dataPort);
            data.setSoTimeout(timeout * 1000); // we leave 1m30 before closing connection is clamd does not issue a response.
            data.connect(sockaddrData);
            data.getOutputStream().write(toScan); // We write to the data stream the content of the file
            data.close(); // Then close the stream, so that clamd knows it's the end of the stream.
            virus = "";
            while (true) {
                try {
                    protocol.getInputStream().read(rec);
                } catch (IOException e3) {
                    break;
                }
                if (rec[0] == '\n') {
                    break;
                }
                virus += new String(rec);
            }
            logger.debug("Response: " + virus);
            if (data != null) {
                data.close();
            }
            if (protocol != null) {
                protocol.close();
            }
            if (TypeCast.isBlank(virus)) {
                logger.error("Response is blank or null. Passing the file anyway...");
                return true;
            }
            if (virus.contains("ERROR")) {
                logger.error("Response is erroneous (" + virus + "). Passing the file anyway...");
            }
            if (virus.equals("stream: OK")) { // clamd writes this if the stream we sent does not contains viruses.            
                return true;
            }
            // Else there is an error, the response contains the name of the identified virus
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
