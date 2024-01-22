import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.sun.net.httpserver.*;

class API
{
    public static void main(String[] args) {
        
        try {
            
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            server.createContext("/api", new DummyAPI());

            int cpuCount = Runtime.getRuntime().availableProcessors();

            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(cpuCount);
            server.setExecutor(threadPoolExecutor);
            server.start();
            System.out.println("API server started.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

