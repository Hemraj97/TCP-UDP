import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class TCPServer {
  static InputStream read;
  static OutputStream write;
  static Properties properties;

  public static void main(String[] args) {

    System.out.print("Enter a port Number: ");
    Scanner port = new Scanner(System.in);
    int PORT = port.nextInt();
    if ( PORT > 65535) {
      throw new IllegalArgumentException("Invalid input!"
          + "Please provide a valid IP address and Port number and start again.");
    }

    try(ServerSocket serverSocket= new ServerSocket(PORT)){
      String start = getTimeStamp();
      System.out.println(start + " Server started on port: " + PORT);
      Socket clientSocket = serverSocket.accept();
      DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());


      read = new FileInputStream("map.properties");
      properties = new Properties();
      properties.load(read);

      write = new FileOutputStream("map.properties");
      properties.store(write,null);

      while(true){
        String input = dataInputStream.readUTF();
        requestLog(input,String.valueOf(clientSocket.getInetAddress()),String.valueOf(clientSocket.getPort()));

        String res = performOperation(input.split(" "));
        responseLog(res);
        dataOutputStream.writeUTF(res);
        dataOutputStream.flush();
      }


    } catch (Exception e){
      System.out.println(getTimeStamp() + " Error: " + e);
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
  private static void responseLog(String s) { System.out.println(getTimeStamp() + " Response:  " + s + "\n");}


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
   * @return PUT/GET/DELETE operation
   * @throws IllegalArgumentException in case of invalid input
   */
  private static String performOperation(String[] input) throws IllegalArgumentException {
    try{
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
      try {
        String value = properties.getProperty(key);
        String result = value == null ?
            "No value found for key \"" + key + "\"" : "Key: \"" + key + "\" ,Value: \"" + value + "\"";
        return result;
      } catch (Exception e){
        throw new IOException("Error: " + e);
      }
  }
}