package main;

import protocol.data.*;

import java.util.*;

public class State {
    static class Site {
        private int id;
        private boolean isMine;
        private boolean isOur;
        Set<River> rivers = new HashSet<>();

        Site(int id) {
            this.id = id;
            isMine = false;
            isOur = false;
        }

        void addRiver(River river) {
            rivers.add(river);
        }

        Set<Site> getNeighbors() {
            Set<Site> neighbors = new HashSet<>();
            for (River river : rivers) {
                if (river.getRiverState() == River.RiverState.Neutral) {
                    for (Site site : river.sites) {
                        if (site.id != this.id) {
                            neighbors.add(site);
                        }
                    }
                }
            }
            return neighbors;
        }

        public int getId() {
            return id;
        }

        void setMine() {
            isMine = true;
        }

        private void setOur() {
            isOur = true;
        }

        boolean isMine() {
            return isMine;
        }

        boolean isOur() {
            return isOur;
        }

        public Set<River> getRivers() {
            return rivers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Site site = (Site) o;

            if (id != site.id) return false;
            if (isMine != site.isMine) return false;
            return isOur == site.isOur;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (isMine ? 1 : 0);
            result = 31 * result + (isOur ? 1 : 0);
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

        public protocol.data.River getRiver() {
            return river;
        }

        @Override
        public String toString() {
            return "River{" +
                    "river=" + river +  "; " +
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

        public Set<Site> getSites() {
            return sites;
        }
    }

    private Set<Site> sites = new HashSet<>();
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
            if (setup.getMap().getMines().contains(site1.id)) site1.setMine();
            if (setup.getMap().getMines().contains(site2.id)) site2.setMine();
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
            for (Site site : sites) {
                if (site.id == claim.getSource() || site.id == claim.getSource()) site.setOur();
            }
        } else for (River river : rivers) {
            if (river.river.equals(new protocol.data.River(claim.getSource(), claim.getTarget()))) {
                river.setEnemy();
            }
        }
    }

    Set<Site> getSites() {
        return sites;
    }

    Set<River> getRivers() {
        return rivers;
    }

    int getMyId() {
        return myId;
    }
}
