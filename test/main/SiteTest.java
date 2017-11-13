package main;

import org.junit.jupiter.api.Test;
import protocol.data.River;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SiteTest {
    @Test
    void getNeighbors() {
        State.Site site1 = new State.Site(1);
        State.Site site2 = new State.Site(2);
        State.Site site3 = new State.Site(3);
        State.Site site4 = new State.Site(4);
        State.Site site5 = new State.Site(5);
        State.Site site6 = new State.Site(6);
        State.Site site7 = new State.Site(7);
        State.Site site8 = new State.Site(8);

        State.River river12 = new State.River(new River(1, 2));
        State.River river13 = new State.River(new River(1, 3));
        State.River river27 = new State.River(new River(2, 7));
        State.River river34 = new State.River(new River(3, 4));
        State.River river45 = new State.River(new River(4, 5));
        State.River river46 = new State.River(new River(4, 6));
        State.River river47 = new State.River(new River(4, 7));
        State.River river58 = new State.River(new River(5, 8));
        State.River river78 = new State.River(new River(7, 8));

        river12.addSite(site1);
        river12.addSite(site2);
        river13.addSite(site1);
        river13.addSite(site3);
        river27.addSite(site2);
        river27.addSite(site7);
        river34.addSite(site3);
        river34.addSite(site4);
        river45.addSite(site4);
        river45.addSite(site5);
        river46.addSite(site4);
        river46.addSite(site6);
        river47.addSite(site4);
        river47.addSite(site7);
        river58.addSite(site5);
        river58.addSite(site8);
        river78.addSite(site7);
        river78.addSite(site8);

        site1.addRiver(river12);
        site1.addRiver(river13);
        site2.addRiver(river12);
        site2.addRiver(river27);
        site3.addRiver(river13);
        site3.addRiver(river34);
        site4.addRiver(river34);
        site4.addRiver(river45);
        site4.addRiver(river46);
        site4.addRiver(river47);
        site5.addRiver(river45);
        site5.addRiver(river58);
        site6.addRiver(river46);
        site7.addRiver(river27);
        site7.addRiver(river47);
        site7.addRiver(river78);
        site8.addRiver(river58);
        site8.addRiver(river78);

        Set<State.Site> expected1 = new HashSet<>();
        expected1.add(site2);
        expected1.add(site3);
        assertEquals(expected1, site1.getNeighbors());

        Set<State.Site> expected2 = new HashSet<>();
        expected2.add(site1);
        expected2.add(site7);
        assertEquals(expected2, site2.getNeighbors());

        Set<State.Site> expected4 = new HashSet<>();
        expected4.add(site3);
        expected4.add(site5);
        expected4.add(site6);
        expected4.add(site7);
        assertEquals(expected4, site4.getNeighbors());
    }

}