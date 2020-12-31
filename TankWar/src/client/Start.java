package client;

import client.client.TankClient;
import client.single.SingleTankClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class Start extends JFrame{
    Socket socket=null;
    private JLabel title;
    private JButton single;
    private JButton couple;
    private ImageIcon icon;
    private JLabel backLabel;
    private JButton slient;
    public Start()  {
        setTitle("TANK WAR");
        setSize(new Dimension(800  ,600));
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        title=new JLabel("TANK WAR");
        title.setFont(new Font(Font.DIALOG, Font.BOLD,32));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(Color.RED);
        single=new JButton("SINGLE");
        single.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SingleTankClient singleTankClient=new SingleTankClient();
            }
        });
        single.setPreferredSize(new Dimension(250,50));
        couple=new JButton("MULTIPLY ");
        couple.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TankClient tankClient=new TankClient();
            }
        });
        couple.setPreferredSize(new Dimension(250,50));
        getContentPane().add(title,BorderLayout.NORTH);
        JPanel p=new JPanel();
        p.add(single);
        p.add(couple);
        p.setOpaque(false);
        add(p,BorderLayout.CENTER);
        setBackLabel();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    public void setBackLabel(){
        ((JPanel)getContentPane()).setOpaque(false);
        icon = new ImageIcon("src/client/images/background/background.jpg"); //添加图片
        icon.setImage(icon.getImage().getScaledInstance(getWidth(),getHeight(),Image.SCALE_DEFAULT));
        backLabel = new JLabel(icon);
        getLayeredPane().add(backLabel, new Integer(Integer.MIN_VALUE));
        backLabel.setBounds(0, 0, getWidth(), getHeight());
    }
    public static void main(String[] args) {
        Start s=new Start();
    }

}
