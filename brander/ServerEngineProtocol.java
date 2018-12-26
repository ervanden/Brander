package brander;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

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
            for (Interval interval : serverEngine.intervalLijst.getIntervals()) {
                if (!interval.isVoorbij()) {
                    WebCommand webCommand = new WebCommand(interval);
                    webCommand.command = "schedule";
                    reply.add(webCommand.toJSON());
                }
            }
        }
        if (cmd.command.equals("putSchedule")) {
            if (cmd.arg.equals("reset")) {
                newIntervals = new IntervalLijst();
            }
            if (cmd.arg.equals("submit")) {
                serverEngine.intervalLijst = newIntervals;
                serverEngine.serverEngineThread.interrupt();
                writeJSONFile(Brander.scheduleFileName, serverEngine.intervalLijst.getIntervals());
                WebCommand webCommand = new WebCommand();
                webCommand.command = "submitConfirmation";
                reply.add(webCommand.toJSON());
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


}
