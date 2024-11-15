package ru.otus.october.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger LOGGER = LogManager.getLogger(HttpRequest.class.getName());
    private String rawRequest;
    private HttpMethod method;
    private String uri;
    private String webProtocol;
    private Map<String, String> parameters;
    private Map<String, String> headers;
    private String body;
    private Exception exception;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getUri() {
        return uri;
    }

    public String getRoutingKey(){
        return method + " " + uri;
    }
    public HttpMethod getMethod(){return method;}
    public String getBody() {
        return body;
    }

    public HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
        this.parameters = new HashMap<>();
        this.headers = new HashMap<>();
        this.parse();
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public boolean containsParameter(String key) {
        return parameters.containsKey(key);
    }

    private void parse() {
        String[] headersAndRequestSplit = rawRequest.split("\r\n");
        String requestLine = headersAndRequestSplit[0];
        String[] requestLineSplit = requestLine.split(" ");
        method = HttpMethod.valueOf(requestLineSplit[0]);
        uri = requestLineSplit[1];
        webProtocol = requestLineSplit[2];
        for (int i = 1; i < headersAndRequestSplit.length; i++) {
            String[] headersSplit = headersAndRequestSplit[i].split(": ");
            headers.put(headersSplit[0],headersSplit[1]);
        }
        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            uri = elements[0];
            String[] keysValues = elements[1].split("[&]");
            for (String o : keysValues) {
                String[] keyValue = o.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
        if (method == HttpMethod.POST) {
            this.body = rawRequest.substring(rawRequest.indexOf("\r\n\r\n") + 4);
        }
    }

    public void info() {
        LOGGER.debug(rawRequest);
        LOGGER.info("Web protocol: " + webProtocol);
        LOGGER.info("Method: " + method);
        LOGGER.info("URI: " + uri);
        LOGGER.info("Parameters: " + parameters);
        LOGGER.info("Body" + body);
        LOGGER.info("Headers" + headers);
    }


}
