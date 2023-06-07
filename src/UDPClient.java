import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

/**
 * UDP Client Class
 */
public class UDPClient  {
  static String key, value, request;
  static Scanner sc;

  /**
   * Driver function for the client
   * @param args command line arguments
   * @throws IOException exception
   */
  public static void main(String[] args) throws IOException {
    // get Server's IP and PORT from command line, or else throw error
    if(args.length != 2 || Integer.parseInt(args[1]) > 65535) {
      throw new IllegalArgumentException("Invalid arguments. " +
          "Please provide valid IP and PORT number and start again");
    }
    InetAddress serverIP = InetAddress.getByName(args[0]);
    int serverPort = Integer.parseInt(args[1]);
    sc = new Scanner(System.in);

    try (DatagramSocket ds = new DatagramSocket()) {
      String start = getTimeStamp();
      System.out.println(start + " Client started");

      byte[] reqBuffer;
      byte[] resBuffer;

      // keep communication channel open user keyboard interruption
      while (true) {
        System.out.print("Operation List: \n1. Put\n2. Get\n3. Delete\nChoose operation: ");
        String op = sc.nextLine().trim();
        if (Objects.equals(op, "1")) {
          getKey();
          getValue();
          request = "PUT " + key + " | " + value;
        } else if (Objects.equals(op, "2")) {
          getKey();
          request = "GET " + key;
        } else if (Objects.equals(op, "3")) {
          getKey();
          request = "DELETE " + key;
        } else {
          System.out.println("Please choose a valid operation.");
          continue;
        }

        requestLog(request);

        reqBuffer = request.getBytes();
        DatagramPacket reqPacket = new DatagramPacket(reqBuffer, reqBuffer.length, serverIP, serverPort);
        ds.send(reqPacket);

        resBuffer = new byte[512];
        DatagramPacket resPacket = new DatagramPacket(resBuffer, resBuffer.length);
        ds.receive(resPacket);
        String res = new String(resBuffer);
        responseLog(res);
      }
    } catch (UnknownHostException | SocketException e) {
      System.out.println("Host/Port unknown, please provide valid hostname and port number.");
    }
  }

  private static void getKey() {
    System.out.print("Enter key: ");
    key = sc.nextLine();
  }
  private static void getValue() {
    System.out.print("Enter Value: ");
    value = sc.nextLine();
  }

  /**
   * helper method to print Request messages
   * @param s message string
   */
  private static void requestLog(String s) { System.out.println(getTimeStamp() + " REQUEST => " + s);}

  /**
   * helper method to print Response messages
   * @param s message string
   */
  private static void responseLog(String s) { System.out.println(getTimeStamp() + " RESPONSE => " + s + "\n");}

  /**
   * helper method to return current timestamp
   */
  private static String getTimeStamp() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    return "[Time: " + sdf.format(new Date()) + "]";
  }

}