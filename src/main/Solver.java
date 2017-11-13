package main;

import protocol.Protocol;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Solver {
    private State state;
    private Protocol protocol;
    Queue<State.Site> route;
    State.Site from = null;
    State.Site to = null;

    public Solver(State state, Protocol protocol) {
        this.state = state;
        this.protocol = protocol;
    }

    public State.Site findMine() {
        State.Site mine = null;
        for (State.Site site : state.getSites()) {
            if (site.isMine()) {
                mine = site;
                break;
            }
        };
        return mine;
    }
    public void makeRoute(State.Site site) {
        Map<State.Site, LinkedList<State.Site>> shortestPaths = BFS.shortestPathsBFS(site, state.getSites());
        int maxLength = 1;
        for (Map.Entry<State.Site, LinkedList<State.Site>> shortestPath : shortestPaths.entrySet()) {
            int length = shortestPath.getValue().size();
            if (length > maxLength) {
                maxLength = length;
                route = shortestPath.getValue();
            }
        }
    }


    public boolean tryToMakeMove() {
        if (!route.isEmpty()) from = route.poll();
        if (!route.isEmpty()) to = route.peek();
        if (from != null && to != null) {
            for (State.River river : state.getRivers()) {
                if (river.getRiver().equals(new protocol.data.River(from.getId(), to.getId()))) {
                    if (river.getRiverState() == State.River.RiverState.Neutral) {
                        protocol.claimMove(from.getId(), to.getId());
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    public boolean makeMove() {
        if (tryToMakeMove()) return true;
        else {
            if (from != null) makeRoute(from);
            if (tryToMakeMove()) return true;
            else {
                makeRoute(findMine());
                if (tryToMakeMove()) return true;
            }
        }
        for (State.River river : state.getRivers()) {
            if (river.getRiverState() == State.River.RiverState.Neutral) {
                for (State.Site site : river.getSites()) {
                    if (site.isOur()) {
                        protocol.claimMove(river.getRiver().getSource(), river.getRiver().getTarget());
                        return true;
                    }
                }
            }
        }
        for (State.River river : state.getRivers()) {
            if (river.getRiverState() == State.River.RiverState.Neutral) {
                protocol.claimMove(river.getRiver().getSource(), river.getRiver().getTarget());
                return true;
            }
        }
        protocol.passMove();
        return true;
    }
}
