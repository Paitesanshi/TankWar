package server;

import client.client.TankClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static client.client.TankClient.GAME_HEIGHT;
import static client.client.TankClient.GAME_WIDTH;

/**
 * 服务器端
 */
public class TankServer extends Frame {//repaint在轻量级组件（如JFrame）会调用paint方法，重量级组件（如Frame）会调用updata方法，所以这里必须用Frame

    public static int ID = 100;//id号的初始序列，每有一个客户端ID++
    public static final int TCP_PORT = 55555;//TCP端口号
    public static final int UDP_PORT = 55556;//转发客户端数据的UDP端口号
    public static final int TANK_DEAD_UDP_PORT = 55557;//接收客户端坦克死亡的端口号
    private List<Client> clients = new ArrayList<>();//客户端集合
    private Image offScreenImage = null;//服务器画布
    private static final int SERVER_HEIGHT = 500;
    private static final int SERVER_WIDTH = 300;

    public static void main(String[] args) {
        TankServer ts = new TankServer();
        ts.launchFrame();
        ts.start();
    }

    public void start(){
        new Thread(new UDPThread()).start();//接收UDP信息
        new Thread(new TankDeadUDPThread()).start();//接收坦克死亡信息
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(TCP_PORT);//在TCP欢迎套接字上监听客户端连接
            System.out.println("TankServer has started...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            Socket s = null;
            try {
                s = ss.accept();//给客户端分配专属TCP套接字
                System.out.println("A client has connected...");
                DataInputStream dis = new DataInputStream(s.getInputStream());
                int UDP_PORT = dis.readInt();//记录客户端UDP端口
                Client client = new Client(s.getInetAddress().getHostAddress(), UDP_PORT, ID);//创建Client对象
                clients.add(client);//添加进客户端容器

                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeInt(ID++);//向客户端分配id号
                dos.writeInt(TankServer.UDP_PORT);//告诉客户端自己的UDP端口号
                dos.writeInt(TankServer.TANK_DEAD_UDP_PORT);//监听坦克死亡端口号
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(s != null) s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class UDPThread implements Runnable{

        byte[] buf = new byte[1024];

        @Override
        public void run() {
            DatagramSocket ds = null;
            try{
                ds = new DatagramSocket(UDP_PORT);
            }catch (SocketException e) {
                e.printStackTrace();
            }

            while (null != ds){
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                try {
                    ds.receive(dp);//接收客户端变化
                    for (int i=0;i<clients.size();++i){//将消息传给其他客户端
                        dp.setSocketAddress(new InetSocketAddress(clients.get(i).IP, clients.get(i).UDP_PORT));
                        ds.send(dp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 监听坦克死亡的UDP线程
     */
    private class TankDeadUDPThread implements Runnable{
        byte[] buf = new byte[300];
        @Override
        public void run() {
            DatagramSocket ds = null;
            try{
                ds = new DatagramSocket(TANK_DEAD_UDP_PORT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            while(null != ds){
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                ByteArrayInputStream bais = null;
                DataInputStream dis = null;
                try{
                    ds.receive(dp);
                    bais = new ByteArrayInputStream(buf, 0, dp.getLength());
                    dis = new DataInputStream(bais);
                    int deadTankUDPPort = dis.readInt();
                    for(int i = 0; i < clients.size(); i++){//找到死亡坦克客户端并注销
                        Client c = clients.get(i);
                        if(c.UDP_PORT == deadTankUDPPort){
                            clients.remove(c);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (null != dis){
                        try {
                            dis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(null != bais){
                        try {
                            bais.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public class Client{
        String IP;//客户端IP地址
        int UDP_PORT;//UDP端口号
        int id;//客户端ID编号

        public Client(String ipAddr, int UDP_PORT, int id) {
            this.IP = ipAddr;
            this.UDP_PORT = UDP_PORT;
            this.id = id;
        }
    }

    /************** 服务器可视化 **************/
    @Override
    public void paint(Graphics g) {
        g.setFont(new Font("TimesRoman",Font.PLAIN,30));
        g.drawString("TankClient :", 20, 70);
        g.setFont(new Font("TimesRoman",Font.PLAIN,15));
        int y = 110;
        for(int i = 0; i < clients.size(); i++){//显示出每个客户端的信息
            Client c = clients.get(i);
            g.drawString("id : " + c.id + " - IP : " + c.IP, 30, y);
            y += 30;
        }
    }

    @Override
    public void update(Graphics g) {//双缓冲防闪烁，先将所有东西画到一张画布上，在一次把画布画出，而不是一个一个画
        if(offScreenImage == null) {
            offScreenImage = this.createImage(SERVER_WIDTH, SERVER_HEIGHT);
        }
        Graphics gOffScreen = offScreenImage.getGraphics();//把offScreenImage的画笔给OffScreen
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.white);
        gOffScreen.fillRect(0, 0, SERVER_WIDTH, SERVER_HEIGHT);
        gOffScreen.setColor(c);
        paint(gOffScreen);
        g.drawImage(offScreenImage, 0, 0, null);
    }

    public void launchFrame() {
        this.setLocation(200, 100);
        this.setSize(SERVER_WIDTH, SERVER_HEIGHT);
        this.setTitle("TankServer");
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.setResizable(false);
        this.setBackground(Color.yellow);
        this.setVisible(true);
        new Thread(new PaintThread()).start();

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
}
