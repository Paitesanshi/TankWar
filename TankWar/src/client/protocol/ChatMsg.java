package client.protocol;

import client.bean.Dir;
import client.bean.Missile;
import client.bean.NowTime;
import client.client.TankClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class ChatMsg implements Msg {
    private int msgType = Msg.CHAT_MSG;
    private TankClient tc;
    private String msg;
    private String name;
    public ChatMsg(TankClient tc){
        this.tc = tc;
    }
    public ChatMsg(String name,String s){this.name=name;msg=s;}
    @Override
    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeUTF(name);
            dos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buf = baos.toByteArray();
        try{
            DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, UDP_Port));
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(DataInputStream dis) {
        try{
         String name=dis.readUTF();
         String ss=dis.readUTF();
         tc.getChatroom().getArea().append(name+" "+ NowTime.getTime()+":\n"+ss+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

