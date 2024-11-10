package ru.otus.october.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

public class HttpServer {
    private static final Logger LOGGER = LogManager.getLogger(HttpServer.class.getName());
    private int port;
    private Dispatcher dispatcher;

    private ExecutorService executorService;
    private boolean isActive;

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
        this.executorService = Executors.newCachedThreadPool();
        this.isActive = true;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Сервер запущен на порту: " + port);
            while (isActive) {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> dispatcherHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        executorService.shutdown();
    }

    private void dispatcherHandler(Socket socket) {
        try (socket){
            byte[] buffer = new byte[8192];
            int n = socket.getInputStream().read(buffer);
            if(n > 0) {
                String rawRequest = new String(buffer, 0, n);
                HttpRequest request = new HttpRequest(rawRequest);
                request.info();
                dispatcher.execute(request, socket.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
