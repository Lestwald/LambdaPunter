import main.Solver;
import main.State;
import org.kohsuke.args4j.*;
import protocol.Protocol;
import protocol.data.*;

public class Main {
    @Option(name = "-u")
    private static String url;
    @Option(name = "-p")
    private static int port;

    public static void main(String[] args) {
        new Main().use(args);
        Protocol protocol = new Protocol(url, port);
        State state = new State();
        Solver solver = new Solver(state, protocol);

        protocol.handShake("Trilogy");
        Setup setup = protocol.setup();
        state.init(setup);
        protocol.ready();

        int k = 0;
        while(true) {
            ServerMessage message = protocol.serverMessage();
            if (message instanceof GameResult) break;
            else if (message instanceof GameTurnMessage) {
                for (protocol.data.Move move :((GameTurnMessage) message).getMove().getMoves()) {
                    if (move instanceof ClaimMove) state.update(((ClaimMove) move).getClaim());
                }
            }
            if (k == 0) {
                k++;
                solver.makeRoute(solver.findMine());
            }
            solver.makeMove();
        }
    }

    private void use(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
        }
    }

}
