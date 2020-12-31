package client.background;

import client.single.SingleTankClient;

import java.awt.*;

public class CommonWall {
    public static final int width = 20; //设置墙的固定参数
    public static final int length = 20;
    int x, y;

    SingleTankClient tc;
    private static Toolkit tk = Toolkit.getDefaultToolkit();
    private static Image[] wallImags = null;
    static {
        wallImags = new Image[] { // 储存commonWall的图片
                tk.getImage("src/client/images/scene/commonWall.png"), };
    }

    public CommonWall(int x, int y, SingleTankClient tc) { // 构造函数
        this.x = x;
        this.y = y;
        this.tc = tc; // 获得界面控制
    }

    public void draw(Graphics g) {// 画commonWall
        g.drawImage(wallImags[0], x, y, null);
    }

    public Rectangle getRect() {  //构造指定参数的长方形实例
        return new Rectangle(x, y, width, length);
    }
}

