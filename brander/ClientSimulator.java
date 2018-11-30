package brander;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ClientSimulator implements WSServerListener {

    public List<String> onClientRequest(String  clientID, String request){
        System.out.println(clientID+" ontvangt: "+request);
        WebCommand cmd;
        cmd = (WebCommand) jsonStringToObject(request, WebCommand.class);
        System.out.println(cmd.toString());
        Interval interval=null;
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(cmd.dag);
             interval= new Interval(date,cmd.vanuur,cmd.vanmin,cmd.totuur,cmd.totmin);
        } catch (ParseException p){
             interval= new Interval(cmd.dag,cmd.vanuur,cmd.vanmin,cmd.totuur,cmd.totmin);
        }
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
