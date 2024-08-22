import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.stream.Collectors;

public class WumpusWorld {
    private int[][] directions = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};
    public int valid = 1;
    public int wumpusFound = 0;
    public int pitFound = 0;
    public int pitX = 0;
    public int pitY = 0;
    public int known = 0;
    public int arrow = 1;
    public Character agentDirection = 'E';
    public boolean wumpusKilled = false;
    public HashSet<String> pits = new HashSet<>();
    public HashSet<String> wumpus = new HashSet<>();
    public HashMap<String, Integer> wumpusCandidates = new HashMap<>();
    public WumpusCell[][] cells = new WumpusCell[4][4];

    public WumpusWorld() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                cells[i][j] = new WumpusCell();
            }
        }
        cells[0][0].safe = 1;
        cells[0][0].visited = 0;
        cells[0][1].frontier = 1;
        cells[1][0].frontier = 1;
    }

    //If the agent is facing a possible wumpus cell, this function will return it's location, otherwise just return {-1,-1}
    public int[] huntWumpus(int agentX, int agentY, Character dir) {
        boolean sameRow = false;
        boolean sameCol = false;
        int targetX = -1;
        int targetY = -1;
        // iterate through possible wumpus locations found.
        for (String wstr : wumpusCandidates.keySet()) {
            String[] wLoc = wstr.split("-");

            int x = Integer.parseInt(wLoc[0]);
            int y = Integer.parseInt(wLoc[1]);
            // Agent and wumpus are on the same column.
            if (x == agentX) sameCol = true;
            // Agent and wumpus are on the same row.
            if (y == agentY) sameRow = true;
            // For some reason I haven't figured out the wumpus candidates not on the same row or column
            // gives slightly higher score. It requires careful tuning or new parameters for the utility function.
//                -----------------------
//               |     |WC   |     |     |
//               |     |     |     |     |
//                -----------------------
//               | WC  | <   |     |     |
//               |     |     |     |     |
//                -----------------------
//               |     |     |     |     |
//               |     |     |     |     |
//                -----------------------
//               |     |     |     |     |
//               |     |     |     |     |
//                -----------------------
            // So there are unknown#-choose-2 ways of putting the pit in the unknown world
            if ((x == agentX && ((dir == 'S' && y < agentY) || (dir == 'N' && y > agentY))) // the agent is now facing a wumpus candidate
                    || (y == agentY && ((dir == 'W' && x < agentX) || (dir == 'E' && x > agentX)))) {
                if(wumpus.contains(x + "-" + y)){ // the position of the wumpus is known, kill it.
                    targetX = x;
                    targetY = y;
                } else if (sameCol && sameRow) {
                    targetX = x;
                    targetY = y;
                    cells[x][y].wumpus -= 1; // reduce the probability of the wumpus, the position of the wumpus will be resolved later by resolveWumpus.
                }
            }
        }
        return new int[]{targetX, targetY};
    }

    // Kill the wumpus temporarily to calculate the utilities of worlds without living wumpus.
    public void killWumpus(int x, int y) {
        wumpusKilled = true;
        if (x > 0 && cells[x][y].pit == 0 && !pits.contains(x + "-" + y)) {
            cells[x][y].safe = 1;
        }
    }
    // Revive the wumpus once the utilities of worlds without living wumpus have been evaluated.
    public void reviveWumpus(int X, int Y) {
        wumpusKilled = false;
        arrow = 1;
        cells[X][Y].safe = -1;
    }
    // If there are no stench on a cell, the wumpus flags of cells nearby will be cleared.
    public void clearWumpus(ArrayList<int[]> neighbors) {
        for (int[] neighbor : neighbors) {
            int X = neighbor[0];
            int Y = neighbor[1];
            cells[X][Y].wumpusExists = -1;
            cells[X][Y].wumpus = 0;
        }
    }
    // Find out where the wumpus is
    public void resolveWumpus() {
        if (wumpusFound == 0) {
            int maxpitprobability = 0;
            Stack<String> wumpusstack = new Stack();

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (cells[i][j].wumpus > maxpitprobability && cells[i][j].safe != 1) {
                        wumpusstack.clear();
                        wumpusstack.push(i + "" + j);
                        maxpitprobability = (int) cells[i][j].wumpus;
                    } else if (cells[i][j].wumpus == maxpitprobability && cells[i][j].safe != 1) {
                        wumpusstack.push(i + "" + j);
                    }
                }
            }

            if (wumpusstack.size() == 1) {
                char position[] = wumpusstack.peek().toCharArray();
                int X = Character.getNumericValue(position[0]);
                int Y = Character.getNumericValue(position[1]);
                cells[X][Y].safe = -1;
                cells[X][Y].wumpusExists = 1;
                wumpusFound = 1;
                wumpus.add(X + "-" + Y);
            }
        }

        if (wumpusFound == 1) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (!wumpus.contains(String.valueOf(i) + "-" + String.valueOf(j))) {
                        cells[i][j].wumpus = 0;
                    }
                }
            }
        }
    }
    // If there are no breeze on a cell, the pit flags of cells nearby will be cleared.
    public void clearPit(ArrayList<int[]> neighbors) {
        for (int[] neighbor : neighbors) {
            int X = neighbor[0];
            int Y = neighbor[1];

            if (cells[X][Y].pit > 0 || cells[X][Y].pitExists != 0) {
                cells[X][Y].pitExists = 0;
                cells[X][Y].pit = 0;

                ArrayList<int[]> n = getNeighbors(X, Y);
                for (int[] n2 : n) {
                    if (cells[n2[0]][n2[1]].visited == 1 && cells[n2[0]][n2[1]].breeze == 1) {
                        // Pit status changed, resolve pit again for each neighbor.
                        resolvePit(X, Y, getNeighbors(n2[0], n2[1]));
                    }
                }
            }
            if (cells[X][Y].visited == 1 && cells[X][Y].breeze == 1) resolvePit(X, Y, getNeighbors(X, Y));
        }
    }
    // Find out where the pits are
    public void resolvePit(int X, int Y, ArrayList<int[]> neighbors) {
        if (pitFound > 2 || (cells[X][Y].pitExists == 0 && cells[X][Y].pit == 4)) {
            valid = -1;
        } else if (pitFound < 2) {
            Stack<String> pitstack = new Stack();
            for (int[] neighbor : neighbors) {
                if (cells[neighbor[0]][neighbor[1]].pitExists == 1 && cells[neighbor[0]][neighbor[1]].safe != 1) {
                    pitstack.push(Integer.toString(neighbor[0]) + Integer.toString(neighbor[1]));
                }
            }
            if (pitstack.size() == 1) { // if 3 out of 4 neighbors have no pit, the one with pit flag must be a pit.
                char position[] = pitstack.peek().toCharArray();
                int pX = Character.getNumericValue(position[0]);
                int pY = Character.getNumericValue(position[1]);
                cells[pX][pY].safe = -1;
                if (pitX != pX || pitY != pY) {
                    pitFound += 1;
                    pits.add(String.valueOf(position[0]) + "-" + String.valueOf(position[1]));
//                    System.out.println("pit found:" + String.valueOf(position[0]) + "-" + String.valueOf(position[1]));

                    pitX = pX;
                    pitY = pY;
                    for (int[] neighbor : getNeighbors(pX, pY)) {
                        if (cells[neighbor[0]][neighbor[1]].wumpusExists == -1) {
                            cells[neighbor[0]][neighbor[1]].safe = 1;
                            cells[neighbor[0]][neighbor[1]].pit = 0;
                        }
                    }
                }
            }
        } else { // pitFound == 2
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (!pits.contains(String.valueOf(i) + "-" + String.valueOf(j))) {
                        cells[i][j].pitExists = 0;
                        cells[i][j].pit = 0;
                    }
                }
            }
        }
    }

    public boolean isCellSafe(int X, int Y) {
        WumpusCell cell = cells[X][Y];
        if (cell.safe != -1 && (cell.safe == 1
                || ((cell.wumpus == 0 || (wumpusFound == 1 && !wumpus.contains(X + "-" + Y)))
                && (cell.pit == 0 || (pitFound == 2 && !pits.contains(X + "-" + Y)))))) {
            return true;
        }
        return false;
    }

    public ArrayList<int[]> getNeighbors(int X, int Y) {
        ArrayList<int[]> neighbors = new ArrayList<>();
        for (int[] d : directions) {
            int x = X + d[0];
            int y = Y + d[1];
            if (x >= 0 && x < 4 && y >= 0 && y < 4) {
                neighbors.add(new int[]{x, y});
            }
        }
        return neighbors;
    }

    public ArrayList<ArrayList<int[]>> getOneJumps(int X, int Y) {
        int[][] directions = new int[][]{{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        ArrayList<ArrayList<int[]>> ret = new ArrayList<>();
        for (int[] direction : directions) {
            int x = X + direction[0];
            int y = Y + direction[1];
            if (x >= 0 && x < 4 && y >= 0 && y < 4) {
                ArrayList<int[]> jumps = new ArrayList<>();
                for (int[] d : directions) {
                    int x2 = x + d[0];
                    int y2 = y + d[1];
                    if (!(x2 == X && y2 == Y) && x2 >= 0 && x2 < 4 && y2 >= 0 && y2 < 4)
                        jumps.add(new int[]{x, y, x2, y2});
                }
                ret.add(jumps);
            }
        }
        return ret;
    }

    // Update the wumpus world model based on the current percepts
    public void updateWumpusWorld(int X, int Y, int configuration) {
        if (cells[X][Y].visited != 1) {
            known += 1;
        }

        ArrayList<int[]> neighbors = getNeighbors(X, Y);
        for (int[] neighbor : neighbors) {
            int nx = neighbor[0];
            int ny = neighbor[1];
            String posStr = String.valueOf(X) + "-" + String.valueOf(Y);
            cells[nx][ny].frontier = !wumpus.contains(posStr)
                    && !pits.contains(posStr) && cells[nx][ny].visited != 1 ? 1 : 0;
        }
        switch (configuration) {
            // For case 0 - No breeze or stench observed, Hence neighboring cells can be tagged as safe
            case 0:
                for (int[] neighbor : neighbors) {
                    cells[neighbor[0]][neighbor[1]].safe = 1;
                }
                clearPit(neighbors);
                clearWumpus(neighbors);
                wumpusCandidates.remove(X + "-" + Y);
                break;
            // For case 1 - Both breeze and stench observed, Update wumpus and pit probabilities
            // Then based on Wummpus and pit Probabilities try to finalize the position of wumpus and pit positions
            // By calling resolveWumpus and resolvePit functions
            case 1:
                cells[X][Y].breeze = 1;
                cells[X][Y].stench = 1;
                for (int[] neighbor : neighbors) {
                    int nX = neighbor[0];
                    int nY = neighbor[1];
                    if (cells[nX][nY].pitExists == 1) {
                        cells[nX][nY].pit += 1;
                    }
                    cells[nX][nY].wumpus += 1;
                    wumpusCandidates.put(nX + "-" + nY, cells[nX][nY].wumpus);
                }
                resolveWumpus();
                resolvePit(X, Y, neighbors);
                break;
            // For case 2 - Only stench observed, Update wumpus probabilities
            // Then based on Wummpus probability try to finalize the position of wumpus positions
            // By calling resolveWumpus function
            // Also since breeze not observed - eliminate possibility of breeze in neighboring cells
            // By  calling clearPit function
            case 2:
                cells[X][Y].stench = 1;
                for (int[] neighbor : neighbors) {
                    int nX = neighbor[0];
                    int nY = neighbor[1];
                    cells[nX][nY].wumpus += 1;
                    wumpusCandidates.put(nX + "-" + nY, cells[nX][nY].wumpus);
                }
                resolveWumpus();
                clearPit(neighbors);
                break;
            // For case 3 - Only breeze observed, Update pit probabilities
            // Then based on Pit Probabilities try to finalize the position of pit
            // By calling resolvePit functions
            case 3:
                cells[X][Y].breeze = 1;
                for (int[] neighbor : neighbors) {
                    if (cells[neighbor[0]][neighbor[1]].pitExists == 1)
                        cells[neighbor[0]][neighbor[1]].pit += 1;
                }
                resolvePit(X, Y, neighbors);
                clearWumpus(neighbors);
                break;
            // Case - 4: Scream Observed along with Breeze
            // Since wumpus is dead, mark all cells as wumpus free by changing wumpus probability to 0
            // Then since breeze is observed, update pit probabilities
            // and then try finalizing pit position
            // by calling resolvePit
            case 4:
                wumpusKilled = true;
                wumpusCandidates = new HashMap<>();
                cells[X][Y].breeze = 1;
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (cells[i][j].wumpusExists == 1) {
                            cells[i][j].safe = 0;
                        }
                        cells[i][j].wumpus = 0;
                        cells[i][j].wumpusExists = -1;
//                        cells[i][j].wumpusExists = 0;
                    }
                }
                for (int[] neighbor : neighbors) {
                    if (cells[neighbor[0]][neighbor[1]].pitExists == 1)
                        cells[neighbor[0]][neighbor[1]].pit += 1;
                }
                resolvePit(X, Y, neighbors);
                break;
            // Case - 5: Only scream observed
            // Mark all cells as Wumpus free, since it is dead by making wumpus probability to 0
            // Since breeze is also not observed, mark neighboring cells as safe
            case 5:
                wumpusKilled = true;
                wumpusCandidates = new HashMap<>();
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (cells[i][j].wumpusExists == 1) {
                            cells[i][j].safe = 0;
                            cells[i][j].wumpusExists = -1;
                        }
//                        cells[i][j].wumpusExists = 0;
                        cells[i][j].wumpus = 0;
                    }
                }
                clearPit(neighbors);
                for (int[] neighbor : neighbors) {
                    cells[neighbor[0]][neighbor[1]].safe = 1;
                }
                break;
        }
        cells[X][Y].visited = 1;
        cells[X][Y].frontier = 0;
    }

    // Try different frontier configuration and check if this configuration is valid while searching
    public void tryFrontierConfiguration(int X, int Y, int configuration) {
        String posStr = String.valueOf(X) + "-" + String.valueOf(Y);
        switch (configuration) {
            case 0:
                // safe
                if (pits.contains(posStr) ||
                        (wumpus.contains(posStr) && !wumpusKilled) || cells[X][Y].safe == -1) {
                    valid = -1;
                }

                cells[X][Y].safe = 1;
                cells[X][Y].pit = 0;
                cells[X][Y].wumpus = 0;
                break;
            case 1:
                // both pit and wumpus
                if (wumpusKilled || cells[X][Y].safe == 1 || cells[X][Y].pitExists == 0 || cells[X][Y].wumpusExists == -1) {
                    valid = -1;
                }
                cells[X][Y].safe = -1;
                cells[X][Y].pit = 4;
                //check if found before
                if (!pits.contains(posStr)) {
                    pitFound += 1;
                }

                if (cells[X][Y].wumpus == 0 && !wumpus.contains(posStr)) {
                    valid = -1;
                }
                cells[X][Y].wumpus = 4;
                if (!wumpus.contains(posStr)) {
                    wumpusFound += 1;
                }
                break;
            case 2:
                // wumpus only
                cells[X][Y].pit = 0;
                if (pits.contains(posStr)) {
                    valid = -1;
                }

                if (wumpusKilled || cells[X][Y].safe == 1 || cells[X][Y].wumpusExists == -1) {
                    valid = -1;
                }
                cells[X][Y].safe = -1;
                cells[X][Y].wumpus = 4;
                if (!wumpus.contains(posStr)) {
                    wumpusFound += 1;
                }
                break;
            case 3:
                // pit only
                if (cells[X][Y].pitExists == 0) {
                    valid = -1;
                }
                cells[X][Y].safe = -1;
                cells[X][Y].pit = 4;
                if (!pits.contains(posStr)) {
                    pitFound += 1;
                }
                if (pitFound > 2) valid = -1;

                cells[X][Y].wumpus = 0;
                if ((wumpus.contains(posStr) && !wumpusKilled)) {
                    valid = -1;
                }
                break;
        }
        if (wumpusFound > 1 || pitFound > 2) valid = -1;
    }

    public void backtrack(int X, int Y, StateSnapshot snap) {
        pitFound = snap.pitFound;
        wumpusFound = snap.wumpusFound;
        pits = snap.pitLocs;
        wumpus = snap.wumpusLoc;
        valid = snap.valid;
        known = snap.known;

        cells[X][Y].safe = snap.safe;
        cells[X][Y].pit = snap.pit;
        cells[X][Y].wumpus = snap.wumpus;
        cells[X][Y].visited = snap.visited;
        cells[X][Y].pitExists = snap.pitExists;
        cells[X][Y].wumpusExists = snap.wumpusExists;
        cells[X][Y].frontier = snap.frontier;
    }
}

