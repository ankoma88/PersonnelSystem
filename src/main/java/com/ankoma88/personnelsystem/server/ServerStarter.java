package com.ankoma88.personnelsystem.server;

import com.ankoma88.personnelsystem.model.Message;
import com.ankoma88.personnelsystem.server.service.interfaces.Processor;
import com.ankoma88.personnelsystem.server.service.impl.ProcessorImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static com.ankoma88.personnelsystem.util.Settings.PORT;

public class ServerStarter extends Thread {

    private static final Logger log = Logger.getLogger(ServerStarter.class.getName());
    private static final Processor processor = new ProcessorImpl();

    public static class ClientHandler implements Runnable {

        private final Socket clientSock;

        public ClientHandler(final Socket clientSocket) {
            this.clientSock = clientSocket;
        }

        /**
         * Method that actually works with client requests (via Processor)
         */
        @Override
        public void run() {

            ObjectInputStream userInput = null;
            ObjectOutputStream userOutput = null;

            try {
                userInput = new ObjectInputStream(this.clientSock.getInputStream());
                userOutput = new ObjectOutputStream(this.clientSock.getOutputStream());
                while (true) {
                    Message input = (Message) userInput.readObject();
                    log.info("Received message on server: " + input);
                    Message output = processor.processMessage(input);
                    userOutput.writeObject(output);
                    userOutput.flush();
                }
            } catch (IOException ioe) {
                log.info("IOException...");
            } catch (ClassNotFoundException e) {
                log.info("Problem on reading message from client");
            }
            try {
                if (userInput != null) {
                    userInput.close();
                }
                if (userOutput != null) {
                    userOutput.close();
                }
                this.clientSock.close();
                System.err.println("Lost connection to " + this.clientSock.getRemoteSocketAddress());
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }

    }


    public static void main(String[] args) {

        int port = PORT;
        if (port <= 0 || port >= 65537) {
            System.err.println("Port value must be in (0, 65535]");
            System.exit(1);
        }

        final ServerStarter server = new ServerStarter(port);

        //Starting server
        server.start();

        try {
            // Wait for the server to shutdown
            server.join();
            log.info("Completed shutdown.");
        } catch (InterruptedException e) {
            System.err.println("Interrupted before accept thread completed.");
            System.exit(1);
        }
    }

    /**
     * Pool of threads of undefined size. A new thread will be created
     * for each concurrent connection, and old threads will be shut down if they stay idle.
     */
    private final ExecutorService workers = Executors.newCachedThreadPool();

    /**
     * Server socket for accepting incoming client connections.
     */
    private ServerSocket listenSocket;

    private volatile boolean keepRunning = true;

    public ServerStarter(final int port) {

        // Capture shutdown requests from virtual machine.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ServerStarter.this.shutdown();
            }
        });

        try {
            this.listenSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Exception occurred while creating the listen socket: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * This is executed when ServerStarter.start() is invoked by another thread.  Will listen for incoming connections
     * and hand them over to the ExecutorService (thread pool) for the actual handling of client requests.
     */
    @Override
    public void run() {
        // Set a timeout on the accept so we can catch shutdown requests
        try {
            this.listenSocket.setSoTimeout(1000);
        } catch (SocketException e1) {
            System.err.println("Unable to set acceptor timeout value.  The server may not shutdown gracefully.");
        }

        log.info("Accepting incoming connections on port " + this.listenSocket.getLocalPort());

        // Accept an incoming connection, handle it, then close and repeat.
        while (this.keepRunning) {
            try {
                // Accept the next incoming connection
                final Socket clientSocket = this.listenSocket.accept();
                log.info("Accepted connection from " + clientSocket.getRemoteSocketAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                this.workers.execute(handler);

            } catch (SocketTimeoutException te) {
                // Ignored, timeouts will happen every 1 second
            } catch (IOException ioe) {
                System.err.println("Exception occurred while handling client request: " + ioe.getMessage());
                // Yield to other threads if an exceptions occurs
                Thread.yield();
            }
        }
        try {
            // Make sure to release the port, otherwise it may remain bound for several minutes
            this.listenSocket.close();
        } catch (IOException ioe) {
            // Ignored
        }
        log.info("Stopped accepting incoming connections.");
    }

    /**
     * Shuts down this server.  Since the main server thread will time out every 1 second,
     * the shutdown process should complete in at most 1 second from the time this method is invoked.
     */
    public void shutdown() {
        log.info("Shutting down the server.");
        this.keepRunning = false;
        this.workers.shutdownNow();
        try {
            this.join();
        } catch (InterruptedException e) {
            // Ignored
        }
    }


}