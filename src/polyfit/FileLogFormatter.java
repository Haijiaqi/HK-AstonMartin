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
        //在一条日志结束后采用常量方式的系统换行符，因为 “\n” 形式可能不识别 
        sb.append(lineSperator);
        return sb.toString();
    }
}
