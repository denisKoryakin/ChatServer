import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Logger.*;
import org.w3c.dom.ls.LSOutput;

public class ClientHandler extends Thread {

    public static List<ClientHandler> clients = new ArrayList<>();
    final String clientName;
    private final Socket socket;
    private BufferedWriter bw;
    private BufferedReader br;
    private Logger logger;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        this.bw = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.clientName = br.readLine();
        logger = LoggerImpl.getInstance();
        clients.add(this);
//        оповещенио о входе новго клиента
        StringBuilder stringBuilder = new StringBuilder();
        for (ClientHandler clientHandler : clients) {
            stringBuilder.append(clientHandler.clientName);
            stringBuilder.append(";\n");
        }
        String clientsOnline = stringBuilder.toString();
        sendMessage("Сервер: " + clientName + " подключился к беседе." + "В сети: " + clientsOnline);
    }

    @Override
    public void run() {
        String inputMessage;
        while (true) {
            try {
//                парсим входной поток в строку
                inputMessage = br.readLine();
                System.out.println(inputMessage);
//                обработка сообщения exit
                String[] message = inputMessage.split(": ");
                if (message[1].equals("exit")) {
                    removeClientHandler();
                } else {
//                    обработка других входящих сообщений (отправка сообщения другим пользователям через выходной поток обработчика клиента)
                    sendMessage(inputMessage);
                }
            } catch (IOException ex) {
                System.out.println("Потеряно соединение с клиентом" + clientName);
                logger.log(this, "Потеряно соединение с клиентом" + clientName);
                ex.getMessage();
                break;
            }
        }
        try {
            bw.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    метод отправки сообщений
    public void sendMessage(String message) throws IOException {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.clientName.equals(this.clientName)) {
                clientHandler.bw.write(message);
                logger.log(this, message);
                clientHandler.bw.newLine();
                clientHandler.bw.flush();
            }
        }
    }

    //  метод остановки потока и удаление клиента
    public void removeClientHandler() throws IOException {
        System.out.println("Сервер: " + clientName + " вышел из чата");
        logger.log(this,"Клиент " + this.clientName + " socket: " + this.socket + " вышел из чата");
        sendMessage("Сервер: " + clientName + " вышел из чата");
        clients.remove(this);
    }

    @Override
    public String toString() {
        return "clientHandler: ";
    }
}

