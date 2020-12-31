package client.background;

import client.single.SingleTankClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;

public class Home {
    private int x, y;
    private SingleTankClient tc;
    public static final int width = 30, length = 30; // 全局静态变量长宽
    private boolean live = true;

    private static Toolkit tk = Toolkit.getDefaultToolkit(); // 全局静态变量
    private static Image[] homeImags = null;
    static {
        System.out.println(new File("src/client/images/scene/home.jpg"));
        homeImags = new Image[] { tk.getImage("src/client/images/scene/home.jpg"), };
    }

    public Home(int x, int y, SingleTankClient tc) {// 构造函数，传递Home的参数并赋值
        this.x = x;
        this.y = y;
        this.tc = tc; // 获得控制
    }

    public void gameOver(Graphics g) {
        tc.gameOver();
    }

    public void draw(Graphics g) {

        if (live) { // 如果活着，则画出home
            g.drawImage(homeImags[0], x, y, null);

            for (int i = 0; i < tc.getHomeWall().size(); i++) {
                CommonWall w = tc.getHomeWall().get(i);
                w.draw(g);
            }
        } else {
            gameOver(g); // 调用游戏结束

        }
    }

    public boolean isLive() { // 判读是否还活着
        return live;
    }

    public void setLive(boolean live) { // 设置生命
        this.live = live;
    }

    public Rectangle getRect() { // 返回长方形实例
        return new Rectangle(x, y, width, length);
    }

}

