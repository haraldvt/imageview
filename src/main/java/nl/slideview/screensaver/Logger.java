package nl.slideview.screensaver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Logger {
    static public void log(String msg) {
        try {
            Path pathOfLog = Paths.get("c:/tmp/slideview_log.txt");
            Charset charSetOfLog = StandardCharsets.US_ASCII;
            BufferedWriter bwOfLog = Files.newBufferedWriter(pathOfLog, charSetOfLog, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            bwOfLog.append(getTime() + ": " + msg, 0, msg.length() + 6);
            bwOfLog.newLine();
            bwOfLog.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("HHmm", Locale.forLanguageTag("NL"));
        return format.format(Calendar.getInstance().getTime());
    }
}
