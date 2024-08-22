import java.util.HashSet;
import java.util.stream.Collectors;

public class StateSnapshot {
    public int gold;
    public int safe;
    public int pit;
    public int wumpus;
    public int visited;
    public int pitExists;
    public int wumpusExists;
    public int frontier;
    public int pitFound;
    public int wumpusFound;
    public int valid;
    public int known;
    public HashSet<String> pitLocs;
    public HashSet<String> wumpusLoc;

    public StateSnapshot(int X, int Y, WumpusWorld trialWolrd) {
        WumpusCell cell = trialWolrd.cells[X][Y];
        pitFound = trialWolrd.pitFound;
        wumpusFound = trialWolrd.wumpusFound;
        pitLocs = (HashSet<String>) trialWolrd.pits.stream().map(String::new).collect(Collectors.toSet());
        wumpusLoc = (HashSet<String>) trialWolrd.wumpus.stream().map(String::new).collect(Collectors.toSet());
        valid = trialWolrd.valid;
        known = trialWolrd.known;

        gold = cell.gold;
        safe = cell.safe;
        pit = cell.pit;
        wumpus = cell.wumpus;
        visited = cell.visited;
        pitExists = cell.pitExists;
        wumpusExists = cell.wumpusExists;
        frontier = cell.frontier;
    }
}
