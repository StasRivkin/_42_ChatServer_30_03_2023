package telran.chat.server.controller;

import telran.chat.model.Message;
import telran.chat.server.task.ChatServerReciever;
import telran.chat.server.task.ChatServerSender;
import telran.mediation.BlkQueue;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerAppl {

    public static void main(String[] args) {
        int port = 9000;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        BlkQueue<Message> messageBox = new BlkQueue<>(10);
        ChatServerSender sender = new ChatServerSender(messageBox);
        Thread senderThread = new Thread(sender);
        senderThread.setDaemon(true);
        senderThread.start();

        try (ServerSocket serverSocket = new ServerSocket(port);) {
            try {
                while (true) {
                    System.out.println("server wait...");
                    Socket socket = serverSocket.accept();
                    System.out.println("Connection established");
                    sender.addClient(socket);
                    ChatServerReciever reciever = new ChatServerReciever(socket, messageBox);
                    executorService.execute(reciever);
                }
            } finally {
                executorService.shutdown();
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
