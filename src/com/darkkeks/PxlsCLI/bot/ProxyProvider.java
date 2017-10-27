package com.darkkeks.PxlsCLI.bot;

import com.darkkeks.PxlsCLI.network.UserProxy;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ProxyProvider {

    private final String filename;
    private final LinkedBlockingQueue<UserProxy> proxyQueue;

    public ProxyProvider(String filename) {
        this.proxyQueue = new LinkedBlockingQueue<>();
        this.filename = filename;
    }

    public void init() {
        if(filename != null) {
            File file = new File(filename);
            if (file.isFile()) {
                try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                  String line;
                  while((line = reader.readLine()) != null) {
                      if(isProxy(line)) {
                          String parts[] = line.split(":");
                          proxyQueue.offer(new UserProxy(parts[0], Integer.parseInt(parts[1])));
                      }
                  }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public UserProxy get() {
        return proxyQueue.poll();
    }

    public void add(UserProxy proxy) {
        proxyQueue.offer(proxy);
    }

    private boolean isProxy(String line) {
        return line.matches("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)");
    }

    public boolean hasNext() {
        return proxyQueue.size() > 0;
    }

    public int getCount() {
        return proxyQueue.size();
    }
}
