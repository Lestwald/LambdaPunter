package main;

import java.util.*;

class BFS {
    static  Map<State.Site, LinkedList<State.Site>> shortestPathsBFS (State.Site startSite, Set<State.Site> finishSites) {
        Queue<State.Site> queue = new LinkedList<>();
        Set<State.Site> visited = new HashSet<>();
        Map<State.Site, State.Site> parentSites = new HashMap<>();
        Map<State.Site, LinkedList<State.Site>> shortestPaths = new HashMap<>();
        queue.add(startSite);
        visited.add(startSite);
        while (!queue.isEmpty()) {
            State.Site next = queue.poll();
            for (State.Site neighbor : next.getNeighbors(true, true, false)) {
                if (visited.contains(neighbor)) continue;
                visited.add(neighbor);
                parentSites.put(neighbor, next);
                queue.add(neighbor);
            }
            if (finishSites.contains(next)) {
                State.Site site = next;
                LinkedList<State.Site> shortestPath = new LinkedList<>();
                while (site != null) {
                    shortestPath.add(site);
                    site = parentSites.get(site);
                }
                Collections.reverse(shortestPath);
                if (!pathExists(shortestPath)) shortestPaths.put(next, shortestPath);
            }
        }
        return shortestPaths;
    }

    static private boolean pathExists(LinkedList<State.Site> path) {
        Queue<State.Site> path1 = new LinkedList<>(path);
        while (path1.size() > 1) {
            State.Site site1 = path1.poll();
            State.Site site2 = path1.peek();
            if (site1.getNeighbors(true, false, false).contains(site2)) return false;
        }
        return true;
    }
}
