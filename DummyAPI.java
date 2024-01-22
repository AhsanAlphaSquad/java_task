import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class DummyAPI implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        HashMap<String, String> body = GetBody(exchange.getRequestBody());
        int statusCode = 200;
        String content = "";
        
        if("GET".equals(exchange.getRequestMethod()))
        {
            File file = new File(body.get("path"));
            if (!file.exists())
            {
                statusCode = 404;
                content = "{ error: \"File not found!\" }";
            }
            else
            {
                content = new String(Files.readAllBytes(file.toPath()));
            }
        }
        else if("POST".equals(exchange.getRequestMethod()))
        {
            File file = new File(body.get("path"));
            if(file.exists())
            {
                content = "{ error: \"File already exists!\" }";
                statusCode = 409;
            }
            else
            {
                Files.write(file.toPath(), body.get("content").getBytes(), StandardOpenOption.CREATE);
                exchange.sendResponseHeaders(200, 0);    
            }
        }
        else if ("PUT".equals(exchange.getRequestMethod()))
        {
            File file = new File(body.get("path"));
            if(!file.exists())
            {
                content = "{ error: \"File not found!\" }";
                statusCode = 404;
            }
            else
            {
                Files.write(file.toPath(), body.get("content").getBytes());
                content = "{ success: \"File updated!\" }";
            }
        }
        else if("PATCH".equals(exchange.getRequestMethod()))
        {
            File file = new File(body.get("path"));
            if(!file.exists())
            {
                content = "{ error: \"File not found!\" }";
                statusCode = 404;                
            }
            else
            {
                Files.write(file.toPath(), body.get("content").getBytes(), StandardOpenOption.APPEND);
                content = "{ success: \"File patched!\" }";
            }
        }

        else if ("DELETE".equals(exchange.getRequestMethod()))
        {
            File file = new File(body.get("path"));
            if(!file.exists())
            {
                content = "{ error: \"File not found!\" }";
                statusCode = 404;
            }
            else
            {
                file.delete();
                content = "{ success: \"File deleted!\" }";
            }
        }
        else
        {
            statusCode = 405;
        }
        
        exchange.sendResponseHeaders(statusCode, content.length());
        exchange.getResponseBody().write(content.getBytes());
        exchange.close();
    }

    HashMap<String, String> GetBody(InputStream stream) throws IOException
    {
        String body = new String(stream.readAllBytes());
        stream.close();
        body = java.net.URLDecoder.decode(body, "UTF-8");

        String[] pair = body.split("&");
        HashMap<String, String> map = new HashMap<>();            
        for(String p : pair)
        {
            map.put(p.split("=")[0], p.split("=")[1]);
        }

        return map;
    }

}
