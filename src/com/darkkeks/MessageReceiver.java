package com.darkkeks;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.net.URISyntaxException;

public abstract class MessageReceiver {
    private SocketClient socketClient;

    protected void connect(String token) {
        try {
            socketClient = new SocketClient(this, token);
            socketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    protected boolean connectionActive() {
        return socketClient.isOpen();
    }

    protected boolean connectionClosed() {
        return socketClient.isClosed();
    }

    protected void close() {
        socketClient.close();
    }

    protected void sendPixel(int x, int y, byte color) {
        JsonObject msg = new JsonObject();
        msg.add("type", new JsonPrimitive("pixel"));
        msg.add("x", new JsonPrimitive(x));
        msg.add("y", new JsonPrimitive(y));
        msg.add("color", new JsonPrimitive(color));

        socketClient.send(msg.toString());
    }

    public void receiveMessage(String message) {
        try {
            JsonObject msg = PxlsCLI.gson.parse(message).getAsJsonObject();
            String type = msg.get("type").getAsString();

            if(type.equalsIgnoreCase("pixel")) {
                JsonObject pixel = msg.get("pixels").getAsJsonArray().get(0).getAsJsonObject();
                handlePixel(pixel.get("x").getAsInt(),
                        pixel.get("y").getAsInt(),
                        pixel.get("color").getAsByte());
            } else if(type.equalsIgnoreCase("cooldown")) {
                handleCooldown(msg.get("wait").getAsFloat());
            } else if(type.equalsIgnoreCase("users")) {
                handleUsers(msg.get("count").getAsInt());
            } else if(type.equalsIgnoreCase("userinfo")) {
                System.out.println(message);
                handleUserinfo(msg.get("username").getAsString(),
                        msg.get("role").getAsString(),
                        msg.get("banned").getAsBoolean(),
                        msg.get("banExpiry").getAsLong(),
                        msg.get("ban_reason").getAsString(),
                        msg.get("method").getAsString());
            } else if(type.equalsIgnoreCase("can_undo")) {
                handleCanUndo(msg.get("time").getAsLong());
            } else if(type.equalsIgnoreCase("alert")) {
                handleAlert(msg.get("message").getAsString());
            }
        } catch (Exception e) {
            socketClient.close();
        }
    }

    protected void handleAlert(String message) {}

    protected void handleCanUndo(long time) {}

    protected void handleUserinfo(String username, String role, boolean banned, long banExpiry, String ban_reason, String method) {}

    protected void handleUsers(int count) {}

    protected void handleCooldown(float wait) {}

    protected void handlePixel(int x, int y, byte color) {}
}
