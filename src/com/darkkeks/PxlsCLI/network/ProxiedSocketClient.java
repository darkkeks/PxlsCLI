package com.darkkeks.PxlsCLI.network;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class ProxiedSocketClient extends SocketClient {

    public ProxiedSocketClient(MessageReceiver receiver, UserProxy userProxy, String token) {
        super(receiver, token);
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(userProxy.getHost(), userProxy.getPort()));

            Socket proxySocket = new Socket(proxy);
            String host = serverUri.getHost();
            int port = serverUri.getPort();

            proxySocket.connect(new InetSocketAddress(host, port > 0 ? port : 443));

            SSLContext sslContext = SSLContext.getInstance( "TLS" );
            sslContext.init( null, null, null );

            SSLSocketFactory factory = sslContext.getSocketFactory();
            this.setSocket( factory.createSocket(proxySocket, host, port, true) );
            this.connectBlocking();
        } catch (IOException | InterruptedException | NoSuchAlgorithmException | KeyManagementException e) {
            System.out.println("Failed to connect through proxy " + userProxy);
            e.printStackTrace();
        }
    }
}
