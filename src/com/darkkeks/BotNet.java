package com.darkkeks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class BotNet extends Thread {

    private Map<Integer, User> users;
    private LinkedBlockingQueue<User> placeQueue, loginQueue;
    private TaskGenerator task;

    public BotNet(TaskGenerator task) {
        users = new HashMap<>();
        loginQueue = new LinkedBlockingQueue<>();
        placeQueue = new LinkedBlockingQueue<>();
        this.task = task;
    }

    @Override
    public void run() {
        System.out.println("BotNet started.");

        int refreshTimer = 30;
        while(true) {
            try {
                if(!loginQueue.isEmpty()) {
                    User user = loginQueue.poll();
                    if(user.isConnected() && user.gotUserinfo()) {
                        System.out.println("Added " + user.getName() + " to place queue.");
                        placeQueue.offer(user);
                    } else if(!user.isClosed()){
                        loginQueue.offer(user);
                    } else {
                        System.out.println("User disconnected " + user.getToken());
                    }
                }

                if(task.isEmpty() || refreshTimer <= 0) {
                    System.out.println("Refreshing task");
                    refreshTimer = 30;
                    task.generate();
                } else {
                    int userCount = placeQueue.size();
                    while(!placeQueue.isEmpty() && refreshTimer > 0 && userCount > 0) {
                        User user = placeQueue.poll();
                        if(user.canPlace()) {
                            if(user.tryPlace(task.getNext())) {
                                task.successfulyPlaced();
                                refreshTimer--;
                            }
                        }
                        if(!user.isClosed()) {
                            placeQueue.offer(user);
                        }
                        userCount--;
                    }

                    while(!placeQueue.isEmpty() &&
                            placeQueue.peek().tryPlace(task.getNext())) {
                        task.successfulyPlaced();
                        refreshTimer--;

                        User user = placeQueue.poll();
                        if(!user.isClosed())
                            placeQueue.offer(user);
                    }
                }

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public BotNet addUser(User user) {
        int id = Integer.parseInt(user.getToken().split("\\|")[0]);
        if(!users.containsKey(id)) {
            users.put(id, user);
            loginQueue.offer(user);
        } else {
            System.out.println("Tried to insert duplicate user");
        }
        return this;
    }
}
