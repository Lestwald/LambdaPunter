package main;

import protocol.data.*;

import java.util.*;

public class State {
    static class Site {
        private int id;
        private boolean isMine;
        Set<River> rivers = new HashSet<>();

        Site(int id) {
            this.id = id;
            isMine = false;
        }

        void addRiver(River river) {
            rivers.add(river);
        }

        Set<Site> getNeighbors(boolean neutralRivers, boolean ourRivers) {
            Set<Site> neighbors = new HashSet<>();
            for (River river : rivers) {
                if (neutralRivers && river.getRiverState() == River.RiverState.Neutral ||
                        (ourRivers && river.getRiverState() == River.RiverState.Our)) {
                    if (river.getSource() != this) neighbors.add(river.getSource());
                    else if (river.getTarget() != this) neighbors.add(river.getTarget());
                }
            }
            return neighbors;
        }

        int getId() {
            return id;
        }

        void setMine() {
            isMine = true;
        }

        boolean isMine() {
            return isMine;
        }

        boolean isOur() {
            for (River river : rivers) {
                if (river.getRiverState() == River.RiverState.Our) return true;
            }
            return false;
        }

        boolean isFree() {
            for (River river : rivers) {
                if (river.getRiverState() == River.RiverState.Neutral) return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Site{" +
                    "id=" + id + "; " +
                    "isMine=" + isMine + "; " +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Site site = (Site) o;
            return id == site.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    static class River {
        enum RiverState {
            Neutral, Our, Enemy,
        }

        private RiverState riverState;
        private Site source;
        private Site target;

        River(Site source, Site target) {
            this.source = source;
            this.target = target;
            riverState = RiverState.Neutral;
        }

        private void setOur() {
            riverState = RiverState.Our;
        }

        private void setEnemy() {
            riverState = RiverState.Enemy;
        }

        public void setSource(Site source) {
            this.source = source;
        }

        public void setTarget(Site target) {
            this.target = target;
        }

        RiverState getRiverState() {
            return riverState;
        }

        Site getSource() {
            return source;
        }

        Site getTarget() {
            return target;
        }

        @Override
        public String toString() {
            return "River{" +
                    "source=" + source + "; " +
                    "target=" + target + "; " +
                    "riverState=" + riverState +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            River river = (River) o;

            return source.getId() == river.source.getId() && target.getId() == river.target.getId() ||
                    source.getId() == river.target.getId() && target.getId() == river.source.getId();
        }

        @Override
        public int hashCode() {
            int result = source.hashCode();
            result = 31 * result + target.hashCode();
            return result;
        }
    }

    private Set<Site> sites = new HashSet<>();
    private Set<Site> mines = new HashSet<>();
    private Set<River> rivers = new HashSet<>();
    private int myId = -1;

    public void init(Setup setup) {
        myId = setup.getPunter();

        for (protocol.data.River river1 : setup.getMap().getRivers()) {
            Site source = new Site(river1.getSource());
            Site target = new Site(river1.getTarget());
            River river = new River(source, target);

            for (Site site : sites) {
                if (site.id == river.getSource().id) {
                    source = site;
                    river.setSource(source);
                }
                if (site.id == river.getTarget().id) {
                    target = site;
                    river.setTarget(target);
                }
            }

            if (!sites.contains(source)) sites.add(source);
            if (!sites.contains(target)) sites.add(target);

            if (setup.getMap().getMines().contains(source.id)) {
                source.setMine();
                mines.add(source);
            }
            if (setup.getMap().getMines().contains(target.id)) {
                target.setMine();
                mines.add(target);
            }
            target.addRiver(river);
            source.addRiver(river);
            rivers.add(river);
        }
    }

    public void update(Claim claim) {
        if (claim.getPunter() == myId) {
            for (River river : rivers) {
                if (river.equals(new River(new Site(claim.getSource()), new Site(claim.getTarget())))) {
                    river.setOur();
                }
            }
        } else for (River river : rivers) {
            if (river.equals(new River(new Site(claim.getSource()), new Site(claim.getTarget())))) {
                river.setEnemy();
            }
        }
    }

    Set<Site> getMines() {
        return mines;
    }

    Set<Site> getSites() {
        return sites;
    }

    Set<River> getRivers() {
        return rivers;
    }
}
