import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Client {
    public static void main (String[] args) {
        String hostAddress;
        int tcpPort;
        int udpPort;

        boolean isTCP = true;
        Socket tcp = null;
        DatagramSocket udp = null;

        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <hostAddress>: the address of the server");
            System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(3) <udpPort>: the port number for UDP connection");
            System.exit(-1);
        }

        hostAddress = args[0];
        tcpPort = Integer.parseInt(args[1]);
        udpPort = Integer.parseInt(args[2]);
        if (isTCP) {
            try {
                tcp = new Socket(hostAddress, tcpPort);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        try {
            while ((userInput = stdIn.readLine()) != null) {
                String cmd = userInput;
                String[] tokens = cmd.split(" ");

                if (tokens[0].equals("setmode")) {
                    // TODO: set the mode of communication for sending commands to the server
                    // and display the name of the protocol that will be used in future
                    if (tokens[1].equals("T")) {
                        isTCP = true;
                    }
                    else isTCP = false;
                    System.out.println("mode" + isTCP);
                }
                else if (tokens[0].equals("purchase")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                } else if (tokens[0].equals("cancel")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                } else if (tokens[0].equals("search")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                } else if (tokens[0].equals("list")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                } else {
                    System.out.println("ERROR: No such command");
                    continue;
                }
                if (tokens[0].equals("setmode")) continue;
                if (isTCP) {
                    PrintWriter printWriter = null;
                    try {
                        printWriter = new PrintWriter(tcp.getOutputStream(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    printWriter.println(cmd);
                    printWriter.flush();
                    BufferedReader in = null;
                    try {
                        in = new BufferedReader( new InputStreamReader(tcp.getInputStream()));
                        //System.out.println("echo: " + in.readLine());
                        String result = in.readLine();
                        if (result != null) {
                            for(String line : result.split("\t")) {
                                System.out.println(line);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        udp = new DatagramSocket();
                        InetAddress ia = InetAddress.getByName(hostAddress);
                        udp.connect(ia, udpPort);
                        byte arr[] = cmd.getBytes();
                        DatagramPacket dpack = new DatagramPacket(arr, arr.length);
                        udp.send(dpack);                                   // send the packet

                        int BUFFERSIZE = 1024;
                        byte[] buf = new byte[BUFFERSIZE];
                        DatagramPacket recv = new DatagramPacket(buf, BUFFERSIZE);
                        udp.receive(recv);
                        String result = new String(recv.getData(), 0, recv.getLength());
                        if (result != null) {
                            for(String line : result.split("\t")) {
                                System.out.println(line);
                            }
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
