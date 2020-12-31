package client.single;

import client.background.CommonWall;
import client.background.Home;
import client.background.MetalWall;
import client.background.Tree;
import client.bean.Dir;
import client.bean.Explode;
import client.bean.MailOperation;
import client.bean.Missile;
import client.client.Tank;
import client.sound.AudioPlayer;

import java.io.File;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class SingleTankClient extends Frame implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final int Fram_width = 800; // 静态全局窗口大小
    public static final int Fram_length = 600;
    public static boolean printable = true;
    private MenuBar jmb = null;
    private Menu jm1 , jm2 , jm3 , jm4 ,jm5;
    private MenuItem jmi1 , jmi2 , jmi3 , jmi4 , jmi5 ,
            jmi6 , jmi7 , jmi8 , jmi9,jmi10,jmi11 ;
    private Image screenImage = null;
    private SingleTank homeTank = new SingleTank("tank",300, 560, true, Dir.STOP, this);// 实例化坦克
    private Home home = new Home(373, 545, this);// 实例化home
    private GameOverDialog gameOverDialog = new GameOverDialog();
    private GameWinDialog gameWinDialog = new GameWinDialog();
    private int level=1;
    private AudioPlayer player;
    private String playerMail;
    private List<SingleTank> tanks = new ArrayList<SingleTank>();
    private List<Explode> explodes = new ArrayList<Explode>();
    private List<Missile> missiles = new ArrayList<Missile>();
    private List<Tree> trees = new ArrayList<Tree>();
    private List<CommonWall> homeWall = new ArrayList<CommonWall>(); // 实例化对象容器
    private List<CommonWall> otherWall = new ArrayList<CommonWall>();
    private List<MetalWall> metalWall = new ArrayList<MetalWall>();

    public void update(Graphics g) {
        screenImage = this.createImage(Fram_width, Fram_length);
        Graphics gps = screenImage.getGraphics();
        Color c = gps.getColor();
        gps.setColor(Color.GRAY);
        gps.fillRect(0, 0, Fram_width, Fram_length);
        gps.setColor(c);
        paint(gps);
        g.drawImage(screenImage, 0, 0, null);
    }

    public void paint(Graphics g) {

        Color c = g.getColor();
        g.setColor(Color.green); // 设置字体显示属性
        Font f1 = g.getFont();
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        g.drawString("Remaining enemy tanks: ", 200, 70);
        g.setFont(new Font("TimesRoman", Font.ITALIC, 30));
        g.drawString("" + tanks.size(), 500, 70);
        g.setFont(f1);

        if (tanks.size() == 0 && home.isLive() && homeTank.isLive()) {
            Font f = g.getFont();
            g.setColor(Color.red);
            g.setFont(new Font("TimesRoman", Font.BOLD, 60)); // 判断是否赢得比赛
            this.otherWall.clear();
            g.drawString("You Win！ ", 310, 300);
            g.setFont(f);
            gameWin();
        }

        if (homeTank.isLive() == false) {
            Font f = g.getFont();
            g.setFont(new Font("TimesRoman", Font.BOLD, 40));
            tanks.clear();
            missiles.clear();
            g.setFont(f);
        }
        g.setColor(c);
        home.draw(g); // 画出home
        homeTank.draw(g);// 画出自己家的坦克
        //homeTank.eat(blood);// 充满血--生命值

        for (int i = 0; i < missiles.size(); i++) { // 对每一个子弹
            Missile m = missiles.get(i);
            for(int j=0;j<tanks.size();++j){
                m.hitTank(tanks.get(j)); // 每一个子弹打到坦克上
            }

            m.hitTank(homeTank); // 每一个子弹打到自己家的坦克上时
            m.hitHome(this); // 每一个子弹打到家里是

            for (int j = 0; j < metalWall.size(); j++) { // 每一个子弹打到金属墙上
                MetalWall mw = metalWall.get(j);
                m.hitWall(mw);
            }

            for (int j = 0; j < otherWall.size(); j++) {// 每一个子弹打到其他墙上
                CommonWall w = otherWall.get(j);
                m.hitWall(w,this);
            }

            for (int j = 0; j < homeWall.size(); j++) {// 每一个子弹打到家的墙上
                CommonWall cw = homeWall.get(j);
                m.hitWall(cw,this);
            }
            m.draw(g); // 画出效果图
        }

        for (int i = 0; i < tanks.size(); i++) {
            SingleTank t = tanks.get(i); // 获得键值对的键
            for (int j = 0; j < homeWall.size(); j++) {
                CommonWall cw = homeWall.get(j);
                t.collideWithWall(cw); // 每一个坦克撞到家里的墙时
                cw.draw(g);
            }
            for (int j = 0; j < otherWall.size(); j++) { // 每一个坦克撞到家以外的墙
                CommonWall cw = otherWall.get(j);
                t.collideWithWall(cw);
                cw.draw(g);
            }
            for (int j = 0; j < metalWall.size(); j++) { // 每一个坦克撞到金属墙
                MetalWall mw = metalWall.get(j);
                t.collideWithWall(mw);
                mw.draw(g);
            }
            t.collideWithTanks(tanks); // 撞到自己的人
            t.collideHome(home);

            t.draw(g);
        }


        for (int i = 0; i < trees.size(); i++) { // 画出trees
            Tree tr = trees.get(i);
            tr.draw(g);
        }

        for (int i = 0; i < explodes.size(); i++) { // 画出爆炸效果
            Explode bt = explodes.get(i);
            bt.draw(g);
        }

        for (int i = 0; i < otherWall.size(); i++) { // 画出otherWall
            CommonWall cw = otherWall.get(i);
            cw.draw(g);
        }

        for (int i = 0; i < metalWall.size(); i++) { // 画出metalWall
            MetalWall mw = metalWall.get(i);
            mw.draw(g);
        }

        homeTank.collideWithTanks(tanks);
        homeTank.collideHome(home);

        for (int i = 0; i < metalWall.size(); i++) {// 撞到金属墙
            MetalWall w = metalWall.get(i);
            homeTank.collideWithWall(w);
            w.draw(g);
        }

        for (int i = 0; i < otherWall.size(); i++) {
            CommonWall cw = otherWall.get(i);
            homeTank.collideWithWall(cw);
            cw.draw(g);
        }

        for (int i = 0; i < homeWall.size(); i++) { // 家里的坦克撞到自己家
            CommonWall w = homeWall.get(i);
            homeTank.collideWithWall(w);
            w.draw(g);
        }

    }
    class GameWinDialog extends Dialog{
        JTextField email=new JTextField(20);
        JButton submit=new JButton("submit");
        JButton again=new JButton("Again");
        JLabel hint=new JLabel("Please enter your e-mail and we will send a message to you!");
        JPanel p=new JPanel();
        public GameWinDialog()
        {
            super(SingleTankClient.this,true);
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            this.setTitle("Congratulations!");
            this.add(hint);
            p.add(email);
            p.add(submit);
            this.add(p);
            this.setLocation(500,400);
            this.pack();
            submit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    dispose();
                    SingleTankClient.this.dispose();
                    MailOperation mail=new MailOperation("smtp.163.com","smtp.163.com","zxc89473324@163.com", "zxc89473324");
                    try {
                        mail.sendingMimeMail("zxc89473324@163.com", email.getText(), "", "", "强●者●证●明！", "恭喜！你完成了level"+level+"的挑战！特发此邮件，请收下这强者的证明！");
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });

        }
    }
    class GameOverDialog extends Dialog{
        Button exit = new Button("Exit");
        Button again=new Button("Again");
        public GameOverDialog() {
            super(SingleTankClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("Game Over~"));
            this.add(again);
            this.add(exit);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                  SingleTankClient.this.dispose();
                }
            });
            exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    SingleTankClient.this.dispose();
                }
            });
            again.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    SingleTankClient.this.dispose();
                    new SingleTankClient();
                }
            });
        }
    }
    public SingleTankClient() {
        // printable = false;
        // 创建菜单及菜单选项
        jmb = new MenuBar();
        jm1 = new Menu("Game");
        jm2 = new Menu("Stop/Continue");
        jm3 = new Menu("Help");
        jm4 = new Menu("Difficulty level");
        jm1.setFont(new Font("TimesRoman", Font.BOLD, 15));// 设置菜单显示的字体
        jm2.setFont(new Font("TimesRoman", Font.BOLD, 15));// 设置菜单显示的字体
        jm3.setFont(new Font("TimesRoman", Font.BOLD, 15));// 设置菜单显示的字体
        jm4.setFont(new Font("TimesRoman", Font.BOLD, 15));// 设置菜单显示的字体
        jmi1 = new MenuItem("New game");
        jmi2 = new MenuItem("Exit");
        jmi3 = new MenuItem("Stop");
        jmi4 = new MenuItem("Continue");
        jmi5 = new MenuItem("Explanation");
        jmi6 = new MenuItem("Level 1");
        jmi7 = new MenuItem("Level 2");
        jmi8 = new MenuItem("Level 3");
        jmi9 = new MenuItem("Level 4");
        jmi1.setFont(new Font("TimesRoman", Font.BOLD, 15));
        jmi2.setFont(new Font("TimesRoman", Font.BOLD, 15));
        jmi3.setFont(new Font("TimesRoman", Font.BOLD, 15));
        jmi4.setFont(new Font("TimesRoman", Font.BOLD, 15));
        jmi5.setFont(new Font("TimesRoman", Font.BOLD, 15));
        jm1.add(jmi1);
        jm1.add(jmi2);
        jm2.add(jmi3);
        jm2.add(jmi4);
        jm3.add(jmi5);
        jm4.add(jmi6);
        jm4.add(jmi7);
        jm4.add(jmi8);
        jm4.add(jmi9);
        jmb.add(jm1);
        jmb.add(jm2);
        jmb.add(jm3);
        jmb.add(jm4);
        jmi1.addActionListener(this);
        jmi1.setActionCommand("NewGame");
        jmi2.addActionListener(this);
        jmi2.setActionCommand("Exit");
        jmi3.addActionListener(this);
        jmi3.setActionCommand("Stop");
        jmi4.addActionListener(this);
        jmi4.setActionCommand("Continue");
        jmi5.addActionListener(this);
        jmi5.setActionCommand("help");
        jmi6.addActionListener(this);
        jmi6.setActionCommand("level1");
        jmi7.addActionListener(this);
        jmi7.setActionCommand("level2");
        jmi8.addActionListener(this);
        jmi8.setActionCommand("level3");
        jmi9.addActionListener(this);
        jmi9.setActionCommand("level4");
        this.setMenuBar(jmb);// 菜单Bar放到JFrame上

        for (int i = 0; i < 10; i++) { // 家的格局
            if (i < 4)
                homeWall.add(new CommonWall(350, 580 - 21 * i, this));
            else if (i < 7)
                homeWall.add(new CommonWall(372 + 22 * (i - 4), 517, this));
            else
                homeWall.add(new CommonWall(416, 538 + (i - 7) * 21, this));

        }

        for (int i = 0; i < 32; i++) {
            if (i < 16) {
                otherWall.add(new CommonWall(220 + 20 * i, 300, this)); // 普通墙布局
               // otherWall.add(new CommonWall(500 + 20 * i, 180, this));
                otherWall.add(new CommonWall(200, 400 + 20 * i, this));
                otherWall.add(new CommonWall(500, 400 + 20 * i, this));
            } else if (i < 32) {
                otherWall.add(new CommonWall(220 + 20 * (i - 16), 320, this));
                //otherWall.add(new CommonWall(500 + 20 * (i - 16), 220, this));
                otherWall.add(new CommonWall(220, 400 + 20 * (i - 16), this));
                otherWall.add(new CommonWall(520, 400 + 20 * (i - 16), this));
            }
        }

        for (int i = 0; i < 10; i++) { // 金属墙布局
            //if (i < 10) {
                metalWall.add(new MetalWall(180 + 30 * i, 150, this));
                metalWall.add(new MetalWall(600, 400 + 20 * (i), this));
            /*else if (i < 20)
                metalWall.add(new MetalWall(140 + 30 * (i - 10), 180, this));
            else
                metalWall.add(new MetalWall(500 + 30 * (i - 10), 160, this));*/
        }

        for (int i = 0; i < 4; i++) { // 树的布局
            if (i < 4) {
                trees.add(new Tree(0 + 30 * i, 360, this));
                trees.add(new Tree(220 + 30 * i, 360, this));
                trees.add(new Tree(440 + 30 * i, 360, this));
                trees.add(new Tree(660 + 30 * i, 360, this));
            }

        }

        for (int i = 0; i < 20; i++) { // 初始化20辆坦克
            if (i < 9) // 设置坦克出现的位置
                tanks.add(new SingleTank("",150 + 70 * i, 40, false, Dir.D, this));
            else if (i < 15)
                tanks.add(new SingleTank("",700, 140 + 50 * (i - 6), false, Dir.D, this));
            else
                tanks.add(new SingleTank("",10, 50 * (i - 12), false, Dir.D, this));
        }

        this.setSize(Fram_width, Fram_length); // 设置界面大小
        this.setLocation(280, 50); // 设置界面出现的位置
        this.setTitle("TANK WAR");
        this.setResizable(false);
        this.setBackground(Color.GREEN);
        File file=new File("src/client/sound/start.mp3");
        player=new AudioPlayer(file);//音乐
        player.start();
        this.addWindowListener(new WindowAdapter() { // 窗口监听关闭
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        this.addKeyListener(new KeyMonitor());// 键盘监听
        new Thread(new PaintThread()).start(); // 线程启动
         this.setVisible(true);
    }

    private class PaintThread implements Runnable {
        public void run() {
            // TODO Auto-generated method stub
            while (printable) {
                repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class KeyMonitor extends KeyAdapter {

        public void keyReleased(KeyEvent e) { // 监听键盘释放
            homeTank.keyReleased(e);
        }

        public void keyPressed(KeyEvent e) { // 监听键盘按下
            homeTank.keyPressed(e);
        }

    }
    public List<Missile> getMissiles() {
        return missiles;
    }

    public List<CommonWall> getHomeWall() {
        return homeWall;
    }

    public List<CommonWall> getOtherWall() {
        return otherWall;
    }

    public List<MetalWall> getMetalWall() {
        return metalWall;
    }

    public List<Tree> getTrees() {
        return trees;
    }
    public SingleTank getHomeTank() {
        return homeTank;
    }

    public List<Explode> getExplodes() {

        return explodes;
    }

    public Home getHome() {
        return home;
    }

    public List<SingleTank> getTanks() {
        return tanks;
    }
    public void gameOver(){

        this.gameOverDialog.setVisible(true);

    }
    public void gameWin()
    {
        this.gameWinDialog.setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("NewGame")) {
            printable = false;
            Object[] options = { "Yes", "No" };
            int response = JOptionPane.showOptionDialog(this, "Are you sure you want to start a new game?", "",
                    JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    options, options[0]);
            if (response == 0) {

                printable = true;
                this.dispose();
                new SingleTankClient();
            } else {
                printable = true;
                new Thread(new PaintThread()).start(); // 线程启动
            }

        } else if (e.getActionCommand().endsWith("Stop")) {
            printable = false;
            // try {
            // Thread.sleep(10000);
            //
            // } catch (InterruptedException e1) {
            // // TODO Auto-generated catch block
            // e1.printStackTrace();
            // }
        } else if (e.getActionCommand().equals("Continue")) {

            if (!printable) {
                printable = true;
                new Thread(new PaintThread()).start(); // 线程启动
            }
            // System.out.println("继续");
        } else if (e.getActionCommand().equals("Exit")) {
            printable = false;
            Object[] options = { "Yes", "No" };
            int response = JOptionPane.showOptionDialog(this, "Are you sure you want to exit?", "",
                    JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    options, options[0]);
            if (response == 0) {
                System.out.println("退出");
                System.exit(0);
            } else {
                printable = true;
                new Thread(new PaintThread()).start(); // 线程启动

            }

        } else if (e.getActionCommand().equals("help")) {
            printable = false;
            JOptionPane.showMessageDialog(null, "用WASD控制方向，J键发射",
                    "提示！", JOptionPane.INFORMATION_MESSAGE);
            this.setVisible(true);
            printable = true;
            new Thread(new PaintThread()).start(); // 线程启动
        } else if (e.getActionCommand().equals("level1")) {
            level=1;
            SingleTank.XSPEED = 6;
            Tank.YSPEED = 6;
            Missile.XSPEED = 10;
            Missile.YSPEED = 10;
            this.dispose();
            new SingleTankClient();
        } else if (e.getActionCommand().equals("level2")) {
            level=2;
           //// Tank.count = 12;
            SingleTank.XSPEED = 10;
            SingleTank.YSPEED = 10;
            Missile.XSPEED = 12;
            Missile.YSPEED = 12;
            this.dispose();
            new SingleTankClient();

        } else if (e.getActionCommand().equals("level3")) {
            level=3;
            //SingleTank.count = 20;
            SingleTank.XSPEED = 14;
            SingleTank.YSPEED = 14;
            Missile.XSPEED = 16;
            Missile.YSPEED = 16;
            this.dispose();
            new SingleTankClient();
        } else if (e.getActionCommand().equals("level4")) {
            level=4;
            //SingleTank.count = 20;
            SingleTank.XSPEED = 16;
            SingleTank.YSPEED = 16;
            Missile.XSPEED = 18;
            Missile.YSPEED = 18;
            this.dispose();
            new SingleTankClient();
        }

    }
}
