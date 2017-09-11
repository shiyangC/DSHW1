import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread extends Thread {

    private int port;
    private ServerSocket listener;

    public TCPServerThread(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            listener = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            Socket s = null;
            try {
                s = listener.accept();
                Thread t = new Thread(new Worker(s));
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
