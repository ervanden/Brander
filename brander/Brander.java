package brander;

import java.time.LocalDateTime;
import java.util.Date;

public class Brander {

    static int server_verbosity;
    static boolean server_active;
    static int server_port;
    static boolean server_test;

    private static void usage() {
        System.out.println("Usage :");
        System.out.println(" Scheduler port=6666 active=true verbosity=0");
        System.out.println();
    }

    public static void main(String[] args) {

        if (args.length > 0) {
            server_verbosity = 1;
            server_port = 4567;
            server_active = true;
            server_test = false;
            for (int arg = 1; arg <= args.length; arg++) {
                String[] s = args[arg - 1].split("=");
                if (s[0].equals("port")) {
                    server_port = Integer.parseInt(s[1]);
                } else if (s[0].equals("active")) {
                    server_active = Boolean.parseBoolean(s[1]);
                } else if (s[0].equals("test")) {
                    server_test = Boolean.parseBoolean(s[1]);
                } else if (s[0].equals("verbosity")) {
                    server_verbosity = Integer.parseInt(s[1]);
                }
            }
            System.out.println("Scheduler starts at " + new Date().toString());
            System.out.println("port=" + server_port);
            System.out.println("verbosity=" + server_verbosity);
            System.out.println("active=" + server_active);
            System.out.println("test=" + server_test);
            System.out.println();

         //            new ServerEngine(server_port,server_verbosity,server_active, server_test).start();
//            ClientSimulator clientSimulator = new ClientSimulator();
//
//            String intervalString;
//            intervalString = "{\"command\":\"interval\", \"arg\":\"eerste\",\"dag\":\"MAANDAG\", \"vanuur\":3, \"vanmin\":15, \"totuur\":15, \"totmin\":35}";
//            clientSimulator.onClientRequest("erik", intervalString);
//            intervalString = "{\"command\":\"interval\", \"arg\":\"laatste\",\"dag\":\"21/5/2018\", \"vanuur\":3, \"vanmin\":15, \"totuur\":15, \"totmin\":35}";
//            clientSimulator.onClientRequest("erik", intervalString);

            LocalDateTime van=LocalDateTime.now().plusMinutes(10);
            LocalDateTime tot=van.plusMinutes(10);
            Interval interval = new Interval(van,tot);
            LocalDateTime d=LocalDateTime.now();
            for (int i = 0; i<30; i++){
                d=d.plusMinutes(1);
                if (interval.contains(d)) {
                    System.out.println("IN " + d.toString() + " " + interval.toString());
                } else {
                    System.out.println("OUT " + d.toString() + " " + interval.toString());
                }
            }
        } else {
            usage();
        }
    }
}

