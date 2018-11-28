package brander;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class ClientSimulator implements WSServerListener {

    public List<String> onClientRequest(String  clientID, String request){
        System.out.println(clientID+" ontvangt: "+request);
        WebCommand interval;
        interval = (WebCommand) jsonStringToObject(request, WebCommand.class);
        System.out.println(interval.toString());
        return null;
    }

    public static Object jsonStringToObject(String jsonString, Class<?> valueType) {

        Object jsonObject = null;
        ObjectMapper mapper = new ObjectMapper();

        try {
            jsonObject = mapper.readValue(jsonString, valueType);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }
}
