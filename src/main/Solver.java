package main;

import protocol.Protocol;

import java.util.*;

public class Solver {
    private State state;
    private Protocol protocol;
    private Queue<State.Site> route = new LinkedList<>();
    private State.Site from = null;
    private State.Site to = null;

    public Solver(State state, Protocol protocol) {
        this.state = state;
        this.protocol = protocol;
    }

    public void makeRoute(boolean ourRivers) {
        int minLength = Integer.MAX_VALUE;
        Set<State.Site> mines = new HashSet<>(state.getMines());
        for (State.Site mine : state.getMines()) {
            mines.remove(mine);
            Map<State.Site, LinkedList<State.Site>> shortestPaths =
                    BFS.shortestPathsBFS(mine, mines, true, ourRivers);
            for (Map.Entry<State.Site, LinkedList<State.Site>> shortestPath : shortestPaths.entrySet()) {
                int length = shortestPath.getValue().size();
                if (length < minLength && length > 1) {
                    minLength = length;
                    route = shortestPath.getValue();
                }
            }
        }
    }

    private void makeRoute(State.Site site, boolean ourRivers) {
        int minLength = Integer.MAX_VALUE;
        Set<State.Site> mines = state.getMines();
        Map<State.Site, LinkedList<State.Site>> shortestPaths =
                BFS.shortestPathsBFS(site, mines, true, ourRivers);
        for (Map.Entry<State.Site, LinkedList<State.Site>> shortestPath : shortestPaths.entrySet()) {
            int length = shortestPath.getValue().size();
            if (length < minLength && length > 1) {
                minLength = length;
                route = shortestPath.getValue();
            }
        }
    }

    private void makeRoute1(State.Site site, boolean ourRivers) {
        Map<State.Site, LinkedList<State.Site>> shortestPaths =
                BFS.shortestPathsBFS(site, state.getSites(), true, ourRivers);
        int maxLength = 1;
        for (Map.Entry<State.Site, LinkedList<State.Site>> shortestPath : shortestPaths.entrySet()) {
            int length = shortestPath.getValue().size();
            if (length > maxLength) {
                maxLength = length;
                route = shortestPath.getValue();
            }
        }
    }

    private boolean tryToMakeMove() {
        from = null;
        if (!route.isEmpty()) from = route.poll();
        to = null;
        if (!route.isEmpty()) to = route.peek();
        if (from != null && to != null) {
            for (State.River river : state.getRivers()) {
                if (river.getRiver().equals(new protocol.data.River(from.getId(), to.getId()))) {
                    if (river.getRiverState() == State.River.RiverState.Neutral) {
                        protocol.claimMove(from.getId(), to.getId());
                        return true;
                    } else if (river.getRiverState() == State.River.RiverState.Our) {
                        while (route.size() > 1) {
                            from = route.poll();
                            to = route.peek();
                            if (from.getNeighbors(true, false).contains(to)) {
                                for (State.River river1 : state.getRivers()) {
                                    if (river1.getRiver().equals(new protocol.data.River(from.getId(), to.getId()))) {
                                        if (river1.getRiverState() == State.River.RiverState.Neutral) {
                                            protocol.claimMove(from.getId(), to.getId());
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }

    public boolean makeMove() {
        if (tryToMakeMove()) return true;

        if (from != null) makeRoute(from, true);
        if (tryToMakeMove()) return true;

        if (from != null) makeRoute(from, false);
        if (tryToMakeMove()) return true;

        makeRoute(true);
        if (tryToMakeMove()) return true;

        makeRoute(false);
        if (tryToMakeMove()) return true;

        for (State.Site mine : state.getMines()) {
            if (mine.isFree()) {
                makeRoute1(mine, true);
                if (tryToMakeMove()) return true;
                makeRoute1(mine, false);
                if (tryToMakeMove()) return true;
            }
        }

        int size = 0;
        for (State.Site site : state.getSites()) {
            if (site.isOur() && site.isFree()) size++;
        }
        if (size != 0) {
            int item = new Random().nextInt(size);
            int i = 0;

            for (State.Site site : state.getSites()) {
                if (site.isOur() && site.isFree()) {
                    if (i == item) {
                        makeRoute1(site, true);
                        if (tryToMakeMove()) return true;
                        break;
                    }
                    i++;
                }
            }

            item = new Random().nextInt(size);
            i = 0;
            for (State.Site site : state.getSites()) {
                if (site.isOur() && site.isFree()) {
                    if (i == item) {
                        makeRoute1(site, false);
                        if (tryToMakeMove()) return true;
                        break;
                    }
                    i++;
                }
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
