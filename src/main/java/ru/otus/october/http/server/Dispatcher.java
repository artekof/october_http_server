package ru.otus.october.http.server;

import ru.otus.october.http.server.app.ItemsRepository;
import ru.otus.october.http.server.processors.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class Dispatcher {
    private Map<String, RequestProcessor> processors;
    private RequestProcessor defaultNotFoundProcessor;
    private RequestProcessor defaultInternalServerErrorProcessor;
    private RequestProcessor defaultBadRequestProcessor;
    private RequestProcessor defaultMethodNotAllowedProcessor;
    private ItemsRepository itemsRepository;

    public Dispatcher() {
        this.itemsRepository = new ItemsRepository();
        this.processors = new HashMap<>();
        this.processors.put("GET /", new HelloWorldProcessor());
        this.processors.put("GET /calculator", new CalculatorProcessor());
        this.processors.put("GET /items", new GetAllItemsProcessor(itemsRepository));
        this.processors.put("POST /items", new CreateNewItemsProcessor(itemsRepository));
        this.defaultNotFoundProcessor = new DefaultNotFoundProcessor();
        this.defaultInternalServerErrorProcessor = new DefaultInternalServerErrorProcessor();
        this.defaultBadRequestProcessor = new DefaultBadRequestProcessor();
        this.defaultMethodNotAllowedProcessor = new DefaultMethodNotAllowedProcessor();
    }

    public void execute(HttpRequest request, OutputStream out) throws IOException {
        try {



            if (!parseRoutingKey(request, out)){
                defaultMethodNotAllowedProcessor.execute(request,out);
                return;
            }
            if (!processors.containsKey(request.getRoutingKey())) {
                defaultNotFoundProcessor.execute(request, out);
                parseRoutingKey(request,out);
                return;
            }
            processors.get(request.getRoutingKey()).execute(request, out);
        } catch (BadRequestException e) {
            request.setException(e);
            defaultBadRequestProcessor.execute(request, out);
        } catch (Exception e) {
            e.printStackTrace();
            defaultInternalServerErrorProcessor.execute(request, out);
        }
    }

    public boolean parseRoutingKey(HttpRequest request, OutputStream out){
        Set<String> set = processors.keySet();
        Map<String, List<String>> map = new HashMap<>();
        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        String[] value;
        for (String entry : set) {
            value = entry.split(" ", 2);
            if (value[0].equalsIgnoreCase(request.getMethod().toString())){
                list1.add(value[1]);
                map.put(value[0], list1);
            } else {
            list2.add(value[1]);
            map.put(value[0], list2);
            }
        }
        if (map.containsKey(request.getMethod().toString())) {
            for (String uri : map.get(request.getMethod().toString()))
                if (uri.equals(request.getUri().toString())) {
                    return true;
                }
        }
        return false;
    }
}
