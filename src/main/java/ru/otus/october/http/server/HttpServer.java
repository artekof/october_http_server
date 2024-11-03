package ru.otus.october.http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private int port;
    private Dispatcher dispatcher;
    private boolean isActive;

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
        this.isActive = true;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (isActive) {
                Socket socket = serverSocket.accept();
                new Thread(() -> dispatcherHandler(socket)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
