package ru.otus.october.http.server;

public class Application {
    /**Домашнее задание:
    Реализуйте возможность обработки более одного потока за запуск сервера
    Реализуйте обработку запросов в отдельных потоках
    **/
    public static void main(String[] args) {
        new HttpServer(8189).start();
    }
}
