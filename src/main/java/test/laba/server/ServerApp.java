package test.laba.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import rawhttp.core.EagerHttpRequest;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpResponse;
import test.laba.server.commands.HttpHandler;
import test.laba.server.util.AppConfig;

@Log4j2
@RequiredArgsConstructor
public class ServerApp {
    private static final int PORT = Integer.parseInt(AppConfig.getProperty("app.port"));
    private final HttpHandler commandsManager;
    private final ExecutorService responseReaderPool;
    private final ExecutorService responseExecutorPool;
    private final ExecutorService responseSenderPool;


    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.info("Server started on port {}", PORT);
            interactivelyModule(serverSocket);
        } catch (IOException e) {
            log.error("Failed to start the server: {}", e.getMessage());
        }
    }


    private void interactivelyModule(ServerSocket serverSocket) {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                if (clientSocket != null) {
                    responseReaderPool.execute(new Client(clientSocket));
                }
            }
        } catch (IOException e) {
            log.error("Error accepting client connection: {}", e.getMessage());
        } finally {
            shutdownPools();
        }
    }

    private void shutdownPools() {
        responseReaderPool.shutdown();
        responseExecutorPool.shutdown();
        responseSenderPool.shutdown();
        log.info("All thread pools shut down");
    }


    private class Client implements Runnable {

        private final Socket socket;
        private final RawHttp http = new RawHttp();

        Client(Socket socket) {
            this.socket = socket;
        }


        @Override
        public void run() {
            log.info("the new client was connected and start execute{}", socket);
            try {
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                while (!Thread.currentThread()
                              .isInterrupted() && !socket.isClosed()) {
                    if (inputStream.available() != 0) {
                        executeCommand(http.parseRequest(inputStream)
                                           .eagerly(), outputStream);
                    }
                }
            } catch (IOException e) {
                log.warn("impossible to connect with {} because of {} the channel is closing", socket.getLocalSocketAddress(), e.getMessage());
            } finally {
                close();
            }

        }

        public void executeCommand(EagerHttpRequest eagerly, OutputStream outputStream) {
            responseExecutorPool.submit(() -> {
                RawHttpResponse rawHttpResponse = commandsManager.executeCommand(eagerly);
                responseSenderPool.submit(() -> {
                    try {
                        rawHttpResponse.writeTo(outputStream);
                    } catch (IOException ex) {
                        log.error("Error sending response: {}", ex.getMessage());
                    }
                });

            });
        }

        private void close() {
            try {
                log.info("the client was disconnected + {}", socket);
                socket.close();
            } catch (IOException e) {
                log.warn("impossible to close channel because of {}", e.getMessage());
            }
        }
    }

}

