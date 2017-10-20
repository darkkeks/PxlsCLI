package com.darkkeks;

import java.util.Date;

public class User extends MessageReceiver {

    private String token;
    private String username;
    private boolean banned;
    private String banReason;

    private long cooldownStart;
    private float cooldown;

    private boolean gotUserinfo;

    public User(String token) {
        this.gotUserinfo = false;
        this.cooldown = Float.NaN;
        this.token = token;
        this.connect(token);
    }

    public boolean canPlace() {
        return cooldown != Float.NaN && new Date().getTime() - cooldownStart > cooldown * 1000;
    }

    public boolean tryPlace(Pixel pixel) {
        if(canPlace()) {
            cooldown = Float.NaN;

            System.out.println(username + " placed pixel " + pixel.toString());
            sendPixel(pixel.getX(), pixel.getY(), (byte)pixel.getColor());
            return true;
        }
        return false;
    }

    public boolean gotUserinfo() {
        return gotUserinfo;
    }

    public boolean isConnected() {
        return connectionActive() && !banned;
    }

    public boolean isClosed() {
        return connectionClosed() || banned;
    }

    public String getName() {
        return username;
    }

    public String getToken() {
        return token;
    }

    @Override
    protected void handleUserinfo(String username, String role, boolean banned, long banExpiry, String ban_reason, String method) {
        System.out.println(username + " authorized.");
        this.gotUserinfo = true;
        this.username = username;
        this.banned = banned;
        this.banReason = ban_reason;
    }

    @Override
    protected void handleCooldown(float wait) {
        System.out.println(username + " cooldown: " + wait);
        if(!gotUserinfo) {
            close();
            throw new IllegalStateException("Couldn't login");
        }

        this.cooldown = wait;
        this.cooldownStart = new Date().getTime();
    }

    @Override
    protected void handlePixel(int x, int y, byte color) {
        if(!gotUserinfo) {
            close();
            throw new IllegalStateException("Couldn't login");
        }
    }
}
