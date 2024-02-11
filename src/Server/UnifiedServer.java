package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

/**
 * UDPServer is a simple implementation of a UDP server in Java.
 *
 * It listens for incoming UDP packets, processes client requests, and sends back responses.
 */
public class UnifiedServer {
  static InputStream read;
  static OutputStream write;
  static Properties properties;

  /**
   * The main start point of the UDPServer program.
   *
   * @param args command line arguments
   * @throws SocketException exception
   */
  public static void main(String[] args) throws Exception {

    // System.out.print("Enter a port Number: ");
    int tcpPort = Integer.parseInt(args[0]);
    int udpPort = Integer.parseInt(args[1]);
    if ( udpPort > 65535 || tcpPort > 65535) {
      throw new IllegalArgumentException("Invalid input!"
          + "Please provide a valid IP address and Port number.");
    }
    


    try {
      // Start TCP server thread
      Thread tcpThread = new Thread(() -> {
          TCPServer(tcpPort);
      });
      tcpThread.start();

      // Start UDP server thread
      Thread udpThread = new Thread(() -> {
          UDPServer(udpPort);
      });
      udpThread.start();

  } catch (NumberFormatException e) {
      e.printStackTrace();
  }
    // UDPServer(udpPort);
    // TCPServer(tcpPort);



  }

  static private void UDPServer(int PORT){
    try (DatagramSocket datagramSocket = new DatagramSocket(PORT)){

      String start = getTimeStamp();
      System.out.println(start + " Server started on UDP port: " + PORT);
      byte[] requestBuffer = new byte[512];
      byte[] responseBuffer;
      read = new FileInputStream("Server/map.properties");
      properties = new Properties();
      properties.load(read);
      write = new FileOutputStream("Server/map.properties");
      properties.store(write, null);


      while (true) {
        DatagramPacket receivePacket = new DatagramPacket(requestBuffer, requestBuffer.length);
        datagramSocket.receive(receivePacket);
        InetAddress client = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        String request = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
        requestLog(request, client.toString(), String.valueOf(clientPort));

        // Validate packet size
        if (receivePacket.getLength() > 512) {
          errorLog("Received packet exceeds maximum allowed size.");
          continue;
        }

        try {
          String[] input = request.split(" ");
          if (input.length < 2) {
            throw new IllegalArgumentException("Malformed request!");
          }
          String result = performOperation(input);
          responseLog(result);
          responseBuffer = result.getBytes();

        } catch (IllegalArgumentException e) {
          String errorMessage = e.getMessage();
          errorLog(errorMessage);
          responseBuffer = errorMessage.getBytes();
        }

        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length,
            client, clientPort);
        datagramSocket.send(responsePacket);
        requestBuffer = new byte[512];

      }
    } catch (Exception e) {
      errorLog("Error! Please make sure IP and Port are valid and try again.");
    }
  }


  static private void TCPServer(int TCPPORT){
    try (ServerSocket serverSocket = new ServerSocket(TCPPORT)) {
      String start = getTimeStamp();
      System.out.println(start + " TCP Server started on port: " + TCPPORT);
      Socket clientSocket = serverSocket.accept();
      DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
      read = new FileInputStream("Server/map.properties");
      properties = new Properties();
      properties.load(read);
      write = new FileOutputStream("Server/map.properties");
      properties.store(write, null);
      

      while (true) {
        String input = dataInputStream.readUTF();
        requestLog(input, String.valueOf(clientSocket.getInetAddress()),
            String.valueOf(clientSocket.getPort()));

        String result = performOperation(input.split(" "));
        responseLog(result);
        dataOutputStream.writeUTF(result);
        dataOutputStream.flush();
      }


    } catch (Exception e) {
      System.out.println(getTimeStamp() + " Error: " + e);
    }
  }

  /**
   * Helper method to print Request messages.
   *
   * @param str    message string
   * @param ip   client IP address
   * @param port client port number
   */
  private static void requestLog(String str, String ip, String port) {
    System.out.println(getTimeStamp() + " Request from: " + ip + ":" + port  + " -> "+ str);
  }

  /**
   * Helper method to print Response messages.
   *
   * @param str message string
   */
  private static void responseLog(String str) { System.out.println(getTimeStamp() +
      " Response -> " + str + "\n");}

  /**
   * Helper method to print Error messages.
   *
   * @param err error message string
   */
  private static void errorLog(String err) { System.out.println(getTimeStamp() +
      " Error -> " + err);}

  /**
   * Helper method to return the current timestamp.
   *
   * @return the current timestamp
   */
  private static String getTimeStamp() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");
    return "<Time: " + simpleDateFormat.format(new Date()) + ">";
  }

  /**
   * Helper method to process user request
   * @param input user request
   * @return result of PUT/GET/DELETE operation
   * @throws IllegalArgumentException in case of invalid input
   */
  private static String performOperation(String[] input) throws IllegalArgumentException {
    try {
      System.out.println(Arrays.toString(input));
      System.out.println(input[0]);
      String operation = input[0].toUpperCase();
      String key = "";
      int j = 0;
      for(int i = 1; i < input.length; i++) {
        if(Objects.equals(input[i], ",")) {
          j = i;
          break;
        }
        else key = key + input[i] + " ";
      }
      key = key.trim();

      switch (operation) {
        case "PUT": {
          String value = "";
          for(int i = j+1; i < input.length; i++) value = value + " " + input[i].trim();
          value = value.trim();
          return addToMap(key, value);
        }
        case "DELETE": {
          return deleteFromMap(key);
        }
        case "GET": {
          return getFromMap(key);
        }
        default:
          throw new IllegalArgumentException();
      }
    } catch (Exception e) {
      return "BAD REQUEST!:  Please view README.md to check available operations." + e;
    }

  }

  /**
   * Add the key-value pair to the map.
   *
   * @param key   the key
   * @param value the value
   * @return a message indicating the success of the operation
   * @throws Exception if there is an error adding to the map
   */
  static String addToMap(String key, String value) throws Exception {
    properties.setProperty(key, value);
    properties.store(write, null);
    String result = "Inserted key \"" + key + "\" with value \"" + value + "\"";
    return result;
  }

  /**
   * Delete the key from the map.
   *
   * @param key the key to delete
   * @return a message indicating the success of the operation
   * @throws IOException if there is an error deleting from the map
   */
  private static String deleteFromMap(String key) throws IOException {
    String result = "";
    if(properties.containsKey(key)) {
      properties.remove(key);
      properties.store(write, null);
      result = "Deleted key \"" + key + "\"" + " successfully!";
    }
    else {
      result = "Key not found.";
    }
    return result;
  }

  /**
   * Get the value associated with the key from the map.
   *
   * @param key the key to retrieve the value for
   * @return the value associated with the key or a message indicating that the key was not found
   * @throws IOException if there is an error retrieving from the map
   */
  private static String getFromMap(String key) throws IOException {
    String value = properties.getProperty(key);
    String result = value == null ?
        "No value found for key \"" + key + "\"" : "Key: \"" + key + "\" ,Value: \"" + value + "\"";
    return result;
  }

}