package com.darkkeks.PxlsCLI.bot;

import com.darkkeks.PxlsCLI.network.UserProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class UserProvider {

    private final Map<Integer, User> users;
    private final LinkedBlockingQueue<User> placeQueue;
    private final LinkedBlockingQueue<User> loginQueue;
    private final LinkedBlockingQueue<String> cantConnect;
    private final ProxyProvider proxyProvider;
    private final boolean useProxy;

    public UserProvider() {
        this(null);
    }

    public UserProvider(ProxyProvider proxyProvider) {
        users = new HashMap<>();
        loginQueue = new LinkedBlockingQueue<>();
        placeQueue = new LinkedBlockingQueue<>();
        cantConnect = new LinkedBlockingQueue<>();
        this.proxyProvider = proxyProvider;
        this.useProxy = proxyProvider != null;
    }

    public void add(String token) {
        int id = getUserId(token);
        if(!users.containsKey(id)) {
            if(useProxy) {
                new Thread(() -> {
                    boolean added = false;
                    int proxyCount = proxyProvider.getCount();
                    while(proxyProvider.hasNext() && proxyCount > 0) {
                        if(added = tryAdd(token, proxyProvider.get())) {
                            break;
                        }
                    }
                    if(!added) {
                        System.out.println("Couldn't connect user using proxy " + token);
                        cantConnect.offer(token);
                    }
                }).start();
            } else {
                User user = new User(token);
                users.put(id, user);
                loginQueue.offer(user);
            }
        } else {
            System.out.println("Tried to insert duplicate user");
        }
    }

    private boolean tryAdd(String token, UserProxy userProxy) {
        try {
            User user = new User(token, userProxy);
            loginQueue.offer(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private int getUserId(String token) {
        return Integer.parseInt(token.split("\\|")[0]);
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
