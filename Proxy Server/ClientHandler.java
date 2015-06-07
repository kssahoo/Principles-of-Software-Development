import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;


public class ClientHandler implements Runnable
{
    String URI = "";
    String httpVersion = "";
    String host = "";
    String headers = "";
    String serverResponseHeaders = "";
    String serverResponseStatus = "";
    Socket server = null;
    int contentLength = 0;
    byte[] serverResponseBody = new byte[500000];
    HashMap<String, String> map = new HashMap();
    Socket client = new Socket();
    public ClientHandler(Socket client)
    {
        this.client = client;
    }
    public synchronized void run()
    {
        try
        {
            readLine(client);
            server = openUpstreamSocket();
            makeUpstreamRequest(server);
            getUpStreamResponse(server);
            forwardRemoteDataToBrowser();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    public void forwardRemoteDataToBrowser() throws IOException
    {
        DataOutputStream toClient = new DataOutputStream(client.getOutputStream());
        toClient.writeBytes(serverResponseHeaders);
        toClient.write(serverResponseBody);
        toClient.flush();
        toClient.close();
        client.close();
    }
    public String getHeaders()
    {
        return headers;
    }
    public String getHost()
    {
        return host;
    }
    public void makeUpstreamRequest(Socket server) throws IOException
    {
        writeHeaders(server);

    }
    public Socket openUpstreamSocket() throws UnknownHostException, IOException
    {
        server = new Socket(getHost(),80);
        return server;

    }
    public void writeHeaders(Socket server) throws IOException
    {
        DataOutputStream toServer = new DataOutputStream(server.getOutputStream());
        toServer.writeBytes(getHeaders());
    }
    public String gerServerResponseHeaders()
    {
        return serverResponseHeaders;
    }

    public void getUpStreamResponse(Socket server) throws IOException
    {
        DataInputStream fromServer = new DataInputStream(server.getInputStream());
        String data = fromServer.readLine();
        serverResponseStatus = data;
        while(data.length() != 0)
        {
            if(!(data.startsWith("Connection") && data.split(" ")[1].equals("Keep-Alive")))
            {
                serverResponseHeaders += data + "\r\n" ;
            }
            if(data.startsWith("Content-Length") || data.startsWith("Content-length"))
            {
                contentLength = Integer.parseInt(data.split(" ")[1]);
            }
            data = fromServer.readLine();

        }
        serverResponseHeaders += "\r\n";

        int bytesRead = 0;
        byte[] dataFromServer = new byte[6000];
        boolean status = true;
        while(bytesRead < contentLength || status)
        {
            int len = fromServer.read(dataFromServer, 0 , 6000);
            if(len == -1)
            {
                break;
            }
            for (int i = 0; i < len && (i + bytesRead) < 500000; i++)
            {
                serverResponseBody[bytesRead + i] = dataFromServer[i];
            }
            bytesRead += len;
        }
        fromServer.close();
        server.close();
    }
    public void process()
    {

    }
    public void readLine(Socket client) throws IOException
    {
        System.out.println("Reading from client");
        DataInputStream fromClient = new DataInputStream(client.getInputStream());
        String line = fromClient.readLine();
        if(line.length() == 0)
        {
            client.close();
            return;
        }
        String[] values = line.split(" ");
        URI = values[1];
        int index = URI.indexOf("/", URI.indexOf("//") + 2);
        String file = "";
        if(!(index == -1))
        {
            CharSequence chSeq = URI.subSequence(index, URI.length());
            file = String.valueOf(chSeq);
        }
        httpVersion = values[2];
        if(line.startsWith("GET"))
        {
            if(httpVersion.equalsIgnoreCase("HTTP/1.1"))
            {
                httpVersion = "HTTP/1.0";
            }
            if(file == null)
            {
                file = "/";
            }
            headers += "GET" + " " + file + " " + httpVersion + "\r\n";
        }
        while(line.length() != 0)
        {
            if(!(line.startsWith("User-Agent") || line.startsWith("Referer") || line.startsWith("Proxy-Connection") || line.startsWith("Connection") || line.startsWith("GET")))
            {
                String[] headerValues = line.split(":");
                String headerName = headerValues[0].toLowerCase();
                headers += headerName + ":"+" "+headerValues[1]+ "\r\n";
                // key and value pair is stored in the map
                map.put(headerName, headerValues[1]);
            }
            if(line.startsWith("Host:"))
            {
                host = line.split(" ")[1];
            }
            line =fromClient.readLine();
        }
        headers += "\r\n";
    }



}
