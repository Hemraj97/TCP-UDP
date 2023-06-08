
# TCP Server and Client

The TCP Server and Client are two Java programs that enable communication between a server and a client using the TCP/IP protocol. The TCP Server listens for client connections on a specified port, while the TCP Client connects to the server's IP address and port. The server and client exchange data through TCP sockets, allowing for reliable and ordered transmission of messages.

## TCPServer

The TCPServer program represents the server-side of the TCP communication. It creates a server socket and listens for incoming client connections on a specified port. Once a client connects, the server can send and receive messages from the client.

### Usage

To use the TCPServer, follow these steps:

1. Compile the TCPServer.java file using the Java compiler:

   ```
   javac TCPServer.java
   ```

2. Run the compiled TCPServer class, providing the desired port number as a command-line argument:

   ```
   java TCPServer 
   ```

3. Add the `<port>` number on which you want the server to listen for incoming connections.

4. The server will start and display a message indicating the IP address and port it is listening on. It will then wait for client connections.


## TCPClient

The TCPClient program represents the client-side of the TCP communication. It connects to a TCP server using the server's IP address and port number. Once connected, the client can send messages to the server and receive responses.

### Usage

To use the TCPClient, follow these steps:

1. Compile the TCPClient.java file using the Java compiler:

   ```
   javac TCPClient.java
   ```

2. Run the compiled TCPClient class, providing the server's IP address and port number as command-line arguments:

   ```
   java TCPClient <server-ip> <server-port>
   ```

   Replace `<server-ip>` with the IP address (In our case `localhost`) of the server you want to connect to, and `<server-port>` with the corresponding port number.

3. The client will attempt to establish a connection with the server. If successful, it will display a message indicating the connection status.

4. Once connected, you can enter messages to send to the server. The client will display the responses received from the server.

5. To terminate the client program, simply close the client window or use the appropriate termination command.

-------

# UDP Server and Client

The UDP Server and Client are two Java programs that enable communication between a server and a client using the UDP protocol. The UDP Server listens for client connections on a specified port, while the UDP Client connects to the server's IP address and port. The server and client exchange data through UDP Datagram sockets, allowing for faster transmission of messages.

## UDPServer

The UDPServer program represents the server-side of the UDP communication. It creates a datagram socket and listens for incoming client connections on a specified port. Once a client connects, the server can send and receive messages from the client.

### Usage

To use the UDPServer, follow these steps:

1. Compile the UDPServer.java file using the Java compiler:

   ```
   javac UDPServer.java
   ```

2. Run the compiled UDPServer class, providing the desired port number as a command-line argument:

   ```
   java UDPServer 
   ```

3. Add the `<port>` number on which you want the server to listen for incoming connections.

4. The server will start and display a message indicating the IP address and port it is listening on. It will then wait for client connections.


## UDPClient

The UDPClient program represents the client-side of the UDP communication. It connects to a UDP server using the server's IP address and port number. Once connected, the client can send messages to the server and receive responses.

### Usage

To use the UDPClient, follow these steps:

1. Compile the UDPClient.java file using the Java compiler:

   ```
   javac UDPClient.java
   ```

2. Run the compiled UDPClient class, providing the server's IP address and port number as command-line arguments:

   ```
   java UDPClient <server-ip> <server-port>
   ```

   Replace `<server-ip>` with the IP address (In our case `localhost`) of the server you want to connect to, and `<server-port>` with the corresponding port number.

3. The client will attempt to establish a connection with the server. If successful, it will display a message indicating the connection status.

4. Once connected, you can enter messages to send to the server. The client will display the responses received from the server.

5. To terminate the client program, simply close the client window or use the appropriate termination command.


