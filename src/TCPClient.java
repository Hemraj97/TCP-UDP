import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * TCP Client Clas
 */
class TCPClient {
  static String key, value, request;
  static BufferedReader br;

  public static void main(String[] args) throws Exception {

    // get Server's IP and PORT from command line, or else throw error
    if (args.length != 2 || Integer.parseInt(args[1]) > 65535) {
      throw new IllegalArgumentException("Invalid arguments. " +
          "Please provide valid IP and PORT number and start again.");
    }

    InetAddress serverIP = InetAddress.getByName(args[0]);
    int serverPort = Integer.parseInt(args[1]);

    try (Socket s = new Socket(serverIP, serverPort)) {
      DataInputStream din = new DataInputStream(s.getInputStream());
      DataOutputStream dout = new DataOutputStream(s.getOutputStream());
      br = new BufferedReader(new InputStreamReader(System.in));
      String start = getTimeStamp();
      System.out.println(start + " Client started on port " + s.getPort());

      // keep communication channel open user keyboard interruption
      while (true) {
        System.out.print("Operation List: \n1. Put\n2. Get\n3. Delete\nChoose operation: ");
        String op = br.readLine().trim();
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

        dout.writeUTF(request);
        dout.flush();
        requestLog(request);

        String res = din.readUTF();
        responseLog(res);
      }
    } catch (UnknownHostException | SocketException e) {
      System.out.println("Host/Port unknown, please provide valid hostname and port number.");
    }

  }

  private static void getKey() throws IOException {
    System.out.print("Enter key: ");
    key = br.readLine();
  }
  private static void getValue() throws IOException {
    System.out.print("Enter Value: ");
    value = br.readLine();
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
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    return "[Time: " + sdf.format(new Date()) + "]";
  }
}