package main;

import org.junit.jupiter.api.Test;

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

        State.River river12 = new State.River(site1, site2);
        State.River river13 = new State.River(site1, site3);
        State.River river27 = new State.River(site2, site7);
        State.River river34 = new State.River(site3, site4);
        State.River river45 = new State.River(site4, site5);
        State.River river46 = new State.River(site4, site6);
        State.River river47 = new State.River(site4, site7);
        State.River river58 = new State.River(site5, site8);
        State.River river78 = new State.River(site7, site8);


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
        assertEquals(expected1, site1.getNeighbors(true, false,false));

        Set<State.Site> expected2 = new HashSet<>();
        expected2.add(site1);
        expected2.add(site7);
        assertEquals(expected2, site2.getNeighbors(true,false,false));

        Set<State.Site> expected4 = new HashSet<>();
        expected4.add(site3);
        expected4.add(site5);
        expected4.add(site6);
        expected4.add(site7);
        assertEquals(expected4, site4.getNeighbors(true,false,false));
    }

}