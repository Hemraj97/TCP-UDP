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
 * Server Class for UDP communication
 */
public class UDPServer {
  static OutputStream write;
  static InputStream read;
  static Properties properties;

  /**
   * Drive function for the server
   * @param args command line arguments
   * @throws SocketException exception
   */
  public static void main(String[] args) throws SocketException {

    // accept server PORT from command line, else throw error
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

      // keep communication channel open until user keyboard interruption
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

        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, client, clientPort);
        datagramSocket.send(responsePacket);
        requestBuffer = new byte[512];

      }
    } catch (Exception e) {
      errorLog("Something went wrong, please make sure IP and PORT are valid and try again.");
    }
  }

  /**
   * helper method to print Request messages
   * @param s message string
   */
  private static void requestLog(String s, String ip, String port) {
    System.out.println(getTimeStamp() + " Request from: " + ip + ":" + port  + " -> "+ s);
  }

  /**
   * helper method to print Response messages
   * @param s message string
   */
  private static void responseLog(String s) { System.out.println(getTimeStamp() + " Response -> " + s + "\n");}

  /**
   * helper method to print Error messages
   * @param err error message string
   */
  private static void errorLog(String err) { System.out.println(getTimeStamp() + " Error -> " + err);}

  /**
   * helper method to return current timestamp
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
        if(Objects.equals(input[i], "|")) {
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
      return "BAD REQUEST: Invalid operation, view README to check available operations.";
    }

  }

  static String addToMap(String key, String value) throws Exception {
    properties.setProperty(key, value);
    properties.store(write, null);
    String result = "Inserted key \"" + key + "\" with value \"" + value + "\"";
    return result;
  }

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

  private static String getFromMap(String key) throws IOException {
    String value = properties.getProperty(key);
    String result = value == null ?
        "No value found for key \"" + key + "\"" : "Key: \"" + key + "\" ,Value: \"" + value + "\"";
    return result;
  }




}
