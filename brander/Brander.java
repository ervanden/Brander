package brander;

import com.pi4j.io.gpio.RaspiPin;

import javax.management.monitor.Monitor;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Brander {

    static int server_verbosity;
    static boolean server_active;
    static int server_port;
    final static String scheduleFileName = "/home/pi/Brander.json";
    // final static String scheduleFileName = "C:\\Users\\erikv\\Downloads\\Brander.json";


    private static void usage() {
        System.out.println("Usage :");
        System.out.println(" Scheduler port=6666 active=true verbosity=0");
        System.out.println();
    }

    public static void main(String[] args) throws InterruptedException {

        if (args.length > 0) {
            server_verbosity = 1;
            server_port = 4567;
            server_active = true;
            for (int arg = 1; arg <= args.length; arg++) {
                String[] s = args[arg - 1].split("=");
                if (s[0].equals("port")) {
                    server_port = Integer.parseInt(s[1]);
                } else if (s[0].equals("active")) {
                    server_active = Boolean.parseBoolean(s[1]);
                } else if (s[0].equals("verbosity")) {
                    server_verbosity = Integer.parseInt(s[1]);
                }
            }
            System.out.println("Scheduler starts at " + new Date().toString());
            System.out.println("port=" + server_port);
            System.out.println("verbosity=" + server_verbosity);
            System.out.println("active=" + server_active);
            System.out.println();

            ServerEngine serverEngine = new ServerEngine(server_port, server_verbosity, server_active);
            serverEngine.start();

//            Thread.sleep(1000);
//            serverEngine.serverEngineProtocol.onClientRequest("clientErik", "{\"command\":\"putSchedule\",\"arg\":\"reset\"}");
//            serverEngine.serverEngineProtocol.onClientRequest("clientErik", "{\"command\":\"putSchedule\",\"arg\":\"interval\",\"dag\":\"13/12/2018\",\"vanuur\":\"0\",\"vanmin\":\"0\",\"totuur\":\"23\",\"totmin\":\"59\"}");
//            serverEngine.serverEngineProtocol.onClientRequest("clientErik", "{\"dag\":\"SUNDAY\",\"vanuur\":0,\"vanmin\":0,\"totuur\":23,\"totmin\":55,\"command\":\"putSchedule\",\"arg\":\"interval\"}");
//            serverEngine.serverEngineProtocol.onClientRequest("clientErik", "{\"command\":\"putSchedule\",\"arg\":\"submit\"}");
        } else {
            usage();
        }
    }
}

