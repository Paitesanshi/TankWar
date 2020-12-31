package client.single;

import client.background.CommonWall;
import client.background.Home;
import client.background.MetalWall;
import client.bean.Dir;
import client.bean.Explode;
import client.bean.Missile;
import client.client.Tank;
import client.client.TankClient;
import client.event.TankHitEvent;
import client.event.TankHitListener;
import client.strategy.FireAction;
import client.strategy.NormalFireAction;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SingleTank implements TankHitListener {
    private int id;

    public static int XSPEED = 5;
    public static int YSPEED = 5;

    protected String name;
    protected boolean good;
    private static Random r = new Random();
    private int step = r.nextInt(10)+5 ; // 产生一个随机数,随机模拟坦克的移动路径
    protected int x, y;
    private int oldx,oldy;
    protected boolean live = true;
    protected SingleTankClient tc;
    protected boolean bL, bU, bR, bD;
    protected Dir dir = Dir.STOP;
    protected Dir ptDir = Dir.D;
    protected int blood = 100;
    protected BloodBar bb = new BloodBar();
    protected FireAction fireAction = new NormalFireAction();//可以开火

    private static Toolkit tk = Toolkit.getDefaultToolkit();
    private static Image[] imgs = null;
    private static Map<String, Image> map = new HashMap<>();
    static{
        imgs = new Image[]{//加载两方阵营的图片
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tD.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tL.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tLD.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tLU.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tR.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tRD.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tRU.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tU.png")),

                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eD.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eL.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eLD.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eLU.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eR.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eRD.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eRU.png")),
                tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eU.png")),
        };
        map.put("tD", imgs[0]);
        map.put("tL", imgs[1]);
        map.put("tLD", imgs[2]);
        map.put("tLU", imgs[3]);
        map.put("tR", imgs[4]);
        map.put("tRD", imgs[5]);
        map.put("tRU", imgs[6]);
        map.put("tU", imgs[7]);
        map.put("eD", imgs[8]);
        map.put("eL", imgs[9]);
        map.put("eLD", imgs[10]);
        map.put("eLU", imgs[11]);
        map.put("eR", imgs[12]);
        map.put("eRD", imgs[13]);
        map.put("eRU", imgs[14]);
        map.put("eU", imgs[15]);
    }

    public static final int WIDTH =  imgs[0].getWidth(null);
    public static final int HEIGHT = imgs[0].getHeight(null);

    public SingleTank(int x, int y, boolean good, String name) {
        this.x = x;
        this.y = y;
        this.oldx=x;
        this.oldy=y;
        this.good = good;
        this.name = name;
    }

    public SingleTank(String name, int x, int y, boolean good, Dir dir, SingleTankClient tc) {
        this(x, y, good, name);
        this.dir = dir;
        this.tc = tc;
    }

    /**
     * 根据坦克阵营画出图片
     * @param g
     */
    public void draw(Graphics g) {
        if(!live) {
            if(!good) {
                tc.getTanks().remove(this);
            }
            return;
        }
        switch(ptDir) {
            case L:
                g.drawImage(good ? map.get("tL") : map.get("eL"), x, y, null);
                break;
            case LU:
                g.drawImage(good ? map.get("tLU") : map.get("eLU"), x, y, null);
                break;
            case U:
                g.drawImage(good ? map.get("tU") : map.get("eU"), x, y, null);
                break;
            case RU:
                g.drawImage(good ? map.get("tRU") : map.get("eRU"), x, y, null);
                break;
            case R:
                g.drawImage(good ? map.get("tR") : map.get("eR"), x, y, null);
                break;
            case RD:
                g.drawImage(good ? map.get("tRD") : map.get("eRD"), x, y, null);
                break;
            case D:
                g.drawImage(good ? map.get("tD") : map.get("eD"), x, y, null);
                break;
            case LD:
                g.drawImage(good ? map.get("tLD") : map.get("eLD"), x, y, null);
                break;
        }
        g.drawString(name, x, y - 20);
        bb.draw(g);//画出血条
        move();
    }

    /**
     * 根据坦克的方向进行移动
     */
    private void move() {
        oldx=x;oldy=y;
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

        if(dir != Dir.STOP) {
            ptDir = dir;
        }

        if(x < 0) x = 0;
        if(y < 30) y = 30;
        if(x + WIDTH > TankClient.GAME_WIDTH) x = TankClient.GAME_WIDTH - WIDTH;
        if(y + HEIGHT > TankClient.GAME_HEIGHT) y = TankClient.GAME_HEIGHT - HEIGHT;
        if (!good) {
            Dir[] directons = Dir.values();
            if (step == 0) {
                step = r.nextInt(12) + 3;  //产生随机路径
                int rn = r.nextInt(directons.length);
                dir= directons[rn];      //产生随机方向
            }
            step--;

            if (r.nextInt(40) > 38)//产生随机数，控制敌人开火
                this.fire();
        }
    }

    /**
     * 监听键盘按下, 上下左右移动分别对应WSAD
     * @param e
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_A:
                bL = true;
                break;
            case KeyEvent.VK_W:
                bU = true;
                break;
            case KeyEvent.VK_D:
                bR = true;
                break;
            case KeyEvent.VK_S:
                bD = true;
                break;
        }
        locateDirection();
    }

    /**
     * 根据4个方向的布尔值判断坦克的方向
     */
    private void locateDirection() {
        Dir oldDir = this.dir;
        if(bL && !bU && !bR && !bD) dir = Dir.L;
        else if(bL && bU && !bR && !bD) dir = Dir.LU;
        else if(!bL && bU && !bR && !bD) dir = Dir.U;
        else if(!bL && bU && bR && !bD) dir = Dir.RU;
        else if(!bL && !bU && bR && !bD) dir = Dir.R;
        else if(!bL && !bU && bR && bD) dir = Dir.RD;
        else if(!bL && !bU && !bR && bD) dir = Dir.D;
        else if(bL && !bU && !bR && bD) dir = Dir.LD;
        else if(!bL && !bU && !bR && !bD) dir = Dir.STOP;
    }

    /**
     * 监听键盘释放
     * @param e
     */
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_J://监听到J键按下则开火
                fire();
                break;
            case KeyEvent.VK_A:
                bL = false;
                break;
            case KeyEvent.VK_W:
                bU = false;
                break;
            case KeyEvent.VK_D:
                bR = false;
                break;
            case KeyEvent.VK_S:
                bD = false;
                break;
        }
        locateDirection();
    }
    public void changToOldDir(){
        x=oldx;
        y=oldy;
    }
    public void fire() {//发出一颗炮弹的方法

        if(!this.isLive()) return;
        int x = this.getX() + 10;//确定子弹的坐标, 这里应该用子弹的常量计算, 待修正
        int y = this.getY() + 10;
        Missile m = new Missile(this.getId(), x, y, this.isGood(), this.getPtDir(),  tc);//产生一颗子弹
        tc.getMissiles().add(m);
    }
    public boolean collideHome(Home h) {   //撞到家的时候
        if (this.live && this.getRect().intersects(h.getRect())) {
            this.changToOldDir();
            return true;
        }
        return false;
    }

    public boolean collideWithTanks(java.util.List<SingleTank> tanks) {//撞到坦克时
        for (int i = 0; i < tanks.size(); i++) {
            SingleTank t = tanks.get(i);
            if (this != t) {
                if (this.live && t.isLive()
                        && this.getRect().intersects(t.getRect())) {
                    this.changToOldDir();
                    t.changToOldDir();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean collideWithWall(CommonWall w) {  //碰撞到普通墙时
        if (this.live && this.getRect().intersects(w.getRect())) {
            this.changToOldDir();    //转换到原来的方向上去
            return true;
        }
        return false;
    }

    public boolean collideWithWall(MetalWall w) {  //撞到金属墙
        if (this.live && this.getRect().intersects(w.getRect())) {
            this.changToOldDir();
            return true;
        }
        return false;
    }
        @Override
    public void actionToTankHitEvent(TankHitEvent tankHitEvent) {
        this.tc.getExplodes().add(new Explode(tankHitEvent.getSource().getX() - 20,
                tankHitEvent.getSource().getY() - 20, this.tc));//坦克自身产生一个爆炸
        if(this.blood == 20){//坦克每次扣20滴血, 如果只剩下20滴了, 那么就标记为死亡.
            this.live = false;
            if(good)this.tc.gameOver();
            return;
        }
        this.blood -= 20;//血量减少20.
    }

    /**
     * 血条
     */
     class BloodBar {
        public void draw(Graphics g) {
            Color c = g.getColor();
            g.setColor(Color.BLACK);
            g.drawRect(x, y - 15, 30, 8);
            int w = (30 * blood) / 100 ;
            g.setColor(Color.RED);
            g.fillRect(x, y - 15, w, 8);
            g.setColor(c);
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, imgs[0].getWidth(null), imgs[0].getHeight(null));
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public boolean isGood() {
        return good;
    }

    public void setGood(boolean good) {
        this.good = good;
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

    public Dir getPtDir() {
        return ptDir;
    }

    public void setPtDir(Dir ptDir) {
        this.ptDir = ptDir;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SingleTankClient getTc() {
        return tc;
    }

    public void setTc(SingleTankClient tc) {
        this.tc = tc;
    }
}






//package client.single;
//
//import client.bean.Dir;
//import client.bean.Explode;
//import client.bean.Missile;
//import client.client.TankClient;
//import client.event.TankHitEvent;
//import client.event.TankHitListener;
//import client.protocol.TankDeadMsg;
//import client.protocol.TankMoveMsg;
//import client.protocol.TankReduceBloodMsg;
//import client.strategy.Fire;
//import client.strategy.FireAction;
//import client.strategy.NormalFireAction;
//
//import java.awt.*;
//import java.awt.event.*;
//import java.util.HashMap;
//import java.util.Map;
//
//public class SingleTank extends client.client.Tank implements TankHitListener, Fire {
//    private int id;
//
//    public static  int XSPEED = 5;
//    public static  int YSPEED = 5;
//
//    private String name;
//    private boolean good;
//    private int x, y;
//    private boolean live = true;
//    private SingleTankClient tc;
//    private boolean bL, bU, bR, bD;
//    private Dir dir = Dir.STOP;
//    private Dir ptDir = Dir.D;
//    private int blood = 100;
//    private BloodBar bb = new BloodBar();
//    private FireAction fireAction = new NormalFireAction();//可以开火
//
//    private static Toolkit tk = Toolkit.getDefaultToolkit();
//    private static Image[] imgs = null;
//    private static Map<String, Image> map = new HashMap<>();
//    static{
//        imgs = new Image[]{//加载两方阵营的图片
//                tk.getImage(client.client.Tank.class.getClassLoader().getResource("client/images/tank/tD.png")),
//                tk.getImage(client.client.Tank.class.getClassLoader().getResource("client/images/tank/tL.png")),
//                tk.getImage(client.client.Tank.class.getClassLoader().getResource("client/images/tank/tLD.png")),
//                tk.getImage(client.client.Tank.class.getClassLoader().getResource("client/images/tank/tLU.png")),
//                tk.getImage(client.client.Tank.class.getClassLoader().getResource("client/images/tank/tR.png")),
//                tk.getImage(client.client.Tank.class.getClassLoader().getResource("client/images/tank/tRD.png")),
//                tk.getImage(client.client.Tank.class.getClassLoader().getResource("client/images/tank/tRU.png")),
//                tk.getImage(client.client.Tank.class.getClassLoader().getResource("client/images/tank/tU.png")),
//
//        };
//        map.put("tD", imgs[0]);
//        map.put("tL", imgs[1]);
//        map.put("tLD", imgs[2]);
//        map.put("tLU", imgs[3]);
//        map.put("tR", imgs[4]);
//        map.put("tRD", imgs[5]);
//        map.put("tRU", imgs[6]);
//        map.put("tU", imgs[7]);
//    }
//
//    public static final int WIDTH =  imgs[0].getWidth(null);
//    public static final int HEIGHT = imgs[0].getHeight(null);
//
//    public SingleTank(int x, int y, boolean good, String name) {
//        super(x,y,good,name);
//    }
//    public SingleTank(String name, int x, int y, boolean good, Dir dir, SingleTankClient tc) {
//        this(x, y, good, name);
//        this.dir = dir;
//        this.tc = tc;
//    }
//    @Override
//    public void fire() {//发出一颗炮弹的方法
//        if(!this.isLive()) return;
//        int x = this.getX() + 10;//确定子弹的坐标, 这里应该用子弹的常量计算, 待修正
//        int y = this.getY() + 10;
//        Missile m = new Missile(this.getId(), x, y, this.isGood(), this.getPtDir(),  tc);//产生一颗子弹
//        tc.getMissiles().add(m);
//
//    }
//    private void locateDirection() {
//        Dir oldDir = this.dir;
//        if(bL && !bU && !bR && !bD) dir = Dir.L;
//        else if(bL && bU && !bR && !bD) dir = Dir.LU;
//        else if(!bL && bU && !bR && !bD) dir = Dir.U;
//        else if(!bL && bU && bR && !bD) dir = Dir.RU;
//        else if(!bL && !bU && bR && !bD) dir = Dir.R;
//        else if(!bL && !bU && bR && bD) dir = Dir.RD;
//        else if(!bL && !bU && !bR && bD) dir = Dir.D;
//        else if(bL && !bU && !bR && bD) dir = Dir.LD;
//        else if(!bL && !bU && !bR && !bD) dir = Dir.STOP;
//    }
//    private void changToOldDir() {
//        dir=Dir.STOP;
//    }
//    public boolean collideHome(Home h) {   //撞到家的时候
//        if (this.live && this.getRect().intersects(h.getRect())) {
//            this.changToOldDir();
//            return true;
//        }
//        return false;
//    }
//
//    public boolean collideWithTanks(java.util.List<SingleTank> tanks) {//撞到坦克时
//        for (int i = 0; i < tanks.size(); i++) {
//            SingleTank t = tanks.get(i);
//            if (this != t) {
//                if (this.live && t.isLive()
//                        && this.getRect().intersects(t.getRect())) {
//                    this.changToOldDir();
//                    t.changToOldDir();
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    public boolean collideWithWall(CommonWall w) {  //碰撞到普通墙时
//        if (this.live && this.getRect().intersects(w.getRect())) {
//            this.changToOldDir();    //转换到原来的方向上去
//            return true;
//        }
//        return false;
//    }
//
//    public boolean collideWithWall(MetalWall w) {  //撞到金属墙
//        if (this.live && this.getRect().intersects(w.getRect())) {
//            this.changToOldDir();
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void actionToTankHitEvent(TankHitEvent tankHitEvent) {
//        this.tc.getExplodes().add(new Explode(tankHitEvent.getSource().getX() - 20,
//                tankHitEvent.getSource().getY() - 20, this.tc));//坦克自身产生一个爆炸
//        if(this.blood == 20){//坦克每次扣20滴血, 如果只剩下20滴了, 那么就标记为死亡.
//            this.live = false;
//            this.tc.gameOver();
//            return;
//        }
//        this.blood -= 20;//血量减少20
//    }
//
//    /**
//     * 血条
//     */
//    private class BloodBar {
//        public void draw(Graphics g) {
//            Color c = g.getColor();
//            g.setColor(Color.BLACK);
//            g.drawRect(x, y - 15, 30, 8);
//            int w = (30 * blood) / 100 ;
//            g.setColor(Color.RED);
//            g.fillRect(x, y - 15, w, 8);
//            g.setColor(c);
//        }
//    }
//
//    public Rectangle getRect() {
//        return new Rectangle(x, y, imgs[0].getWidth(null), imgs[0].getHeight(null));
//    }
//
//    public boolean isLive() {
//        return live;
//    }
//
//    public void setLive(boolean live) {
//        this.live = live;
//    }
//
//    public boolean isGood() {
//        return good;
//    }
//
//    public void setGood(boolean good) {
//        this.good = good;
//    }
//
//    public int getX() {
//        return x;
//    }
//
//    public void setX(int x) {
//        this.x = x;
//    }
//
//    public int getY() {
//        return y;
//    }
//
//    public void setY(int y) {
//        this.y = y;
//    }
//
//    public Dir getDir() {
//        return dir;
//    }
//
//    public void setDir(Dir dir) {
//        this.dir = dir;
//    }
//
//    public Dir getPtDir() {
//        return ptDir;
//    }
//
//    public void setPtDir(Dir ptDir) {
//        this.ptDir = ptDir;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getBlood() {
//        return blood;
//    }
//
//    public void setBlood(int blood) {
//        this.blood = blood;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public SingleTankClient getSingleTc() {
//        return tc;
//    }
//
//    public void setTc(SingleTankClient tc) {
//        this.tc = tc;
//    }
//}
