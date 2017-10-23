package com.darkkeks;

import java.util.concurrent.LinkedBlockingQueue;

public class TaskGenerator {

    private final Board board;
    private final Template template;

    private final LinkedBlockingQueue<Pixel> task;

    public TaskGenerator(Board board, Template template) {
        this.board = board;
        this.template = template;
        this.task = new LinkedBlockingQueue<>();
    }

    public void generate() {
        if(!board.isLoaded() || !template.isLoaded()) return;

        task.clear();

        byte[][] brd = getData(board.getData(), board.getWidth(), board.getHeight());
        byte[][] tpl = getData(template.getData(), template.getWidth(), template.getHeight());

        int n = template.getWidth();
        int m = template.getHeight();
        int _x = template.getX();
        int _y = template.getY();
        boolean[][] used = new boolean[n][m];

        LinkedBlockingQueue<Pixel> q = new LinkedBlockingQueue<>();
        int[] dx = {1, 0, -1, 0};
        int[] dy = {0, 1, 0, -1};
        for(int i = _x; i < _x + n; ++i) {
            for(int j = _y; j < _y + m; ++j) {
                int x = i - _x, y = j - _y;
                if(brd[i][j] >= 0 && !used[x][y]) {
                    if(brd[i][j] != tpl[x][y])
                        if(tpl[x][y] != Color.TRANSPARENT.id)
                            task.offer(new Pixel(i, j, tpl[x][y]));
                        else
                            task.offer(new Pixel(i, j, Color.TRANSPARENT.id));

                    used[x][y] = true;
                    q.offer(new Pixel(i, j, tpl[x][y]));
                    while(!q.isEmpty()) {
                        Pixel cur = q.poll();

                        for(int d = 0; d < 4; ++d) {
                            int nx = cur.getX() + dx[d], ny = cur.getY() + dy[d];
                            if(nx >= _x && ny >= _y &&
                                    nx < _x + n && ny < _y + m &&
                                    !used[nx - _x][ny - _y]) {
                                used[nx - _x][ny - _y] = true;
                                q.offer(new Pixel(nx, ny, tpl[nx - _x][ny - _y]));

                                if(tpl[nx - _x][ny - _y] >= 0) {
                                    if(brd[nx][ny] != tpl[nx - _x][ny - _y])
                                        task.offer(new Pixel(nx, ny, tpl[nx - _x][ny - _y]));
                                } else {
                                    if(brd[nx][ny] != 1 && brd[nx][ny] != -1) {
                                        task.offer(new Pixel(nx, ny, 1));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private byte[][] getData(byte[] data, int width, int height) {
        byte[][] res = new byte[width][height];
        int current = 0;

        for(int j = 0; j < height; ++j) {
            for(int i = 0; i < width; ++i){
                res[i][j] = data[current++];
            }
        }
        return res;
    }

    public Pixel getNext() {
        return task.peek();
    }

    public void successfullyPlaced() {
        task.poll();
    }

    public boolean isEmpty() {
        return task.isEmpty();
    }
}
