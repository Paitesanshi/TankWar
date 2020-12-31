package client.client;

import client.protocol.ChatMsg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Chatroom extends JFrame {
    private JPanel chatpanel=new JPanel();
    private JTextArea tArea;
    private JTextField tField;
    private JScrollPane jScrollPane;
    private JButton button1;
    public Chatroom(TankClient tc)
    {
        this.setLocation(1000,100);
        this.setLayout(new BorderLayout());
        this.setSize(400,600);
        tArea=new JTextArea(" ",10,10);
        tArea.setFont(new Font("Serif",Font.PLAIN,20));
        tArea.setEditable(false);
        jScrollPane=new JScrollPane(tArea);
        tField=new JTextField(30);
        tField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar()==KeyEvent.VK_ENTER)
                {
                    if(tField.getText()!="")
                    {
                        ChatMsg chatMsg=new ChatMsg(tc.getMyTank().getName(),tField.getText());
                        tc.getNc().send(chatMsg);
                        tField.setText("");
                    }
                }
            }
        });
        button1=new JButton("Send");
        button1.setFont(new Font("Serif",Font.BOLD,18));
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(tField.getText()!="")
                {
                    ChatMsg chatMsg=new ChatMsg(tc.getMyTank().getName(),tField.getText());
                    tc.getNc().send(chatMsg);
                    tField.setText("");
                }
            }
        });
        tArea.setBorder(BorderFactory.createTitledBorder("Chat room"));
        JPanel p=new JPanel();
        p.add(tField);
        p.add(button1);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(p,BorderLayout.SOUTH);
        this.setVisible(true);
    }
    public JTextArea getArea()
    {
        return tArea;
    }

}
