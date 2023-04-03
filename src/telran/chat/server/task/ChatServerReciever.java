package telran.chat.server.task;

import telran.chat.model.Message;
import telran.mediation.IBlkQueue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ChatServerReciever implements Runnable {
    Socket socket;
    IBlkQueue<Message> messagesBox;

    public ChatServerReciever(Socket socket, IBlkQueue<Message> messagesBox) {
        this.socket = socket;
        this.messagesBox = messagesBox;
    }

    @Override
    public void run() {
        try (Socket socket = this.socket) {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Message message = (Message) ois.readObject();
                messagesBox.push(message);
            }
        } catch (IOException e) {
            System.out.println("Connection host: " + socket.getInetAddress() + ":" + socket.getPort() + " closed");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
