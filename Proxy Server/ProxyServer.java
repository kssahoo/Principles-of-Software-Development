import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ProxyServer {
    public static void main(String[] args) throws IOException
    {
        int port = 0;
        try
        {
            port = Integer.parseInt(args[0]);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            System.out.println(e.getMessage());
        }
        if(port == 0)
        {
            port = 8080;
        }
        ServerSocket socket = new ServerSocket(port);
        System.out.println("Proxy server is started...");
        System.out.println("and listening at port :"+port);
        Socket client = socket.accept();
        while(client != null)
        {
            ClientHandler handlerObj = new ClientHandler(client);
            Thread threadOne = new Thread(handlerObj);
            threadOne.start();
            client = socket.accept();
        }

    }

}
