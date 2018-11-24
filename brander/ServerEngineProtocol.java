package brander;

import java.util.ArrayList;
import java.util.Date;

public class ServerEngineProtocol implements WSServerListener {

    private ServerEngine serverEngine;

    ServerEngineProtocol(ServerEngine serverEngine) {
        this.serverEngine = serverEngine;
    }

    public ArrayList<String> onClientRequest(String clientID, String request) {
        ArrayList<String> reply = new ArrayList<String>();
        reply.add(new Date().toString()+ "ontvangen door de brander <" + request + ">");
        if (request.equals("GETSTATUS")){
            if (serverEngine.getState()){
                reply.add("ON");
            } else {
                reply.add("OFF");
            }
            return reply;
        }
        return reply;
    }
}
