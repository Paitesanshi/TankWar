package client.client;

import client.bean.Dir;
import client.bean.Explode;
import client.bean.Missile;
import client.protocol.MissileDeadMsg;
import client.background.MetalWall;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class TankClient extends Frame {
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    private Image offScreenImage = null;
    private Tank myTank;//客户端的坦克
    private Chatroom chatroom;
    private NetClient nc = new NetClient(this);
    private ConDialog dialog = new ConDialog();
    private GameOverDialog gameOverDialog = new GameOverDialog();
    private UDPPortWrongDialog udpPortWrongDialog = new UDPPortWrongDialog();
    private ServerNotStartDialog serverNotStartDialog = new ServerNotStartDialog();
    private List<Missile> missiles = new ArrayList<>();//存储游戏中的子弹集合
    private List<Explode> explodes = new ArrayList<>();//爆炸集合
    private List<Tank> tanks = new ArrayList<>();//坦克集合
    private List<MetalWall> metalWall = new ArrayList<MetalWall>();
    @Override
    public void paint(Graphics g) {
        int teammate=0;
        int enemies=0;
        for(int i=0;i<tanks.size();++i)
        {
            if(tanks.get(i).getGood()==myTank.getGood())teammate++;
            else enemies++;
        }
        g.setFont(new Font("TimesRoman",Font.PLAIN,20));
        g.drawString("Enemy tanks count:" +enemies, 10, 50);
        g.drawString("Team tanks count:" + teammate, 10, 80);
        g.setFont(new Font("微软雅黑",Font.PLAIN,15));
        for(int i = 0; i < missiles.size(); i++) {
            Missile m = missiles.get(i);
            if(m.hitTank(myTank)){
                MissileDeadMsg mmsg = new MissileDeadMsg(m.getTankId(), m.getId());
                nc.send(mmsg);
            }
            m.draw(g);
        }
        for(int i = 0; i < explodes.size(); i++) {
            Explode e = explodes.get(i);
            e.draw(g);
        }
        for(int i = 0; i < tanks.size(); i++) {
            Tank t = tanks.get(i);
            t.draw(g);
        }
        for (int i = 0; i < metalWall.size(); i++) { // 画出metalWall
            MetalWall mw = metalWall.get(i);
            mw.draw(g);
        }
        for (int i = 0; i < missiles.size(); i++) { // 对每一个子弹
            Missile m = missiles.get(i);
            for (int j = 0; j < metalWall.size(); j++) { // 每一个子弹打到金属墙上
                MetalWall mw = metalWall.get(j);
                m.hitWall(mw);
            }
            m.draw(g); // 画出效果图
        }
        for (int i = 0; i < metalWall.size(); i++) {// 撞到金属墙
            MetalWall w = metalWall.get(i);
            if(myTank!=null)myTank.collideWithWall(w);
            w.draw(g);
        }
        if(null != myTank){
            myTank.draw(g);
        }
    }
    @Override
    public void update(Graphics g) {
        if(offScreenImage == null) {
            offScreenImage = this.createImage(800, 600);
        }
        Graphics gOffScreen = offScreenImage.getGraphics();
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.GRAY);
        gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        gOffScreen.setColor(c);
        paint(gOffScreen);
        g.drawImage(offScreenImage, 0, 0, null);
    }
    public void launchFrame() {


        this.setLayout(null);
        this.setLocation(200, 100);
        this.setSize(GAME_WIDTH, GAME_HEIGHT);
        this.setTitle("TankClient");
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                nc.sendClientDisconnectMsg();//关闭窗口前要向服务器发出注销消息.
                dispose();
            }
        });
        for(int i=0;i<5;++i)
        {
            metalWall.add(new MetalWall(140 + 30 * i, 150));
            metalWall.add(new MetalWall(140 , 150+30*i));
            metalWall.add(new MetalWall(440 + 30 * i, 150));
            metalWall.add(new MetalWall(440 + 30 * 4, 150+30*i));
            metalWall.add(new MetalWall(140 + 30 * i, 480));
            metalWall.add(new MetalWall(140 , 360+30*i));
            metalWall.add(new MetalWall(440 + 30 * i, 480));
            metalWall.add(new MetalWall(440 + 30 * 4, 360+30*i));
        }
        this.setResizable(false);
        this.setBackground(Color.LIGHT_GRAY);
        this.addKeyListener(new KeyMonitor());
        this.setVisible(true);
        new Thread(new PaintThread()).start();
        dialog.setVisible(true);
    }
    public TankClient(){
        launchFrame();
        chatroom=new Chatroom(this);

     }
     /**
     * 重画线程
     */
    class PaintThread implements Runnable {
        public void run() {
            while(true) {
                repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class KeyMonitor extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            myTank.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            myTank.keyPressed(e);
        }
    }

    /**
     * 游戏开始前连接到服务器的对话框
     */
    class ConDialog extends Dialog{
        Button b = new Button("connect to server");
        TextField tfIP = new TextField("127.0.0.1", 15);//服务器的IP地址
        TextField tfTankName = new TextField("myTank", 8);

        public ConDialog() {
            super(TankClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("server IP:"));
            this.add(tfIP);
            this.add(new Label("tank name:"));
            this.add(tfTankName);
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String IP = tfIP.getText().trim();
                    String tankName = tfTankName.getText().trim();

                    myTank = new Tank(tankName, 50 + (int)(Math.random() * (GAME_WIDTH - 100)),
                            50 + (int)(Math.random() * (GAME_HEIGHT - 100)), true, Dir.STOP, TankClient.this);
                    for(int i=0;i<metalWall.size();++i)//防止坦克出现在金属墙上
                    {
                        if(myTank.collideWithWall(metalWall.get(i)))
                        {
                            myTank = new Tank(tankName, 50 + (int)(Math.random() * (GAME_WIDTH - 100)),
                                    50 + (int)(Math.random() * (GAME_HEIGHT - 100)), true, Dir.STOP, TankClient.this);
                        }
                    }
                    nc.connect(IP);
                    setVisible(false);

                }
            });
        }
    }

    /**
     * 坦克死亡后退出的对话框
     */
    class GameOverDialog extends Dialog{
        Button exit = new Button("exit");
        public GameOverDialog() {
            super(TankClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("Game Over~"));
            this.add(exit);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
            exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
        }
    }

    /**
     * UDP端口分配失败后的对话框
     */
    class UDPPortWrongDialog extends Dialog{
        Button b = new Button("ok");
        public UDPPortWrongDialog() {
            super(TankClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("something wrong, please connect again"));
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {

                    dispose();
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
        }
    }

    /**
     * 连接服务器失败后的对话框
     */
    class ServerNotStartDialog extends Dialog{
        Button b = new Button("ok");
        public ServerNotStartDialog() {
            super(TankClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("The server has not been opened yet..."));
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {

                 dispose();
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    dispose();
                }
            });
        }
    }

    public Chatroom getChatroom() {
        return chatroom;
    }

    public void gameOver(){
        this.gameOverDialog.setVisible(true);
    }

    public List<Missile> getMissiles() {
        return missiles;
    }

    public void setMissiles(List<Missile> missiles) {
        this.missiles = missiles;
    }

    public List<Explode> getExplodes() {
        return explodes;
    }

    public void setExplodes(List<Explode> explodes) {
        this.explodes = explodes;
    }

    public List<Tank> getTanks() {
        return tanks;
    }

    public void setTanks(List<Tank> tanks) {
        this.tanks = tanks;
    }

    public Tank getMyTank() {
        return myTank;
    }

    public void setMyTank(Tank myTank) {
        this.myTank = myTank;
    }

    public NetClient getNc() {
        return nc;
    }

    public void setNc(NetClient nc) {
        this.nc = nc;
    }

    public UDPPortWrongDialog getUdpPortWrongDialog() {
        return udpPortWrongDialog;
    }

    public ServerNotStartDialog getServerNotStartDialog() {
        return serverNotStartDialog;
    }
}