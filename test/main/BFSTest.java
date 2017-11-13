package main;

import org.junit.jupiter.api.Test;
import protocol.data.River;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BFSTest {
    @Test
    void shortestPathsBFS() {
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

        Set<State.Site> finishSites = new HashSet<>();
        finishSites.add(site1);
        finishSites.add(site2);
        finishSites.add(site3);
        finishSites.add(site4);
        finishSites.add(site5);
        finishSites.add(site6);
        finishSites.add(site7);
        finishSites.add(site8);

        Map<State.Site, LinkedList<State.Site>> result1 = BFS.shortestPathsBFS(site1, finishSites);

        Map<State.Site, List<State.Site>> expected1 = new HashMap<>();

        List<State.Site> list1_1 = new ArrayList<>();
        list1_1.add(site1);
        expected1.put(site1, list1_1);
        List<State.Site> list1_2 = new ArrayList<>();
        list1_2.add(site1);
        list1_2.add(site2);
        expected1.put(site2, list1_2);
        List<State.Site> list1_3 = new ArrayList<>();
        list1_3.add(site1);
        list1_3.add(site3);
        expected1.put(site3, list1_3);
        List<State.Site> list1_4 = new ArrayList<>();
        list1_4.add(site1);
        list1_4.add(site3);
        list1_4.add(site4);
        expected1.put(site4, list1_4);
        List<State.Site> list1_5 = new ArrayList<>();
        list1_5.add(site1);
        list1_5.add(site3);
        list1_5.add(site4);
        list1_5.add(site5);
        expected1.put(site5, list1_5);
        List<State.Site> list1_6 = new ArrayList<>();
        list1_6.add(site1);
        list1_6.add(site3);
        list1_6.add(site4);
        list1_6.add(site6);
        expected1.put(site6, list1_6);
        List<State.Site> list1_7 = new ArrayList<>();
        list1_7.add(site1);
        list1_7.add(site2);
        list1_7.add(site7);
        expected1.put(site7, list1_7);
        List<State.Site> list1_8 = new ArrayList<>();
        list1_8.add(site1);
        list1_8.add(site2);
        list1_8.add(site7);
        list1_8.add(site8);
        expected1.put(site8, list1_8);

        assertEquals(expected1, result1);
        
        
        Map<State.Site, LinkedList<State.Site>> result4 = BFS.shortestPathsBFS(site4, finishSites);

        Map<State.Site, List<State.Site>> expected4 = new HashMap<>();
        List<State.Site> list4_1 = new ArrayList<>();
        list4_1.add(site4);
        list4_1.add(site3);
        list4_1.add(site1);
        expected4.put(site1, list4_1);
        List<State.Site> list4_2 = new ArrayList<>();
        list4_2.add(site4);
        list4_2.add(site7);
        list4_2.add(site2);
        expected4.put(site2, list4_2);
        List<State.Site> list4_3 = new ArrayList<>();
        list4_3.add(site4);
        list4_3.add(site3);
        expected4.put(site3, list4_3);
        List<State.Site> list4_4 = new ArrayList<>();
        list4_4.add(site4);
        expected4.put(site4, list4_4);
        List<State.Site> list4_5 = new ArrayList<>();
        list4_5.add(site4);
        list4_5.add(site5);
        expected4.put(site5, list4_5);
        List<State.Site> list4_6 = new ArrayList<>();
        list4_6.add(site4);
        list4_6.add(site6);
        expected4.put(site6, list4_6);
        List<State.Site> list4_7 = new ArrayList<>();
        list4_7.add(site4);
        list4_7.add(site7);
        expected4.put(site7, list4_7);
        List<State.Site> list4_8 = new ArrayList<>();
        list4_8.add(site4);
        list4_8.add(site5);
        list4_8.add(site8);
        expected4.put(site8, list4_8);

        assertEquals(expected4, result4);

        
    }

}