import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;


import java.io.*;
import java.net.Socket;
import java.util.stream.Stream;

public class ClientHandlerTest {

    @BeforeAll
    public static void beforeAllTests() {
        System.out.println("All ClientHandlerTests started");
    }

    @AfterAll
    public static void afterAllTests() {
        System.out.println("All ClientHandlerTests completed");
    }

    @ParameterizedTest
    @MethodSource("messageTestParametersDefinition")
    public void collectiveMessageTest(String inputMessage) throws IOException {
//    arrange
        Socket socket1 = Mockito.mock(Socket.class);
        ByteArrayInputStream input1 = new ByteArrayInputStream((inputMessage).getBytes());
        ByteArrayOutputStream output1 = new ByteArrayOutputStream();
        Mockito.when(socket1.getInputStream()).thenReturn(input1);
        Mockito.when(socket1.getOutputStream()).thenReturn(output1);
        ClientHandler clientHandler1 = new ClientHandler(socket1);

        Socket socket2 = Mockito.mock(Socket.class);
        ByteArrayInputStream input2 = new ByteArrayInputStream(output1.toByteArray());
        ByteArrayOutputStream output2 = new ByteArrayOutputStream();
        Mockito.when(socket2.getInputStream()).thenReturn(input2);
        Mockito.when(socket2.getOutputStream()).thenReturn(output2);
        ClientHandler clientHandler2 = new ClientHandler(socket2);
//    act
        clientHandler1.collectiveMessage(inputMessage);
        String[] outputMessage = inputMessage.split(System.lineSeparator());
        StringBuilder sb = new StringBuilder();
        sb.append("Вы вошли в чат. Для отображения всех пользователей введите 'all', для выхода введите 'exit'.");
        sb.append(System.lineSeparator());
        sb.append(outputMessage[0]);
        sb.append(System.lineSeparator());
        sb.append(outputMessage[1]);
        sb.append(System.lineSeparator());
//    assert
        Assertions.assertEquals(sb.toString(), output2.toString());
    }

    @ParameterizedTest
    @MethodSource("messageTestParametersDefinition")
    public void personalMessageTest(String inputMessage) throws IOException {
//    arrange
        Socket socket = Mockito.mock(Socket.class);
        ByteArrayInputStream input = new ByteArrayInputStream((inputMessage).getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Mockito.when(socket.getInputStream()).thenReturn(input);
        Mockito.when(socket.getOutputStream()).thenReturn(output);
        ClientHandler clientHandler = new ClientHandler(socket);
//    act
        clientHandler.personalMessage(inputMessage);
        String[] outputMessage = inputMessage.split(System.lineSeparator());
        StringBuilder sb = new StringBuilder();
        sb.append("Вы вошли в чат. Для отображения всех пользователей введите 'all', для выхода введите 'exit'.");
        sb.append(System.lineSeparator());
        sb.append(outputMessage[0]);
        sb.append(System.lineSeparator());
        sb.append(outputMessage[1]);
        sb.append(System.lineSeparator());
//    assert
        Assertions.assertEquals(sb.toString(), output.toString());
    }

    private static Stream<Arguments> messageTestParametersDefinition() throws IOException {
        return Stream.of(
                Arguments.of("Denis" + System.lineSeparator() + "Как дела?"),
                Arguments.of("Ivan" + System.lineSeparator() + "Все хорошо!")
        );
    }

    @ParameterizedTest
    @MethodSource("removeClientHandlerTestParametersDefinition")
    public void removeClientHandlerTest(String message, String name) throws IOException {
//    arrange
        ClientHandler.clients.clear();
        Socket socket = Mockito.mock(Socket.class);
        ByteArrayInputStream input = new ByteArrayInputStream((name).getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Mockito.when(socket.getInputStream()).thenReturn(input);
        Mockito.when(socket.getOutputStream()).thenReturn(output);
        ClientHandler clientHandler = new ClientHandler(socket);
        System.setOut(new PrintStream(output));
//    act
        clientHandler.removeClientHandler();
        String[] outputMessage = message.split(name);
        StringBuilder sb = new StringBuilder();
        sb.append("Вы вошли в чат. Для отображения всех пользователей введите 'all', для выхода введите 'exit'.");
        sb.append(System.lineSeparator());
        sb.append(outputMessage[0]);
        sb.append(name);
        sb.append(outputMessage[1]);
        sb.append(System.lineSeparator());
//    assert
        Assertions.assertEquals(sb.toString(), output.toString());
        Assertions.assertTrue(ClientHandler.clients.isEmpty());
    }

    private static Stream<Arguments> removeClientHandlerTestParametersDefinition() throws IOException {
        String name = "Denis";
        return Stream.of(
                Arguments.of("Сервер: " + name + " вышел из чата", name)
        );
    }
}
