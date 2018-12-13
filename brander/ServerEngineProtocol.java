package brander;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.time.format.DateTimeParseException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class ServerEngineProtocol implements WSServerListener {

    private ServerEngine serverEngine;
    private IntervalLijst newIntervals;
    final String scheduleFileName = "/home/pi/Brander.json";


    ServerEngineProtocol(ServerEngine serverEngine) {
        this.serverEngine = serverEngine;
        readJSONFile(scheduleFileName);
    }

    public List<String> onClientRequest(String clientID, String request) {
        ArrayList<String> reply = new ArrayList<>();
        System.out.println(clientID + " ontvangt: " + request);
        WebCommand cmd;
        cmd = (WebCommand) jsonStringToObject(request, WebCommand.class);
        System.out.println(cmd.toString());

        if (cmd.command.equals("getStatus")) {
            WebCommand webCommand = new WebCommand();
            webCommand.command = "status";
            if (serverEngine.getState()) {
                webCommand.arg = "ON";
            } else {
                webCommand.arg = "OFF";
            }
            reply.add(webCommand.toJSON());
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
            }
            if (cmd.arg.equals("submit")) {
                serverEngine.intervalLijst = newIntervals;
                serverEngine.serverEngineThread.interrupt();
                writeJSONFile(scheduleFileName, serverEngine.intervalLijst.intervals);
            }
            if (cmd.arg.equals("interval")) {
                newIntervals.add(cmd.toInterval());
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


    public static void writeJSONFile(String fileName, List<Interval> intervals) {
        try {
            File initialFile = new File(fileName);
            OutputStream is = new FileOutputStream(initialFile);
            OutputStreamWriter isr = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter outputStream = new BufferedWriter(isr);

            for (Interval interval : intervals) {
                System.out.println("{ "
                        + "\"" + "dag" + "\"" + ":" + "\"" + interval.getDag() + "\", "
                        + "\"" + "vanuur" + "\"" + ":" + interval.getVanUur() + ", "
                        + "\"" + "vanmin" + "\"" + ":" + interval.getVanMinuut() + ", "
                        + "\"" + "totuur" + "\"" + ":" + interval.getTotUur() + ", "
                        + "\"" + "totmin" + "\"" + ":" + interval.getTotMinuut()
                        + "}"
                );
                outputStream.write("{ "
                        + "\"" + "dag" + "\"" + ":" + "\"" + interval.getDag() + "\", "
                        + "\"" + "vanuur" + "\"" + ":" + interval.getVanUur() + ", "
                        + "\"" + "vanmin" + "\"" + ":" + interval.getVanMinuut() + ", "
                        + "\"" + "totuur" + "\"" + ":" + interval.getTotUur() + ", "
                        + "\"" + "totmin" + "\"" + ":" + interval.getTotMinuut()
                        + "}\r\n"
                );
            }
            outputStream.close();
        } catch (IOException io) {
            System.out.println("io exception");
        }
    }

    public void readJSONFile(String fileName) {
        ArrayList<Object> l;
        l = JSON2Object.readJSONFile(fileName, WebCommand.class);
        IntervalLijst newIntervals = new IntervalLijst();
        for (Object o : l) {
            WebCommand webCommand = (WebCommand) o;
            System.out.println(" read from json file " + webCommand.toString());
            System.out.println("   converted to interval " + webCommand.toInterval().toString());
            newIntervals.add(webCommand.toInterval());
        }
        serverEngine.intervalLijst = newIntervals;
        serverEngine.serverEngineThread.interrupt();
        String scheduleFileName = "/home/pi/Brander.json";
    }
}
