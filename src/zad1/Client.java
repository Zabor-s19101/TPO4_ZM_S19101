/**
 *
 *  @author Zaborowski Mateusz S19101
 *
 */

package zad1;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client {
    private String host;
    private int port;
    private String id;
    private SocketChannel sc;
    private StringBuilder log = new StringBuilder();

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void connect() {
        try {
            sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.connect(new InetSocketAddress(host, port));
            while (!sc.finishConnect()) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StringBuffer responseBuffer = new StringBuffer();
    private Charset charset = StandardCharsets.UTF_8;
    private ByteBuffer inBuffer = ByteBuffer.allocate(1024);
    public String send(String request) {
        String response = null;
        responseBuffer.setLength(0);
        CharBuffer charBuffer = CharBuffer.wrap(request);
        ByteBuffer outBuffer = charset.encode(charBuffer);
        try {
            sc.write(outBuffer);
            while (true) {
                inBuffer.clear();
                int readBytes = sc.read(inBuffer);
                if (readBytes > 0) {
                    inBuffer.flip();
                    charBuffer = charset.decode(inBuffer);
                    responseBuffer.append(charBuffer);
                    break;
                }
            }
            response = responseBuffer.toString();
            if (response.equals("logged in")) {
                log.append("=== ").append(id).append(" log start ===").append("\n")
                        .append(response).append("\n");
            } else if (response.contains("bye")) {
                log.append("logged out").append("\n")
                        .append("=== ").append(id).append(" log end ===\n");
                if (response.equals("bye and log transfer")) {
                    response = log.toString();
                }
            } else {
                log.append("Request: ").append(request).append("\n")
                        .append("Result: ").append("\n")
                        .append(response).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
