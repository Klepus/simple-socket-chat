package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Enter server port: ");
        try (ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt())) {
            ConsoleHelper.writeMessage("The Server is running.");
            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void sendBroadcastMessage(Message message) {
        try {
            for (Map.Entry<String, Connection> map : connectionMap.entrySet()) {
                map.getValue().send(message);
            }
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Обработчик для обмена сообщениями с клиентом.
     */
    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Connected to the server: " + socket.getRemoteSocketAddress());
            String userName = null;
            try (Connection connection = new Connection(socket)) {
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Error while communicating with the server: " + e.getMessage());
            } finally {
                if (userName != null) {
                    connectionMap.remove(userName);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                    ConsoleHelper.writeMessage("Server connection terminated.");
                }
            }
        }

        /**
         *
         * @param connection
         * @return New UserName
         */
        private String serverHandshake(Connection connection) {
            while (true) {
                try {
                    connection.send(new Message(MessageType.NAME_REQUEST));
                    Message message = connection.receive();
                    if (message.getType() != MessageType.USER_NAME) {
                        continue;
                    } else if (message.getType() == MessageType.USER_NAME && !message.getData().isEmpty()) {
                        if (!connectionMap.containsKey(message.getData())) {
                            connectionMap.put(message.getData(), connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            ConsoleHelper.writeMessage(message.getData() + " - accepted.");
                            return message.getData();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         *
         * @param connection
         * @param userName
         * @throws IOException
         */
        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> map : connectionMap.entrySet()) {
                if (!map.getKey().equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, map.getKey()));
                }
            }
        }

        /**
         *
         * @param connection
         * @param userName
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, String.format("%s: %s", userName, message.getData())));
                } else {
                    ConsoleHelper.writeMessage(String.format("Message type error: %s, from user: %s",
                            message.getType(), userName));
                }
            }
        }
    }
}
