import java.io.*;
import java.net.*;

import Logger.*;

public class Server {
    public static Logger logger = LoggerImpl.getInstance();

    public Server() {
    }

    public void startServer() {
        String portNumber = null;
//        считываем номер порта из файла
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("settings.txt"))
        ) {
            portNumber = bufferedReader.readLine();
        } catch (
                IOException ex) {
            String message = "Не удалось прочитать файл настроек сервера";
            System.out.println(message);
            logger.log(this, message);
            ex.printStackTrace();
        }

        int port = 0;
        try {
//            парсим номер порта из прочитанного значения
            port = Integer.parseInt(portNumber);
            logger.log(this, "Server started");
        } catch (
                NumberFormatException ex) {
            String message = "Номер порта не распознан";
            System.out.println(message);
            logger.log(this, message);
            ex.printStackTrace();
        }
//        создаем новое сетевое соединение
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept(); // ждем подключения
//            создаем обработчик клиента, который подключился к серверу
                ClientHandler clientHandler = new ClientHandler(clientSocket);
//                создаем новый поток на работу с клиентом
                Thread thread = new Thread(clientHandler);
                System.out.println("Подключение клиента "
                        + clientHandler.clientName
                        + " "
                        + clientSocket.getInetAddress()
                        .getHostAddress());
                logger.log(this, "Подключение клиента "
                        + clientHandler.clientName
                        + clientSocket.getInetAddress()
                        .getHostAddress());
                thread.start();
            }
        } catch (
                IOException ex) {
            ex.getMessage();
        }
    }

    @Override
    public String toString() {
        return "Server: ";
    }
}

