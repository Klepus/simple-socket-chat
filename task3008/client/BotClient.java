package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {
    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return String.format("date_bot_%d", (int) (Math.random() * 100));
    }

    public class BotSocketThread extends SocketThread {

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            SimpleDateFormat dateFormat;
            Calendar calendar = Calendar.getInstance();
            if (message.isEmpty() || !message.contains(":")) {
                return;
            }
            String userName = message.split(": ")[0];
            String userCommand = message.replaceFirst(userName + ": ", "");
            if (userCommand.equals("дата")) {
                dateFormat = new SimpleDateFormat("d.MM.YYYY");
            } else if (userCommand.equals("день")) {
                dateFormat = new SimpleDateFormat("d");
            } else if (userCommand.equals("месяц")) {
                dateFormat = new SimpleDateFormat("MMMM");
            } else if (userCommand.equals("год")) {
                dateFormat = new SimpleDateFormat("YYYY");
            } else if (userCommand.equals("время")) {
                dateFormat = new SimpleDateFormat("H:mm:ss");
            } else if (userCommand.equals("час")) {
                dateFormat = new SimpleDateFormat("H");
            } else if (userCommand.equals("минуты")) {
                dateFormat = new SimpleDateFormat("m");
            } else if (userCommand.equals("секунды")) {
                dateFormat = new SimpleDateFormat("s");
            } else {
                dateFormat = null;
            }

            if (dateFormat != null) {
                sendTextMessage(String.format("Информация для %s: %s", userName, dateFormat.format(calendar.getTime())));
            }

        }
    }
}
