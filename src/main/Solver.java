package main;

import protocol.Protocol;

import java.util.*;

public class Solver {
    private static class PairSites {
        private State.Site site1;
        private State.Site site2;

        PairSites(State.Site site1, State.Site site2) {
            this.site1 = site1;
            this.site2 = site2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PairSites pairSites = (PairSites) o;

            return site1.equals(pairSites.site1) && site2.equals(pairSites.site2) ||
                    site1.equals(pairSites.site2) && site2.equals(pairSites.site1);
        }

        @Override
        public int hashCode() {
            return site1.hashCode() ^ site2.hashCode();
        }
    }

    private State state;
    private Protocol protocol;
    private Deque<State.Site> route = new LinkedList<>();
    private Set<PairSites> connectedMines = new HashSet<>();
    private State.Site from;
    private State.Site to;
    private int parityOfMove = 0;
    private boolean twoWaysMovement = true;


    public Solver(State state, Protocol protocol) {
        this.state = state;
        this.protocol = protocol;
        makeRouteMtoM();
    }

    private int lengthOfRoute(LinkedList<State.Site> route1) {
        Queue<State.Site> route = new LinkedList<>(route1);
        int length = 0;
        while (route.size() > 1) {
            State.Site site1 = route.poll();
            State.Site site2 = route.peek();
            if (site1 != null && site1.getNeighbors(true, false,false).contains(site2)) length++;
        }
        return length;
    }

    private boolean isSitesConnected(State.Site site1, State.Site site2) {
        Queue<State.Site> queue = new LinkedList<>();
        Set<State.Site> visited = new HashSet<>();
        queue.add(site1);
        visited.add(site1);
        while (!queue.isEmpty()) {
            State.Site next = queue.poll();
            if (next.equals(site2)) return true;
            for (State.Site neighbor : next.getNeighbors(false, true, false)) {
                if (visited.contains(neighbor)) continue;
                visited.add(neighbor);
                queue.add(neighbor);
            }
        }
        return false;
    }

    private void makeRouteMtoM() {
        int minLength = Integer.MAX_VALUE;
        Set<State.Site> mines = new HashSet<>(state.getMines());
        for (State.Site mine : state.getMines()) {
            mines.remove(mine);
            if (mine.isFree()) {
                Map<State.Site, LinkedList<State.Site>> shortestPaths =
                        BFS.shortestPathsBFS(mine, mines);
                for (Map.Entry<State.Site, LinkedList<State.Site>> shortestPath : shortestPaths.entrySet()) {
                    int length = lengthOfRoute(shortestPath.getValue());
                    State.Site mine0 = shortestPath.getKey();
                    if (!mine.equals(mine0) && !connectedMines.contains(new PairSites(mine, mine0)) &&
                            isSitesConnected(mine, mine0)) {
                        connectedMines.add(new PairSites(mine, mine0));
                    } else if (!isSitesConnected(mine, mine0) && length < minLength && length > 1) {
                        minLength = length;
                        route = shortestPath.getValue();
                    }
                }
            }
        }
    }

    private void makeLongestRoute() {
        int maxLength = 1;
        for (State.Site mine : state.getMines()) {
            if (mine.isFree()) {
                Map<State.Site, LinkedList<State.Site>> shortestPaths =
                        BFS.shortestPathsBFS(mine, state.getSites());
                for (Map.Entry<State.Site, LinkedList<State.Site>> shortestPath : shortestPaths.entrySet()) {
                    int length = shortestPath.getValue().size();
                    if (length > maxLength) {
                        maxLength = length;
                        route = shortestPath.getValue();
                    }
                }
            }
        }
    }

    private boolean isRouteFree() {
        Deque<State.Site> path = new LinkedList<>(route);
        while (path.size() > 1) {
            State.Site site1 = path.poll();
            State.Site site2 = path.peek();
            if (site1.getNeighbors(false, false, true).contains(site2)) return false;
        }
        return true;
    }

    private boolean currentMove() {
        if (route.size() < 2) return false;
        if (twoWaysMovement) {
            if (parityOfMove % 2 == 1) {
                from = route.poll();
                to = route.peek();
            } else {
                from = route.pollLast();
                to = route.peekLast();
            }
        } else {
            from = route.poll();
            to = route.peek();
        }
        return true;
    }

    private boolean tryToMakeMove() {
        if (!isRouteFree()) return false;
        while (currentMove()) {
            for (State.River river : state.getRivers()) {
                if (river.equals(new State.River(new State.Site(from.getId()), new State.Site(to.getId())))) {
                    if (river.isNeutral()) {
                        protocol.claimMove(from.getId(), to.getId());
                        parityOfMove++;
                        return true;
                    } else if (river.isEnemy()) return false;
                    break;
                }
            }
        }
        return false;
    }

    public void makeMove() {
        if (tryToMakeMove()) return;

        if (connectedMines.size() < (state.getMines().size() * (state.getMines().size() - 1)) / 2) {
            makeRouteMtoM();
            twoWaysMovement = true;
            if (tryToMakeMove()) return;
        }

        makeLongestRoute();
        twoWaysMovement = false;
        if (tryToMakeMove()) return;

        for (State.River river : state.getRivers()) {
            if (river.isNeutral()) {
                protocol.claimMove(river.getSource().getId(), river.getTarget().getId());
                return;
            }
        }

        protocol.passMove();
    }
}
