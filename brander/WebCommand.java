package brander;

public class WebCommand {
    public String command;
    public String arg;
    public String dag;
    public Integer vanuur;
    public Integer vanmin;
    public Integer totuur;
    public Integer totmin;

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
}
