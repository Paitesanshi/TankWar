package client.bean;

import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailOperation {
    private Properties props; //系统属性
    private Session mailSession; //邮件会话对象
    private MimeMessage mimeMsg; //MIME邮件对象
    public MailOperation(String SMTPHost, String Port, String MailUsername, String MailPassword) {
        Auth au = new Auth(MailUsername, MailPassword);		//设置系统属性
        props=java.lang.System.getProperties(); //获得系统属性对象
        props.put("mail.smtp.host", SMTPHost); //设置SMTP主机
        props.put("mail.smtp.port", Port); //设置服务端口号
        props.put("mail.smtp.auth", "true"); //同时通过验证//获得邮件会话对象
        mailSession = Session.getInstance(props, au);
    }
    public boolean sendingMimeMail(String MailFrom, String MailTo,String MailCopyTo, String MailBCopyTo, String MailSubject,String MailBody) {
        try {
            //创建MIME邮件对象
            mimeMsg=new MimeMessage(mailSession);
            //设置发信人
            mimeMsg.setFrom(new InternetAddress(MailFrom));
            //设置收信人
            if(MailTo!=null){
                mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(MailTo));
            }
            //设置抄送人
            if(MailCopyTo!=null){
                mimeMsg.setRecipients(javax.mail.Message.RecipientType.CC,InternetAddress.parse(MailCopyTo));
            }
            //设置暗送人
            if(MailBCopyTo!=null){
                mimeMsg.setRecipients(javax.mail.Message.RecipientType.BCC,InternetAddress.parse(MailBCopyTo));
            }
            //设置邮件主题
            mimeMsg.setSubject(MailSubject,"utf-8");
            //设置邮件内容，将邮件body部分转化为HTML格式
            mimeMsg.setContent(MailBody,"text/html;charset=utf-8");
            //发送邮件
            Transport.send(mimeMsg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public class Auth extends Authenticator {
        private String username = "";
        private String password = "";
        public Auth(String username, String password) {
            this.username = username;
            this.password = password;
        }
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
}
