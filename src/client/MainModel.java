package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Svirinstel on 21.03.17.
 */
public class MainModel {

    private MessageReceiver receiver;

    private Socket socket;
    private DataInputStream inStream;
    private DataOutputStream outStream;

    private Thread inThread;

    private final String SERVER_NAME = "localhost";
    private final int SERVER_SOCKET = 8189;

    public MainModel() {

    }

    public void setReceiver(MessageReceiver receiver) {
        this.receiver = receiver;
    }

    public void connect(){
        try {
            socket = new Socket(SERVER_NAME,SERVER_SOCKET);

            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());

            initInputThread();

            inThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initInputThread(){
        inThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String message = inStream.readUTF();

                        if (message != null) {
                            receiver.addNewChatMessage(message + "\n");
                        }

                        Thread.sleep(100);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMessage(String message){
        try {
            outStream.writeUTF(message);
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean authorize(String userName, String password) {
        String loginMessage = "/auth|" + userName + "|" + password;
        sendMessage(loginMessage);
        return true;
    }

}
