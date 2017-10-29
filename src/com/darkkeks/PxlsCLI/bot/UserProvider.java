package com.darkkeks.PxlsCLI.bot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class UserProvider {

    private final Map<Integer, User> users;
    private final LinkedBlockingQueue<User> placeQueue;
    private final LinkedBlockingQueue<User> loginQueue;

    public UserProvider() {
        users = new HashMap<>();
        loginQueue = new LinkedBlockingQueue<>();
        placeQueue = new LinkedBlockingQueue<>();
    }

    public void add(User user) {
        int id = user.getId();
        if(!users.containsKey(id)) {
            users.put(id, user);
            loginQueue.offer(user);
        } else {
            System.out.println("Tried to insert duplicate user");
        }
    }

    public void checkAuth() {
        int count = loginQueue.size();
        while(!loginQueue.isEmpty() && count > 0) {
            User user = loginQueue.poll();
            if(user.isConnected() && user.gotUserInfo()) {
                System.out.println("Added " + user.getName() + " to place queue.");
                placeQueue.offer(user);
            } else if(!user.isClosed()){
                loginQueue.offer(user);
            } else {
                System.out.println("User disconnected " + user.getToken());
            }
            count--;
        }
    }

    public User getNext() {
        if(!hasNext()) return null;
        User user = placeQueue.poll();
        if(!user.isClosed()) {
            placeQueue.offer(user);
        } else {
            // TODO Try to reconnect user
        }
        return user;
    }

    public int getCount() {
        return placeQueue.size();
    }

    public boolean hasNext() {
        return !placeQueue.isEmpty();
    }

    public float getMinimalCooldown() {
        float cooldown = 1e9f;
        for(User user : placeQueue) {
            cooldown = Math.min(cooldown, user.getCooldown());
        }
        return cooldown;
    }
}
