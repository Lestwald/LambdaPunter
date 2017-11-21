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
                    for (Site site : river.sites) {
                        if (site.id != this.id) {
                            neighbors.add(site);
                        }
                    }
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Site site = (Site) o;
            if (id != site.id) return false;
            return isMine == site.isMine;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (isMine ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Site{" +
                    "id=" + id +
                    ", isMine=" + isMine +
                    ", rivers=" + rivers +
                    '}';
        }
    }

    static class River {
        private protocol.data.River river;

        enum RiverState {
            Neutral, Our, Enemy,
        }

        private RiverState riverState;
        private Set<Site> sites = new HashSet<>();

        River(protocol.data.River river) {
            this.river = river;
            riverState = RiverState.Neutral;
        }

        void addSite(Site site) {
            sites.add(site);
        }

        private void setOur() {
            riverState = RiverState.Our;
        }

        private void setEnemy() {
            riverState = RiverState.Enemy;
        }

        RiverState getRiverState() {
            return riverState;
        }

        protocol.data.River getRiver() {
            return river;
        }

        Set<Site> getSites() {
            return sites;
        }

        @Override
        public String toString() {
            return "River{" +
                    "river=" + river + "; " +
                    "riverState=" + riverState +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            River river1 = (River) o;

            if (river != null ? !river.equals(river1.river) : river1.river != null) return false;
            return riverState == river1.riverState;
        }

        @Override
        public int hashCode() {
            int result = river != null ? river.hashCode() : 0;
            result = 31 * result + (riverState != null ? riverState.hashCode() : 0);
            return result;
        }
    }

    private Set<Site> sites = new HashSet<>();
    private Set<Site> mines = new HashSet<>();
    private Set<River> rivers = new HashSet<>();
    private int myId = -1;

    public void init(Setup setup) {
        myId = setup.getPunter();

        for (protocol.data.River river : setup.getMap().getRivers()) {
            River river1 = new River(river);
            Site site1 = null;
            Site site2 = null;
            for (Site site : sites) {
                if (site.id == river.getSource()) site1 = site;
                if (site.id == river.getTarget()) site2 = site;
            }
            if (site1 == null) {
                site1 = new Site(river.getSource());
                sites.add(site1);
            }
            if (site2 == null) {
                site2 = new Site(river.getTarget());
                sites.add(site2);
            }
            if (setup.getMap().getMines().contains(site1.id)) {
                site1.setMine();
                mines.add(site1);
            }
            if (setup.getMap().getMines().contains(site2.id)) {
                site2.setMine();
                mines.add(site2);
            }
            site1.addRiver(river1);
            site2.addRiver(river1);
            river1.addSite(site1);
            river1.addSite(site2);
            rivers.add(river1);
        }
    }

    public void update(Claim claim) {
        if (claim.getPunter() == myId) {
            for (River river : rivers) {
                if (river.river.equals(new protocol.data.River(claim.getSource(), claim.getTarget()))) {
                    river.setOur();
                }
            }
        } else for (River river : rivers) {
            if (river.river.equals(new protocol.data.River(claim.getSource(), claim.getTarget()))) {
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
