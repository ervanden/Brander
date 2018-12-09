package brander;

public class WebCommand {
    public String command;
    public String arg;
    public String dag;
    public Integer vanuur;
    public Integer vanmin;
    public Integer totuur;
    public Integer totmin;

    public WebCommand() {

    }

    public WebCommand(Interval interval) {
        dag = interval.getDag();
        vanuur = interval.getVanUur();
        totuur = interval.getTotUur();
        totmin = interval.getTotMinuut();
        vanmin = interval.getVanMinuut();
    }

    public String toString() {
        String s = "";
        s = s + "{\n";
        s = s + "command : " + command + "\n";
        s = s + "arg : " + arg + "\n";
        s = s + "dag : " + dag + "\n";
        s = s + "vanuur : " + vanuur + "\n";
        s = s + "vanmin : " + vanmin + "\n";
        s = s + "totuur : " + totuur + "\n";
        s = s + "totmin : " + totmin + "\n";
        s = s + "}";
        return s;
    }

    public String toJSON() {
        return "{"
                + "\"command\":\"" + command + "\","
                + "\"arg\":\"" + arg + "\","
                + "\"dag\":\"" + dag + "\","
                + "\"vanuur\":\"" + vanuur + "\","
                + "\"vanmin\":\"" + vanmin + "\","
                + "\"totuur\":\"" + totuur + "\","
                + "\"totmin\":\"" + totmin + "\""
                + "}";
    }
}
