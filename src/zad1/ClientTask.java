/**
 *
 *  @author Zaborowski Mateusz S19101
 *
 */

package zad1;


import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClientTask implements Runnable {
    private Client client;
    private List<String> requestList;
    boolean showRes;
    private volatile String log = "";

    public ClientTask(Client client, List<String> requestList, boolean showRes) {
        this.client = client;
        this.requestList = requestList;
        this.showRes = showRes;
    }

    public static ClientTask create(Client client, List<String> requestList, boolean showRes) {
        return new ClientTask(client, requestList, showRes);
    }

    @Override
    public void run() {
        client.connect();
        client.send("login " + client.getId());
        for(String req : requestList) {
            String res = client.send(req);
            if (showRes) System.out.println(res);
        }
        log = client.send("bye and log transfer");
    }

    public String get() throws InterruptedException, ExecutionException {
        while (log.isEmpty()) {}
        return log;
    }
}
