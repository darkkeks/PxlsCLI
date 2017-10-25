package com.darkkeks.PxlsCLI.bot;

public class BotNet extends Thread {

    private final UserProvider userProvider;
    private final TaskGenerator task;

    public BotNet(TaskGenerator task, UserProvider userProvider) {
        this.task = task;
        this.userProvider = userProvider;
    }

    @Override
    public void run() {
        System.out.println("BotNet started.");

        int refreshTimer = 30;
        while(true) {
            try {
                userProvider.checkAuth();

                if(task.isEmpty() || refreshTimer <= 0) {
                    System.out.println("Refreshing task");
                    refreshTimer = 60; // Every minute
                    task.generate();
                } else {
                    boolean isPlaced = false;
                    int count = userProvider.getCount();
                    while(userProvider.hasNext() && count > 0) {
                        User user = userProvider.getNext();
                        if(user.canPlace()) {
                            if(user.tryPlace(task.getNext())) {
                                task.successfullyPlaced();
                                isPlaced = true;
                            }
                        }
                        refreshTimer--;
                        count--;
                    }
                    if(isPlaced)
                        System.out.println("Users in queue: " + userProvider.getCount());
                }

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
