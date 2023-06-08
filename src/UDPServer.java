import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

/**
 * UDPServer is a simple implementation of a UDP server in Java.
 *
 * It listens for incoming UDP packets, processes client requests, and sends back responses.
 */
public class UDPServer {
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

    System.out.print("Enter a port Number: ");
    Scanner port = new Scanner(System.in);
    int PORT = port.nextInt();
    if ( PORT > 65535) {
      throw new IllegalArgumentException("Invalid input!"
          + "Please provide a valid IP address and Port number and start again.");
    }

    try (DatagramSocket datagramSocket = new DatagramSocket(PORT)){

      String start = getTimeStamp();
      System.out.println(start + " Server started on port " + PORT);
      byte[] requestBuffer = new byte[512];
      byte[] responseBuffer;

      read = new FileInputStream("map.properties");
      properties = new Properties();
      properties.load(read);
      write = new FileOutputStream("map.properties");
      properties.store(write, null);


      while (true) {
        DatagramPacket receivePacket = new DatagramPacket(requestBuffer, requestBuffer.length);
        datagramSocket.receive(receivePacket);
        InetAddress client = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        String request = new String(receivePacket.getData());
        requestLog(request, client.toString(), String.valueOf(clientPort));

        try {
          String[] input = request.split(" ");
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
      errorLog("Error! , please make sure IP and Port are valid and try again.");
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
      return "BAD REQUEST!:  Please view README to check available operations.";
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
