import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Server {

    public static void main (String[] args) {
        int tcpPort;
        int udpPort;
        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(2) <udpPort>: the port number for UDP connection");
            System.out.println("\t(3) <file>: the file of inventory");

            System.exit(-1);
        }
        tcpPort = Integer.parseInt(args[0]);
        udpPort = Integer.parseInt(args[1]);
        String fileName = args[2];

        // parse the inventory file
        File inputFile = new File(fileName);

        try {
            Scanner scanner = new Scanner(inputFile);
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
                String[] tokens = line.split(" ");
                if (tokens.length < 2) break;
                Worker.productMap.put(tokens[0], Integer.parseInt(tokens[1]));
            }
            Thread tcpThread = new Thread(new TCPServerThread(tcpPort));
            tcpThread.start();
            Thread udpThread = new Thread(new UDPServerThread(udpPort));
            udpThread.start();
            try {
                tcpThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // TODO: handle request from clients
    }
}
