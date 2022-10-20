package polyfit;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class FileLogFormatter extends Formatter {

    public FileLogFormatter(){
        super();
    }

    @Override
    public String format(LogRecord r) {
        Date date = new Date();
        String sDate = date.toString();
        String lineSperator = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();
        sb.append("[" + sDate + "]" + "[" + r.getLevel() +"]");
        sb.append(r.getMessage());
        //��һ����־��������ó�����ʽ��ϵͳ���з�����Ϊ ��\n�� ��ʽ���ܲ�ʶ�� 
        sb.append(lineSperator);
        return sb.toString();
    }
}
