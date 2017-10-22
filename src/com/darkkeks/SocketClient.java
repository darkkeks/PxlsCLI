package com.darkkeks;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class SocketClient extends WebSocketClient {

    private static final String WEBSOCKET_URL = "wss://pxls.space/ws";
    protected static final URI serverUri;
    static {
        URI uri = null;
        try {
            uri = new URI(WEBSOCKET_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        serverUri = uri;
    }

    private static Map<String, String> headers = new HashMap<>();
    private MessageReceiver receiver;

    public SocketClient(MessageReceiver receiver, String token) {
        super(serverUri, new Draft_6455(), headers, 0);
        this.receiver = receiver;

        if(token != null) {
            headers.put("Cookie", "pxls-token=" + token);
        }

        headers = new HashMap<>(); // new SocketClient will receive it's own headers
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {}

    @Override
    public void onMessage(String message) {
        receiver.receiveMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
    }
}