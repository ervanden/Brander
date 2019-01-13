package brander;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerEngineProtocol implements WSServerListener {

    private ServerEngine serverEngine;
    private IntervalLijst newIntervals;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E dd/MM/yyyy");


    ServerEngineProtocol(ServerEngine serverEngine) {
        this.serverEngine = serverEngine;
    }

    public List<String> onClientRequest(String clientID, String request) {
        ArrayList<String> reply = new ArrayList<>();
        System.out.println(clientID + " ontvangt: " + request);
        WebCommand cmd;
        cmd = (WebCommand) jsonStringToObject(request, WebCommand.class);
        System.out.println(cmd.toString());

        // client vraagt om ON en HEATING status door te sturen
        if (cmd.command.equals("getStatus")) {
            WebCommand webCommand = new WebCommand();
            webCommand.command = "status";
            if (serverEngine.getState()) {
                webCommand.arg1 = "ON";
            } else {
                webCommand.arg1 = "OFF";
            }
            reply.add(webCommand.toJSON());

            webCommand.command = "fire";
            if (serverEngine.monitorThread.getHeatingState()) {
                webCommand.arg1 = "ON";
            } else {
                webCommand.arg1 = "OFF";
            }
            reply.add(webCommand.toJSON());
        }
        // client vraagt de schedule
        if (cmd.command.equals("getSchedule")) {
            for (Interval interval : serverEngine.intervalLijst.getIntervals()) {
                if (!interval.isVoorbij()) {
                    WebCommand webCommand = new WebCommand(interval);
                    webCommand.command = "schedule";
                    reply.add(webCommand.toJSON());
                }
            }
        }
        // client stuurt een ge-update schedule door
        if (cmd.command.equals("putSchedule")) {
            if (cmd.arg1.equals("reset")) {
                newIntervals = new IntervalLijst();
            }
            if (cmd.arg1.equals("submit")) {
                serverEngine.intervalLijst = newIntervals;
                serverEngine.serverEngineThread.interrupt();
                writeJSONFile(Brander.scheduleFileName, serverEngine.intervalLijst.getIntervals());
                WebCommand webCommand = new WebCommand();
                webCommand.command = "submitConfirmation";
                reply.add(webCommand.toJSON());
            }
            if (cmd.arg1.equals("interval")) {
                newIntervals.add(cmd.toInterval());
            }
        }
        // client vraagt om minuten HEATING voor de laatste 'arg1' dagen door te sturen
        if (cmd.command.equals("data")) {
            WebCommand webCommand = new WebCommand();
            webCommand.command = "data";
            webCommand.arg1 = "start";
            reply.add(webCommand.toJSON());

            String aantalDagenString = cmd.arg1;
            int aantalDagen = Integer.parseInt(aantalDagenString);
            List<DagTotaal> dagTotalen = serverEngine.logger.dagTotalen(aantalDagen);
            Collections.reverse(dagTotalen); // meest recente eerst
            int dag = 0;
            for (DagTotaal dagTotaal : dagTotalen) {
                dag++;
                if (dag <= aantalDagen) {
                    webCommand.command = "data";
                    webCommand.arg1 = dagTotaal.getDatum().format(formatter);
                    webCommand.arg2 = ((Integer) dagTotaal.getSeconden()).toString();
                    reply.add(webCommand.toJSON());
                }
            }

            webCommand.command = "data";
            webCommand.arg1 = "end";
            reply.add(webCommand.toJSON());
        }
        // client vraagt om minuten HEATING voor een bepaalde dag (arg1) door te sturen
        if (cmd.command.equals("data2")) {
            String datumString = cmd.arg1;
            WebCommand webCommand = new WebCommand();
            webCommand.command = "data2";
            webCommand.arg1 = "start";
            reply.add(webCommand.toJSON());

            LocalDate datum = LocalDate.parse(datumString, formatter);
            List<MinuutStatus> l = serverEngine.logger.statusPerMinuut(datum, 10);
            for (MinuutStatus minuutStatus : l) {
                webCommand.command = "data2";
                int uur = minuutStatus.getUur();
                int minuut = minuutStatus.getMinuut();
                webCommand.arg1 = ((uur < 10) ? "0" : "") + uur + ":" + ((minuut < 10) ? "0" : "") + minuut;
                webCommand.arg2 = minuutStatus.isStatus() ? "1" : "0";
                reply.add(webCommand.toJSON());
            }
            webCommand.command = "data2";
            webCommand.arg1 = "end";
            reply.add(webCommand.toJSON());
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
