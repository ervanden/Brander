package brander;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ServerEngineProtocol implements WSServerListener {

    private ServerEngine serverEngine;
    private IntervalLijst newIntervals;

    ServerEngineProtocol(ServerEngine serverEngine) {
        this.serverEngine = serverEngine;
    }

    public List<String> onClientRequest(String clientID, String request) {
        ArrayList<String> reply = new ArrayList<>();
        System.out.println(clientID + " ontvangt: " + request);
        WebCommand cmd;
        cmd = (WebCommand) jsonStringToObject(request, WebCommand.class);
        System.out.println(cmd.toString());

        if (cmd.command.equals("status")) {
            if (serverEngine.getState()) {
                reply.add("ON");
            } else {
                reply.add("OFF");
            }
        }

        if (cmd.command.equals("getSchedule")) {
            for (Interval interval : serverEngine.intervalLijst.intervals) {
                WebCommand webCommand = new WebCommand(interval);
                webCommand.command = "schedule";
                reply.add(webCommand.toJSON());
            }
        }
        if (cmd.command.equals("putSchedule")) {
            if (cmd.arg.equals("reset")) {
                newIntervals = new IntervalLijst();
                WebCommand webCommand = new WebCommand();
                webCommand.command = "reset OK";
                reply.add(webCommand.toJSON());
            }
            if (cmd.arg.equals("submit")) {
                serverEngine.intervalLijst = newIntervals;
                WebCommand webCommand = new WebCommand();
                webCommand.command = "submit OK";
                reply.add(webCommand.toJSON());
            }
            if (cmd.arg.equals("interval")) {
                Interval interval = null;
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/y");
                    LocalDate datum = LocalDate.parse(cmd.dag, formatter);
                    interval = new EenmaligInterval(datum, cmd.vanuur, cmd.vanmin, cmd.totuur, cmd.totmin);
                } catch (DateTimeParseException e) {
                    interval = new HerhalendInterval(cmd.dag, cmd.vanuur, cmd.vanmin, cmd.totuur, cmd.totmin);
                }
                newIntervals.add(interval);
                reply.add("putSchedule interval OK: " + interval.toString());
            }
        }

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
