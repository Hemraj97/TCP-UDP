package Client;
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * TCPClient is a simple implementation of a TCP client in Java language. It sends requests to a TCP
 * server, receives responses, and handles exceptions.
 */

class TCPClient {

  static String key, value, request;
  static BufferedReader bufferedReader;

  /**
   * The main function/start point of the TCPClient program.
   *
   * @param args command-line arguments where it expects two arguments: Host name and port number
   * @throws Exception an error occurring during execution.
   */
  public static void main(String[] args) throws Exception {

    if (args.length != 2 || Integer.parseInt(args[1]) > 65535) {
      throw new IllegalArgumentException("Invalid arguments. " +
          "Please provide valid IP and PORT number and start again.");
    }

    InetAddress serverIP = InetAddress.getByName(args[0]);
    int serverPort = Integer.parseInt(args[1]);

    try (Socket s = new Socket()) {
      int timeout = 10000;
      s.connect(new InetSocketAddress(serverIP, serverPort), timeout);

      DataInputStream dataInputStream = new DataInputStream(s.getInputStream());
      DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());
      bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      String start = getTimeStamp();
      System.out.println(start + " Client started on port: " + s.getPort());

      while (true) {
        System.out.println("---------------------------------------");
        System.out.print("Operations: \n1. PUT\n2. GET\n3. DELETE\nChoose operation number: ");
        String operation = bufferedReader.readLine().trim();
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
          System.out.println("Please choose a valid operation.");
          continue;
        }

        // Send request packet to the server
        sendPacket(dataOutputStream, request);

        // Receive response packet from the server
        String response = receivePacket(dataInputStream);

        if (response.startsWith("ERROR")) {
          System.out.println("Received error response from the server: " + response);
        } else {
          responseLog(response);
        }
      }
    } catch (UnknownHostException | SocketException e) {
      System.out.println("Host or Port unknown, please provide a valid hostname and port number.");
    } catch (SocketTimeoutException e) {
      System.out.println("Connection timed out. Please check the server availability and try again.");
    } catch (Exception e) {
      System.out.println("Exception occurred!" + e);
    }
  }

  /**
   * Gets the key from the user via the console input.
   *
   * @throws IOException if an error occurs during input reading
   */
  private static void getKey() throws IOException {
    System.out.print("Enter key: ");
    key = bufferedReader.readLine();
  }

  /**
   * Gets the value from the user via the console input.
   *
   * @throws IOException if an error occurs during input reading
   */
  private static void getValue() throws IOException {
    System.out.print("Enter Value: ");
    value = bufferedReader.readLine();
  }

  /**
   * Helper method to send a packet to the server.
   *
   * @param outputStream the output stream to write the packet
   * @param packet       the packet to send
   * @throws IOException if an error occurs during writing
   */
  private static void sendPacket(DataOutputStream outputStream, String packet) throws IOException {
    outputStream.writeUTF(packet);
    outputStream.flush();
    requestLog(packet);
  }

  /**
   * Helper method to receive a packet from the server.
   *
   * @param inputStream the input stream to read the packet
   * @return the received packet
   * @throws IOException if an error occurs during reading
   */
  private static String receivePacket(DataInputStream inputStream) throws IOException {
    return inputStream.readUTF();
  }

  /**
   * Helper method to print Request messages.
   *
   * @param str message string
   */
  private static void requestLog(String str) {
    System.out.println(getTimeStamp() + " Request: " + str);
  }

  /**
   * Helper method to print Response messages.
   *
   * @param str message string
   */
  private static void responseLog(String str) {
    System.out.println(getTimeStamp() + " Response: " + str + "\n");
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
