/**
 * @author Zaborowski Mateusz S19101
 */

package zad1;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Server {
    private String host;
    private int port;
    //private final static String[] DATE_PATTERNS = {"^\\d{4}-\\d{2}-\\d{2} \\d{4}-\\d{2}-\\d{2}$", "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2} \\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$"};
    private Map<SocketAddress, String> clients = new HashMap<>();
    private StringBuilder log = new StringBuilder();
    private volatile boolean serverIsRunning;
    private Thread serverThread;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startServer() {
        serverThread = new Thread(() -> {
            serverIsRunning = true;
            try {
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.bind(new InetSocketAddress(host, port));
                serverSocketChannel.configureBlocking(false);
                Selector selector = Selector.open();
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                while (serverIsRunning) {
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = keys.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isAcceptable()) {
                            SocketChannel sc = serverSocketChannel.accept();
                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_READ);
                            continue;
                        }
                        if (key.isReadable()) {
                            SocketChannel sc = (SocketChannel)key.channel();
                            serviceRequest(sc);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            serverIsRunning = false;
        });
        serverThread.start();
    }

    private StringBuffer requestBuffer = new StringBuffer();
    private static Charset charset = StandardCharsets.UTF_8;
    private ByteBuffer inBuffer = ByteBuffer.allocate(1024);
    private void serviceRequest(SocketChannel sc) {
        if (!sc.isOpen()) return;
        requestBuffer.setLength(0);
        inBuffer.clear();
        try {
            while (true) {
                int readBytes = sc.read(inBuffer);
                if (readBytes > 0) {
                    inBuffer.flip();
                    CharBuffer charBuffer = charset.decode(inBuffer);
                    requestBuffer.append(charBuffer);
                    break;
                }
            }
            SocketAddress address = sc.getRemoteAddress();
            String request = requestBuffer.toString();
            if (request.contains("login")) {
                clients.put(address, request.split(" ")[1]);
                log.append(clients.get(address)).append(" logged in at ").append(getTime()).append("\n");
                writeResponse(sc, "logged in");
            } else if (request.contains("bye")) {
                log.append(clients.get(address)).append(" logged out at ").append(getTime()).append("\n");
                writeResponse(sc, request);
            } else {
                log.append(clients.get(address)).append(" request at ").append(getTime()).append(": \"").append(request).append("\"").append("\n");
                String[] reqSplit = request.split(" ");
                String msg = Time.passed(reqSplit[0], reqSplit[1]);
                writeResponse(sc, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                sc.socket().close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    StringBuffer responseBuffer = new StringBuffer();
    private void writeResponse(SocketChannel sc, String msg) throws Exception {
        responseBuffer.setLength(0);
        responseBuffer.append(msg);
        ByteBuffer buffer = charset.encode(CharBuffer.wrap(responseBuffer));
        sc.write(buffer);
    }

    public String getTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }

    public void stopServer() {
        serverIsRunning = false;
        serverThread.interrupt();
    }

    public String getServerLog() {
        return log.toString();
    }
}
