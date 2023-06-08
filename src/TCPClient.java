import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * TCP Client Class
 */
class TCPClient {

//  private static final int TIMEOUT = 5000;
  static String key, value, request;
  static BufferedReader bufferedReader;

  public static void main(String[] args) throws Exception {

    // get Server's IP and PORT from command line, or else throw error
    if (args.length != 2 || Integer.parseInt(args[1]) > 65535) {
      throw new IllegalArgumentException("Invalid arguments. " +
          "Please provide valid IP and PORT number and start again.");
    }

    InetAddress serverIP = InetAddress.getByName(args[0]);
    int serverPort = Integer.parseInt(args[1]);

    try (Socket s = new Socket(serverIP, serverPort)) {
      DataInputStream dataInputStream = new DataInputStream(s.getInputStream());
      DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());
      bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      String start = getTimeStamp();
      System.out.println(start + " Client started on port " + s.getPort());

      // keep communication channel open user keyboard interruption
      while (true) {
        System.out.println("---------------------------------------");
        System.out.print("Operations: \n1. PUT\n2. GET\n3. DELETE\nChoose operation number: ");
        String op = bufferedReader.readLine().trim();
        if (Objects.equals(op, "1")) {
          getKey();
          getValue();
          request = "PUT " + key + " , " + value ;
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

        dataOutputStream.writeUTF(request);
        dataOutputStream.flush();
        requestLog(request);

        String res = dataInputStream.readUTF();
        responseLog(res);
      }
    }
//    catch (SocketTimeoutException e) {
//      // Handle timeout exception
//      System.out.println("Request timed out!");
//    }
    catch (UnknownHostException | SocketException e) {
      System.out.println("Host/Port unknown, please provide valid hostname and port number.");
    }
    catch (Exception e){
      System.out.println("Exception occurred");
    }

  }

  private static void getKey() throws IOException {
    System.out.print("Enter key: ");
    key = bufferedReader.readLine();
  }
  private static void getValue() throws IOException {
    System.out.print("Enter Value: ");
    value = bufferedReader.readLine();
  }

  /**
   * helper method to print Request messages
   * @param s message string
   */
  private static void requestLog(String s) {
    System.out.println(getTimeStamp() + " Request: " + s);
  }

  /**
   * helper method to print Response messages
   * @param s message string
   */
  private static void responseLog(String s) {
    System.out.println(getTimeStamp() + " Response: " + s + "\n");
  }

  /**
   * helper method to return current timestamp
   */
  private static String getTimeStamp() {
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");
    return "[Time: " + sdf.format(new Date()) + "]";
  }
}