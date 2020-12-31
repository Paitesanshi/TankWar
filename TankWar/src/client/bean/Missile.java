package client.bean;

import client.client.Tank;
import client.client.TankClient;
import client.event.TankHitEvent;
import client.background.CommonWall;
import client.background.MetalWall;
import client.single.SingleTank;
import client.single.SingleTankClient;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Missile {
    public static  int XSPEED = 10;
    public static  int YSPEED = 10;
    private static int ID = 10;

    private int id;
    private TankClient tc=null;
    private SingleTankClient stc=null;
    private int tankId;
    private int x, y;
    private Dir dir = Dir.R;
    private boolean live = true;
    private boolean good;

    private static Toolkit tk = Toolkit.getDefaultToolkit();
    private static Image[] imgs = null;
    private static Map<String, Image> map = new HashMap<>();
    static{
        imgs = new Image[]{
            tk.getImage(Missile.class.getClassLoader().getResource("client/images/missile/m.png")),
            tk.getImage(Missile.class.getClassLoader().getResource("client/images/missile/n.png"))
        };
        map.put("n", imgs[0]);
        map.put("m", imgs[1]);
    }
    public Missile(int tankId, int x, int y, boolean good, Dir dir) {
        this.tankId = tankId;
        this.x = x;
        this.y = y;
        this.good = good;
        this.dir = dir;
        this.id = ID++;
    }

    public Missile(int tankId, int x, int y, boolean good, Dir dir, TankClient tc) {
        this(tankId, x, y, good, dir);
        this.tc = tc;
    }
    public Missile(int tankId, int x, int y, boolean good, Dir dir, SingleTankClient tc) {
        this(tankId, x, y, good, dir);
        this.stc = tc;
    }
    public void draw(Graphics g) {
        if(!live) {
            if(tc!=null)tc.getMissiles().remove(this);
            else stc.getMissiles().remove(this);
            return;
        }
        g.drawImage(good ? map.get("n") : map.get("m"), x, y, null);
        move();
    }

    private void move() {//每画一次, 子弹的坐标移动一次
        switch(dir) {
            case L:
                x -= XSPEED;
                break;
            case LU:
                x -= XSPEED;
                y -= YSPEED;
                break;
            case U:
                y -= YSPEED;
                break;
            case RU:
                x += XSPEED;
                y -= YSPEED;
                break;
            case R:
                x += XSPEED;
                break;
            case RD:
                x += XSPEED;
                y += YSPEED;
                break;
            case D:
                y += YSPEED;
                break;
            case LD:
                x -= XSPEED;
                y += YSPEED;
                break;
            case STOP:
                break;
        }

        if(x < 0 || y < 0 || x > TankClient.GAME_WIDTH || y > TankClient.GAME_HEIGHT) {
            live = false;
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, imgs[0].getWidth(null), imgs[0].getHeight(null));
    }

    public boolean hitTank(Tank t) {//子弹击中坦克的方法
        if(this.live && t.isLive() && this.good != t.isGood() && this.getRect().intersects(t.getRect())) {
            this.live = false;//子弹死亡
            t.actionToTankHitEvent(new TankHitEvent(this));//告知观察的坦克被打中了
            return true;
        }
        return false;
    }
    public boolean hitTank(SingleTank t) {//子弹击中坦克的方法
        if(this.live && t.isLive() && this.good != t.isGood() && this.getRect().intersects(t.getRect())) {
            this.live = false;//子弹死亡
            t.actionToTankHitEvent(new TankHitEvent(this));//告知观察的坦克被打中了
            return true;
        }
        return false;
    }
    public boolean hitWall(CommonWall w,SingleTankClient tc) { // 子弹打到CommonWall上
        if (this.live && this.getRect().intersects(w.getRect())) {
            this.live = false;
            tc.getOtherWall().remove(w); // 子弹打到CommonWall墙上时则移除此击中墙
            tc.getHomeWall().remove(w);
            return true;
        }
        return false;
    }

    public boolean hitWall(MetalWall w) { // 子弹打到金属墙上
        if (this.live && this.getRect().intersects(w.getRect())) {
            this.live = false;
            //this.tc.metalWall.remove(w); //子弹不能摧毁金属墙
            return true;
        }
        return false;
    }

    public boolean hitHome(SingleTankClient tc) { // 当子弹打到家时
        if (this.live && this.getRect().intersects(tc.getHome().getRect())) {
            this.live = false;
            tc.getHome().setLive(false); // 当家接受一枪时就死亡
            return true;
        }
        return false;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTankId() {
        return tankId;
    }

    public void setTankId(int tankId) {
        this.tankId = tankId;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Dir getDir() {
        return dir;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public boolean isGood() {
        return good;
    }

    public void setGood(boolean good) {
        this.good = good;
    }
}
