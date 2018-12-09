
package brander;

import java.util.List;


public interface WSServerListener {

    // When a client connects, its message is forwarded to the listener.
    // The listener object handles the request and returns the reply to be sent back to the client.
    
   List<String> onClientRequest(String  clientID, String request);

}
