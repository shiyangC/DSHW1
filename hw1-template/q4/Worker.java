import com.sun.tools.corba.se.idl.constExpr.Or;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class Worker extends Thread{
    public static Map<String, Integer> productMap = new HashMap<>();
    public static Map<String, Set<Order>> customerOrder = new HashMap<>();
    public static Map<Integer, Order> orderMap = new HashMap<>();
    public static int uid = 1;
    private Socket socket;
    public Worker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
//        System.out.println("worker");

        PrintWriter out = null;
        BufferedReader in = null;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputLine;
        try {
            while ((inputLine = in.readLine()) != null) {
                System.out.println("cmd" + inputLine);

                String result = runSync(inputLine);
                if (result != null) {
                    try {
                        out = new PrintWriter(socket.getOutputStream(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.println(result);
                    out.flush();
                    System.out.println(result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String runSync(String cmd) {
        System.out.println("runsync");
        String [] tokens = cmd.split(" ");
        String result = "result";
        switch (tokens[0]) {
            case "purchase":
                result = handlePurchase(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                break;
            case "cancel":
                result = handleCancel(Integer.parseInt(tokens[1]));
                break;
            case "search":
                result = handleSearch(tokens[1]);
                break;
            case "list":
                result = handleList();
                break;
            default:
                break;
        }
        return result;
    }

    private String handleCancel(int id) {
        if (!orderMap.containsKey(id)) {
            return id + " not found, no such order";
        }
        Order order = orderMap.get(id);

        customerOrder.get(order.username).remove(order);

        orderMap.remove(id);

        int inventoryCount = productMap.get(order.productName);
        inventoryCount += order.n;
        productMap.put(order.productName, inventoryCount);
        return "Order " + order.orderId + " is canceled";
    }

    private String handleSearch(String username) {
        if (!customerOrder.containsKey(username))
            return "No order found for " + username;
        List<Order> list = new ArrayList<>();
        for (Order order : customerOrder.get(username)) {
            list.add(order);
        }
        Collections.sort(list);
        StringBuffer sb = new StringBuffer();
        for (Order key : list) {
            sb.append(key).append("\t");
        }
        return sb.toString();
    }

    private String handleList() {
        List<String> list = new ArrayList<>();
        for (String key : Worker.productMap.keySet()) {
            list.add(key + " " + productMap.get(key));
        }
        Collections.sort(list);

        StringBuffer sb = new StringBuffer();
        for (String key : list) {
            sb.append(key).append("\t");
        }
        System.out.println(sb);
        return sb.toString();
    }

    private String handlePurchase(String username, String product, int n) {
        if (!productMap.containsKey(product)) {
            return "Not Available We do not sell this product";
        }
        int inventoryCount = productMap.get(product);
        if (inventoryCount < n) {
            return "Not Available - Not enough items";
        }
        productMap.put(product, inventoryCount - n);
        Order order = new Order(username, product, n);
        Set<Order> orderSet = customerOrder.getOrDefault(username, new HashSet<Order>());
        orderSet.add(order);

        orderMap.put(order.orderId, order);

        customerOrder.put(username, orderSet);
        return "‘You order has been placed, " + order.orderId + " " + username + " " + product + " " + n;
    }

}

class Order implements Comparable<Order>{
    public int orderId;
    public String username;
    public String productName;
    public int n;

    public Order(String username, String productName, int n) {
        this.username = username;
        this.productName = productName;
        this.n = n;
        this.orderId = ++Worker.uid;
    }

    @Override
    public int compareTo(Order o) {
        return this.orderId - o.orderId;
    }

    @Override
    public String toString() {
        //<order-id>, <product-name>, <quantity>
        return this.orderId + " " + this.productName + " " + this.n;
    }
}
