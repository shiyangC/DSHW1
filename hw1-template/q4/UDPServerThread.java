import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServerThread extends Thread {
    private int port;
    private DatagramSocket dsock;

    public UDPServerThread(int port) {
        this.port = port;
    }
    @Override
    public void run() {
        try {
            dsock = new DatagramSocket(this.port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                int BUFFERSIZE = 1024;
                byte[] buf = new byte[BUFFERSIZE];
                DatagramPacket datagramPacket = new DatagramPacket(buf, BUFFERSIZE);
                dsock.receive(datagramPacket);
                Thread t = new Thread(new UPDWorker(datagramPacket, dsock));
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
