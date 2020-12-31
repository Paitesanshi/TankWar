package client.bean;

import client.client.TankClient;
import client.single.SingleTank;
import client.single.SingleTankClient;

import java.awt.*;

/**
 * 子弹击中坦克后产生的爆炸
 */
public class Explode {
    private int x, y;//爆炸的坐标
    private boolean live = true;//爆炸的生命
    private TankClient tc;
    private SingleTankClient stc;
    private int step = 0;//播放图片的计数器
    private static boolean init = false;//在正式画出爆炸之前先在其他地方画出一次爆炸, 确保爆炸的图片加入到内存中
    private static Toolkit tk = Toolkit.getDefaultToolkit();
    private static Image[] images = {
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode1.png")),//从src向下找路径
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode2.png")),
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode3.png")),
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode4.png")),
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode5.png")),
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode6.png")),
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode7.png"))
    };

    public Explode(int x, int y, TankClient tc) {
        this.x = x;
        this.y = y;
        this.tc = tc;
    }
    public Explode(int x, int y, SingleTankClient tc) {
        this.x = x;
        this.y = y;
        this.stc = tc;
    }
    public void draw(Graphics g) {
        if(!init){//先在其他地方画一次爆炸
            for(int i = 0; i < images.length; i++){
                g.drawImage(images[i], -100, -100, null);//在屏幕外画一次，是图片加载到内存中。Toolkit获得图片没有加载到内存
            }
            init = true;
        }
        if(!live) {//爆炸炸完了就从容器移除
            if(tc!=null)tc.getExplodes().remove(this);
            else stc.getExplodes().remove(this);
            return;
        }
        if(step == images.length){//把爆炸数组中的图片都画一次
            live = false;
            step = 0;
            return;
        }
        g.drawImage(images[step++], x, y, null);
    }
}