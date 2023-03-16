package Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;


public class LoggerImpl implements Logger{

    private static class Loader {
        static final LoggerImpl INSTANCE = new LoggerImpl();
    }

    private LoggerImpl () {}

    public static LoggerImpl getInstance() {
        return Loader.INSTANCE;
    }

    Date date = new Date();

    // TODO: 09.03.2023 Почему то запись в файл происходит после завершения работы программы

    @Override
    public void log(Object object, String text) {
        try (FileOutputStream fos = new FileOutputStream("logs.txt", true)) {
//        переводим текст в массив байт
            String stringBuilder =
                    object.toString() +
                    String.format("%td %tB %tY года %tH:%tM:%tS - ", date, date, date, date, date, date) +
                    text +
                    "\n";
            byte[] bytes = stringBuilder.getBytes(StandardCharsets.UTF_8);
//        запись байт в файл
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
