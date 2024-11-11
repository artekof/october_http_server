package ru.otus.october.http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
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
            System.out.println("Сервер запущен на порту: " + port);
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
                request.info(true);
                dispatcher.execute(request, socket.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
