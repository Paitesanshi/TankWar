package client.bean;
import java.text.SimpleDateFormat;
import java.util.Date;
public class NowTime {
    public static String getTime()
    {
        long timemillis = System.currentTimeMillis();
        //转换日期显示格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date(timemillis));
    }

}
