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
    private State.Site from = null;
    private State.Site to = null;
    private Set<PairSites> connectedMines = new HashSet<>();
    private int parity = 0;
    private int flag = 1; // 0 - в одну сторону, 1 - в две

    public Solver(State state, Protocol protocol) {
        this.state = state;
        this.protocol = protocol;
    }

    private int lengthOfRoute(LinkedList<State.Site> route1) {
        Queue<State.Site> route = new LinkedList<>(route1);
        int length = 0;
        while (route.size() > 1) {
            State.Site site1 = route.poll();
            State.Site site2 = route.peek();
            if (site1 != null && site1.getNeighbors(true, false).contains(site2)) length++;
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
            for (State.Site neighbor : next.getNeighbors(false, true)) {
                if (visited.contains(neighbor)) continue;
                visited.add(neighbor);
                queue.add(neighbor);
            }
        }
        return false;
    }

    public void makeRouteMtoM() {
        int minLength = Integer.MAX_VALUE;
        Set<State.Site> mines = new HashSet<>(state.getMines());
        for (State.Site mine : state.getMines()) {
            mines.remove(mine);
            Map<State.Site, LinkedList<State.Site>> shortestPaths =
                    BFS.shortestPathsBFS(mine, mines, true, true);
            for (Map.Entry<State.Site, LinkedList<State.Site>> shortestPath : shortestPaths.entrySet()) {
                int length = lengthOfRoute(shortestPath.getValue());
                State.Site mine0 = shortestPath.getKey();
                if (!mine.equals(mine0) && !connectedMines.contains(new PairSites(mine, mine0)) &&
                        isSitesConnected(mine, mine0)) {
                    connectedMines.add(new PairSites(mine, mine0));
                }
                else if (!isSitesConnected(mine, mine0) && length < minLength && length > 1) {
                    minLength = length;
                    route = shortestPath.getValue();
                }
            }
        }
    }

    private void makeRoute1(State.Site site) {
        Map<State.Site, LinkedList<State.Site>> shortestPaths =
                BFS.shortestPathsBFS(site, state.getSites(), true, true);
        int maxLength = 1;
        int minNeutralLength = Integer.MAX_VALUE;
        for (Map.Entry<State.Site, LinkedList<State.Site>> shortestPath : shortestPaths.entrySet()) {
            int length = shortestPath.getValue().size();
            //int neutralLength = lengthOfRoute(shortestPath.getValue());
            if (length > maxLength) {
                maxLength = length;
                //minNeutralLength = neutralLength;
                route = shortestPath.getValue();
            }
        }
    }

    private boolean tryToMakeMove() {
        parity++;
        if (parity % 2 == 1) {
            from = null;
            if (!route.isEmpty()) from = route.poll();
            to = null;
            if (!route.isEmpty()) to = route.peek();
        } else {
            from = null;
            if (!route.isEmpty()) from = route.pollLast();
            to = null;
            if (!route.isEmpty()) to = route.peekLast();
        }

        if (from != null && to != null) {
            for (State.River river : state.getRivers()) {
                if (river.equals(new State.River(new State.Site(from.getId()), new State.Site(to.getId())))) {
                    if (river.getRiverState() == State.River.RiverState.Neutral) {
                        protocol.claimMove(from.getId(), to.getId());
                        return true;
                    } else if (river.getRiverState() == State.River.RiverState.Our) {
                        while (route.size() > 1) {
                            from = route.poll();
                            to = route.peek();
                            if (from.getNeighbors(true, false).contains(to)) {
                                for (State.River river1 : state.getRivers()) {
                                    if (river1.equals(new State.River(new State.Site(from.getId()), new State.Site(to.getId())))) {
                                        if (river1.getRiverState() == State.River.RiverState.Neutral) {
                                            protocol.claimMove(from.getId(), to.getId());
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }

    private boolean tryToMakeMove1() {
        from = null;
        if (!route.isEmpty()) from = route.poll();
        to = null;
        if (!route.isEmpty()) to = route.peek();

        if (from != null && to != null) {
            for (State.River river : state.getRivers()) {
                if (river.equals(new State.River(new State.Site(from.getId()), new State.Site(to.getId())))) {
                    if (river.getRiverState() == State.River.RiverState.Neutral) {
                        protocol.claimMove(from.getId(), to.getId());
                        return true;
                    } else if (river.getRiverState() == State.River.RiverState.Our) {
                        while (route.size() > 1) {
                            from = route.poll();
                            to = route.peek();
                            if (from.getNeighbors(true, false).contains(to)) {
                                for (State.River river1 : state.getRivers()) {
                                    if (river1.equals(new State.River(new State.Site(from.getId()), new State.Site(to.getId())))) {
                                        if (river1.getRiverState() == State.River.RiverState.Neutral) {
                                            protocol.claimMove(from.getId(), to.getId());
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }

    public boolean makeMove() {
        if (flag == 1) {
            if (tryToMakeMove()) return true;
        } else {
            if (tryToMakeMove1()) return true;
        }

        if (connectedMines.size() < (state.getMines().size() * (state.getMines().size() - 1)) / 2) {
            makeRouteMtoM();
            flag = 1;
            if (tryToMakeMove()) return true;
        }

        for (State.Site mine : state.getMines()) {
            if (mine.isFree()) {
                makeRoute1(mine);
                flag = 0;
                if (tryToMakeMove1()) return true;
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
                        makeRoute1(site);
                        flag = 0;
                        if (tryToMakeMove1()) return true;
                        break;
                    }
                    i++;
                }
            }
        }

        for (State.River river : state.getRivers()) {
            if (river.getRiverState() == State.River.RiverState.Neutral) {
                if (river.getSource().isOur() || river.getTarget().isOur()) {
                    protocol.claimMove(river.getSource().getId(), river.getTarget().getId());
                    return true;
                }
            }
        }

        for (State.River river : state.getRivers()) {
            if (river.getRiverState() == State.River.RiverState.Neutral) {
                protocol.claimMove(river.getSource().getId(), river.getTarget().getId());
                return true;
            }
        }

        protocol.passMove();
        return true;
    }
}
