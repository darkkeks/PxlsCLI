package com.darkkeks;

import java.net.URISyntaxException;
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
        this.connect(token);
    }

    public boolean tryPlace(int x, int y, byte color) {
        if(new Date().getTime() - cooldownStart > cooldown * 1000) {
            sendPixel(x, y, color);
            return true;
        }
        return false;
    }

    @Override
    protected void handleUserinfo(String username, String role, boolean banned, long banExpiry, String ban_reason, String method) {
        this.gotUserinfo = true;
        this.username = username;
        this.banned = banned;
        this.banReason = ban_reason;
    }

    @Override
    protected void handleCooldown(float wait) {
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
