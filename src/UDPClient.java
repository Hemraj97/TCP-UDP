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
 * UDPClient is a simple implementation of a UDP client in Java.
 * <p>
 * It sends requests to a UDP server, receives responses, and handles exceptions.
 */
public class UDPClient {

  static String key, value, request;
  static Scanner scanner;

  /**
   * The main start point of the UDPClient program.
   *
   * @param args command line arguments containing server IP and port.
   * @throws IOException if an I/O error occurs.
   */
  public static void main(String[] args) throws IOException {
    if (args.length != 2 || Integer.parseInt(args[1]) > 65535) {
      throw new IllegalArgumentException("Invalid argument! " +
          "Please provide valid IP and Port number and start again");
    }
    InetAddress serverIP = InetAddress.getByName(args[0]);
    int serverPort = Integer.parseInt(args[1]);
    scanner = new Scanner(System.in);

    try (DatagramSocket datagramSocket = new DatagramSocket()) {
      datagramSocket.setSoTimeout(10000);
      String start = getTimeStamp();
      System.out.println(start + " Client started");

      while (true) {
        System.out.println("---------------------------------------");
        System.out.print("Operations: \n1. PUT\n2. GET\n3. DELETE\nChoose operation number: ");
        String operation = scanner.nextLine().trim();
        if (Objects.equals(operation, "1")) {
          getKey();
          getValue();
          request = "PUT " + key + " , " + value;
        } else if (Objects.equals(operation, "2")) {
          getKey();
          request = "GET " + key;
        } else if (Objects.equals(operation, "3")) {
          getKey();
          request = "DELETE " + key;
        } else {
          System.out.println("Please choose a valid operation!");
          continue;
        }

        requestLog(request);

        byte[] requestBuffer = request.getBytes();
        if (requestBuffer.length > 65507) {
          System.out.println("Error: Request size exceeds the maximum allowed limit.");
          continue;
        }

        DatagramPacket requestPacket = new DatagramPacket(requestBuffer, requestBuffer.length,
            serverIP, serverPort);
        datagramSocket.send(requestPacket);

        byte[] resultBuffer = new byte[512];
        DatagramPacket resultPacket = new DatagramPacket(resultBuffer, resultBuffer.length);

        try {
          datagramSocket.receive(resultPacket);
          String result = new String(resultBuffer);
          responseLog(result);
        } catch (java.net.SocketTimeoutException e) {
          System.out.println("Timeout occurred. " +
              "The server did not respond within the specified time.");
        }
      }
    } catch (UnknownHostException | SocketException e) {
      System.out.println(
          "Host or Port unknown error, try again!");
    }
  }

  /**
   * Gets the key from the user via the console input.
   *
   * @throws IOException if an error occurs during input reading
   */
  private static void getKey() throws IOException {
    System.out.print("Enter key: ");
    key = scanner.nextLine();
  }

  /**
   * Gets the value from the user via the console input.
   *
   * @throws IOException if an error occurs during input reading
   */
  private static void getValue() throws IOException {
    System.out.print("Enter Value: ");
    value = scanner.nextLine();
  }

  /**
   * Helper method to print Request messages.
   *
   * @param str message string
   */
  private static void requestLog(String str) {
    System.out.println(getTimeStamp() +
        " Request -> " + str);
  }

  /**
   * Helper method to print Response messages.
   *
   * @param str message string
   */
  private static void responseLog(String str) {
    System.out.println(getTimeStamp() +
        " Response -> " + str + "\n");
  }

  /**
   * Helper method to return the current timestamp.
   *
   * @return the current timestamp
   */
  private static String getTimeStamp() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");
    return "[Time: " + simpleDateFormat.format(new Date()) + "]";
  }
}
