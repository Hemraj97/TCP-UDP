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

/**
 * Server Class for UDP communication
 */
public class UDPServer {
  static OutputStream writer;
  static InputStream reader;
  static Properties prop;

  /**
   * Drive function for the server
   * @param args command line arguments
   * @throws SocketException exception
   */
  public static void main(String[] args) throws SocketException {
    // accept server PORT from command line, else throw error
    if(args.length != 1 || Integer.parseInt(args[0]) > 65535) {
      throw new IllegalArgumentException("Invalid arguments. " +
          "Please provide just the PORT number and start again");
    }
    int PORT = Integer.parseInt(args[0]);

    try (DatagramSocket ds = new DatagramSocket(PORT)){

      String start = getTimeStamp();
      System.out.println(start + " Server started on port " + PORT);
      byte[] requestBuffer = new byte[512];
      byte[] responseBuffer;

      reader = new FileInputStream("map.properties");
      prop = new Properties();
      prop.load(reader);
      writer = new FileOutputStream("map.properties");
      prop.store(writer, null);

      // keep communication channel open until user keyboard interruption
      while (true) {
        DatagramPacket receivePacket = new DatagramPacket(requestBuffer, requestBuffer.length);
        ds.receive(receivePacket);
        InetAddress client = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        String req = new String(receivePacket.getData());
        requestLog(req, client.toString(), String.valueOf(clientPort));

        try {
          String[] input = req.split(" ");
          String res = performAction(input);
          responseLog(res);
          responseBuffer = res.getBytes();

        } catch (IllegalArgumentException e) {
          String errorMsg = e.getMessage();
          errorLog(errorMsg);
          responseBuffer = errorMsg.getBytes();
        }

        DatagramPacket resPacket = new DatagramPacket(responseBuffer, responseBuffer.length, client, clientPort);
        ds.send(resPacket);
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
    System.out.println(getTimeStamp() + " REQUEST from: " + ip + ":" + port  + " => "+ s);
  }

  /**
   * helper method to print Response messages
   * @param s message string
   */
  private static void responseLog(String s) { System.out.println(getTimeStamp() + " RESPONSE => " + s + "\n");}

  /**
   * helper method to print Error messages
   * @param err error message string
   */
  private static void errorLog(String err) { System.out.println(getTimeStamp() + " ERROR => " + err);}

  /**
   * helper method to return current timestamp
   */
  private static String getTimeStamp() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    return "[Time: " + sdf.format(new Date()) + "]";
  }

  /**
   * Helper method to process user request
   * @param input user request
   * @return result of PUT/GET/DELETE operation
   * @throws IllegalArgumentException in case of invalid input
   */
  private static String performAction(String[] input) throws IllegalArgumentException {
    try {
      String method = input[0].toUpperCase();
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

      switch (method) {
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

  private static String getFromMap(String key) throws IOException {
    String value = prop.getProperty(key);
    String res = value == null ?
        "No value found for key \"" + key + "\"" : "Key: \"" + key + "\" ,Value: \"" + value + "\"";
    return res;
  }

  private static String deleteFromMap(String key) throws IOException {
    String res = "";
    if(prop.containsKey(key)) {
      prop.remove(key);
      prop.store(writer, null);
      res = "Deleted key \"" + key + "\"";
    }
    else {
      res = "Key not found.";
    }
    return res;
  }

  static String addToMap(String key, String value) throws Exception {
    prop.setProperty(key, value);
    prop.store(writer, null);
    String res = "Inserted key \"" + key + "\" with value \"" + value + "\"";
    return res;
  }
}
