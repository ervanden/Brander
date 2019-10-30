package brander;

import java.util.Date;

public class Brander { // commit test

    static int server_verbosity;
    static boolean server_active;
    static int server_port;
    final static String scheduleFileName = "/home/pi/Brander.json";
    final static String logFileName = "/home/pi/BranderLog.txt";
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
        } else {
            usage();
        }
    }
}

