import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UPDWorker extends Worker{
    private DatagramPacket datagramPacket;
    private DatagramSocket datagramSocket;
    public UPDWorker(DatagramPacket datagramPacket, DatagramSocket datagramSocket) {
        super(null);
        this.datagramPacket = datagramPacket;
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        try {
            byte buff[] = datagramPacket.getData();
            int packSize = datagramPacket.getLength();
            String cmd = new String(buff, 0, packSize);
            String result = runSync(cmd);
            System.out.println("udp:" + result);

            byte arr[] = result.getBytes();
            DatagramPacket dpack = new DatagramPacket(arr, arr.length, datagramPacket.getAddress(), datagramPacket.getPort());
            datagramSocket.send(dpack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
