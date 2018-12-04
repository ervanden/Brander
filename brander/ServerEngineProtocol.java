package brander;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerEngineProtocol implements WSServerListener {

    private ServerEngine serverEngine;

    ServerEngineProtocol(ServerEngine serverEngine) {
        this.serverEngine = serverEngine;
    }

    public List<String> onClientRequest(String  clientID, String request){
        ArrayList<String> reply = new ArrayList<>();
        System.out.println(clientID+" ontvangt: "+request);
        WebCommand cmd;
        cmd = (WebCommand) jsonStringToObject(request, WebCommand.class);
        System.out.println(cmd.toString());
        Interval interval=null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/y");
            LocalDate date = LocalDate.parse(cmd.dag, formatter);
            LocalDateTime dt1=date.atTime(cmd.vanuur,cmd.vanmin);
            LocalDateTime dt2=date.atTime(cmd.totuur,cmd.totmin);
            interval = new Interval(dt1,dt2);
        } catch (DateTimeParseException e){
            LocalDate date = LocalDate.now();
            LocalDateTime dt1=date.atTime(cmd.vanuur,cmd.vanmin);
            LocalDateTime dt2=date.atTime(cmd.totuur,cmd.totmin);
            interval = new Interval(cmd.dag,dt1,dt2);

        }
        reply.add(interval.toString());
        return reply;
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
