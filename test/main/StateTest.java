package main;

import org.junit.jupiter.api.Test;
import protocol.data.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class StateTest {
    @Test
    void init() {
        List<Site> sites = new ArrayList<>();
        sites.add(new Site(0, 0.0, 0.0));
        sites.add(new Site(1, 1.0, 1.0));
        sites.add(new Site(2, 2.0, 0.0));

        List<River> rivers = new ArrayList<>();
        rivers.add(new River(0, 1));
        rivers.add(new River(0, 2));
        rivers.add(new River(1, 2));

        List<Integer> mines = new ArrayList<>();
        mines.add(1);

        Map map = new Map(sites, rivers, mines);
        Setup setup = new Setup(0, 1, map, null);
        State state = new State();
        state.init(setup);

        Set<State.Site> sites1 = new HashSet<>();
        State.Site site0 = new State.Site(0);
        State.Site site1 = new State.Site(1);
        site1.setMine();
        State.Site site2 = new State.Site(2);

        sites1.add(site0);
        sites1.add(site1);
        sites1.add(site2);

        Set<State.River> rivers1 = new HashSet<>();
        State.River river01 = new State.River(site0, site1);
        State.River river02 = new State.River(site0, site2);
        State.River river12 = new State.River(site1, site2);

        rivers1.add(river01);
        rivers1.add(river02);
        rivers1.add(river12);

        site0.addRiver(river01);
        site0.addRiver(river02);
        site1.addRiver(river01);
        site1.addRiver(river12);
        site2.addRiver(river02);
        site2.addRiver(river12);

        assertEquals(rivers1, state.getRivers());
        assertEquals(sites1, state.getSites());
    }
}