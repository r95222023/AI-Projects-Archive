import java.util.*;

public class Model {
    private Cell[][] world;
    // the current direction of agent.
    private String agentDirection;
    // current and next agent location
    private int[] agLoc, nextLoc;
    private int prevAction;
    private int[][] directions = {{-1, 0}, {0, 1}, {0, -1}, {1, 0}};
    // record wumpus locations found.
    private HashSet<Utils.Coordinates> wumpusLoc = new HashSet<>();
    // record pit locations found.
    private HashSet<Utils.Coordinates> pitLoc = new HashSet<>();
    // show if the arrow was shot.
    public boolean shot = false;
    // show if the wumpus was killed.
    private boolean wumpusKilled = false;
    // record the steps of agent without moving forward.
    public int visited = 0;
    private int noMoveStep = 0;
    public boolean firstStep = true;

    public Model() {
        // Initialize the 4x4 world
        world = new Cell[Utils.worldSize][Utils.worldSize];
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                world[i][j] = new Cell();
            }
        }
        // Assign the initial conditions
        agentDirection = "E";
        agLoc = new int[]{world.length - 1, 0}; // the world start with an agent at South-West corner.
        world[agLoc[0]][agLoc[1]].setVisited(true);
        visited++;
        nextLoc = new int[]{-1, -1};
        prevAction = Action.NO_OP;
    }

    // detect if the agent is spinning around at a cell.
    public boolean stuck() {
        noMoveStep++;
        return noMoveStep > 5;
    }

    // update the agent based on the action made during previous step.
    public void updateModelOnAction() {
        switch (prevAction) {
            case 1: //move forward
                updateAgentLoc();
                break;
            case 2: //turn right
                updateAgentDirection(0);
                break;
            case 3: //turn left
                updateAgentDirection(1);
                break;
            case 4: //grab gold, game ends
                break;
            case 5: //shoot
                //shoot the wumpus
                break;
            case 6: //no-op
                //do nothing
                break;
        }
    }

    // update the world based on the current percepts.
    public void updateModelOnPercept(boolean breeze, boolean stench, boolean bump, boolean scream, boolean glitter) {
        Cell currCell = world[agLoc[0]][agLoc[1]];
        HashMap flags = currCell.getFlags();
        if (scream) {
            wumpusKilled = true;
        }

        // asign pit flags and clear conflicting pit flags based on current known world.
        if (breeze) {
            resolvePit();
            if (!currCell.isVisited()) {
                flags.put(Utils.breeze, 1);
                assignFlags(Utils.pit, 1, false);
            }
        }
        if (stench) {
            // asign wumpus flags and return the wumpus location if found one.
            int[] wLoc = resolveWumpus();
            //if no overlapping wumpus location is returned, then assign wumpus flag to all adjacent cells..
            if (wLoc[0] == -1) {
                if (!currCell.isVisited()) {
                    flags.put(Utils.stench, 1);
                    assignFlags(Utils.wumpus, 1, false);
                }
            } else {
                // assign Ok flags to all adjacent cells other than the wumpus loc, if no pit flag is found
                assignClearFlags(wLoc);
            }
        }

        // if no stench or breeze, then the cell and the adjacent cells are Ok.
        currCell.setCellOK(true);
        if (!breeze && !stench)
            assignFlags(null, null, true);
        if (!currCell.isVisited()) {
            currCell.setVisited(true);
            visited++;
        }

        if (breeze || stench) {
            world = resolveWorld();
        }
    }

    // turn toward the assigned location and avoid turning toward the wall.
    public int findDirection(int[] nextCell) {
        int r = agLoc[0];
        int c = agLoc[1];
        int nX = nextCell[0];
        int nY = nextCell[1];
        switch (agentDirection) {
            case "N":
                // c == 0 means that if the agent turn left it will bump the wall so turn right instead
                // nY > c means that next location is on the right hand side, so turn right.
                if (nY > c || c == 0) {
                    return 2;
                }
                return 3;
            case "S":
                // similar to 'N' case.
                if (nY < c || c == world[0].length - 1) {
                    return 2;
                }
                return 3;
            case "E":
                if (nX > r || r == 0) {
                    return 2;
                }
                return 3;
            case "W":
                if (nX < r || r == world[0].length - 1) {
                    return 2;
                }
                return 3;
        }
        return 3;
    }

    // check the agent to see if it is facing a wumpus.
    public boolean isFacingWumpus() {
        // only one arrow, so the agent won't need to shoot the arrow again.
        if (shot) return false;
        int r = agLoc[0];
        int c = agLoc[1];
        // iterate though wumpus locations found.
        for (Utils.Coordinates wLoc : wumpusLoc) {
            int x = wLoc.getX();
            int y = wLoc.getY();
            // agent and wumpus are on the same row.
            if (x == r) {
                switch (agentDirection) {
                    // facing a wumpus.
                    case "E":
                        // shoot the wumpus
                        return y > c;
                    case "W":
                        return y < c;
                }
            }
            if (y == c) {
                switch (agentDirection) {
                    case "N":
                        return x > r;
                    case "S":
                        return x < r;
                }
            }
        }
        return false;
    }

    private void assignFlags(String obj, Integer flag, boolean cellOk) {
        // assign flags to all adjacent cells adjacent.
        for (int[] direction : directions) {
            int nextX = agLoc[0] + direction[0];
            int nextY = agLoc[1] + direction[1];
            if (nextY < world[0].length && nextY >= 0 && nextX < world.length && nextX >= 0 && !world[nextX][nextY].isCellOK()) {
                if (cellOk) {
                    // if the cell is Ok, remove all pit and wumpus flags
                    HashMap<String, Integer> flags = world[nextX][nextY].getFlags();
                    if (!world[nextX][nextY].isCellOK()) {
                        world[nextX][nextY].setCellOK(true);
                        world[nextX][nextY].updateFlags(Utils.okNeighbor, 1);
                    }
                    flags.put(Utils.wumpus, 0);
                    flags.put(Utils.pit, 0);
                } else
                    world[nextX][nextY].updateFlags(obj, flag);
            }
        }
    }

    private void assignClearFlags(int[] loc) {
        // if no flags are set, assign the adjacent cells other than wLoc location as safe cells
        for (int[] direction : directions) {
            int nextX = agLoc[0] + direction[0];
            int nextY = agLoc[1] + direction[1];
            if (nextY < world[0].length && nextY >= 0 && nextX < world.length && nextX >= 0 && !world[nextX][nextY].isCellOK()
                    && (nextX != loc[0] && nextY != loc[1])) {
                if (!(world[nextX][nextY].getFlags().get(Utils.pit) > 0 ||
                        world[nextX][nextY].getFlags().get(Utils.wumpus) > 0))
                    world[nextX][nextY].setCellOK(true);
            }
        }
    }

    private int[] resolveWumpus() {
        int[] loc = new int[]{-1, -1};
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                // the cell was marked as possible wumpus location
                if (world[i][j].getFlags().get(Utils.wumpus) > 0) {
                    // remove the flag if the wumpus is killed.
                    if (wumpusKilled && !(world[i][j].getFlags().get(Utils.pit) > 0)) {
                        world[i][j].setCellOK(true);
                    }
                    // if flag >2, it has to be a wumpus there since there is only one wumpus and
                    // at least three adjacent cells are stenchy therefore the only place wumpus can be is this cell.
                    if (world[i][j].getFlags().get(Utils.wumpus) > 2) {
                        for (int[] direction : directions) {
                            int nextX = i + direction[0];
                            int nextY = j + direction[1];

                            if (nextX >= 0 && nextX < world.length && nextY >= 0 && nextY < world[0].length
                                    && world[nextX][nextY].isVisited() && !(world[nextX][nextY].getFlags().get(Utils.pit) > 0)) {
                                world[nextX][nextY].setCellOK(true);
                            }
                        }
                    }
                    // candidate wumpus is found(~50% accuracy), shoot arrow toward this location gives higher scores (increasing about 6 points).
                    if (isNeighborCell(i, j)) {
                        loc[0] = i;
                        loc[1] = j;
                        wumpusLoc.add(new Utils.Coordinates(i, j));
                        world[i][j].getFlags().put(Utils.wumpus, Utils.wumpusCellFlag);
                    } else {
                        // remove the flag if a wumpus flag was set to a cell which is not adjacent to current
                        // stenchy position since there is only one wumpus.
                        world[i][j].getFlags().put(Utils.wumpus, 0);
                        if (!(world[i][j].getFlags().get(Utils.pit) > 0)) {
                            world[i][j].setCellOK(true);
                        }
                    }
                }
            }
        }
        return loc;
    }

    private void resolvePit() {
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                // the pit is resolved (Utils.pitCellFlag==4) or all adjacent cell are breezy, then this cell must be a pit.
                if (world[i][j].getFlags().get(Utils.pit) > 3) {
                    pitLoc.add(new Utils.Coordinates(i, j));
                    for (int[] direction : directions) {
                        int nextX = i + direction[0];
                        int nextY = j + direction[1];

                        if (nextX >= 0 && nextX < world.length && nextY >= 0 && nextY < world[0].length &&
                                !pitLoc.contains(new Utils.Coordinates(nextX, nextY))) {
                            if (wumpusLoc.size() == Utils.wumpusN && !wumpusLoc.contains(new Utils.Coordinates(nextX, nextY))) {
                                world[nextX][nextY].setCellOK(true);
                            } else if (!(world[nextX][nextY].getFlags().get(Utils.wumpus) > 0)) {
                                world[nextX][nextY].setCellOK(true);
                            }
                        }
                    }
                }
            }
        }
    }

    private Cell[][] resolveWorld() {
        // search every possible pit and wumpus locations combinations,
        Set<Utils.Coordinates> visited = new HashSet<>();
        List<int[]> wCandidates = new ArrayList<>();
        List<int[]> pCandidates = new ArrayList<>();
        Cell[][] trialWorld = new Cell[world.length][world[0].length];
        List<Set<Utils.Coordinates>> result = new ArrayList<>();
        int M = world.length;
        int N = world[0].length;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                trialWorld[i][j] = world[i][j].copy();
                HashMap<String, Integer> flags = world[i][j].getFlags();
                int wFlag = flags.get(Utils.wumpus);
                int pFlag = flags.get(Utils.pit);
                if (wFlag > 0 && wFlag != Utils.wumpusCellFlag) {
                    wCandidates.add(new int[]{i, j});
                }
                if (pFlag > 0 && pFlag != Utils.pitCellFlag) {
                    pCandidates.add(new int[]{i, j});
                }
            }
        }


        if (pCandidates.size() > 0) {
            Set<Utils.Coordinates> comb = new HashSet<>();
            findPit(trialWorld, pCandidates, 0, comb, result);
            if(result.size() > 0 ){
                for (int[] p : pCandidates) {
                    boolean excluded = true;
                    for (Set<Utils.Coordinates> coordinates : result) {
                        if (coordinates.contains(new Utils.Coordinates(p[0], p[1]))) {
                            excluded = false;
                        }
                    }

                    if (excluded) {
                        // rule out the cell that leads to contradiction if there is a pit in it.
                        world[p[0]][p[1]].getFlags().put(Utils.pit, 0);
                        if(world[p[0]][p[1]].getFlags().get(Utils.wumpus) <= 0){
                            world[p[0]][p[1]].setCellOK(true);
                        }
                    }
                }
            }
        }
        return world;
    }

    // use backtracking algorithm to try every possible pit combinations recursively.
    private void findPit(Cell[][] trialWorld, List<int[]> pCandidates, int i,
                         Set<Utils.Coordinates> comb, List<Set<Utils.Coordinates>> result) {
        int pCandSize = pCandidates.size();

        if (comb.size() == pCandSize) {
            Set<Utils.Coordinates> copy = new HashSet<>();
            copy.addAll(comb);
            result.add(copy);
        }
        if (i == pCandSize) {
            return;
        }

        int[] cand = pCandidates.get(i);
        int X = cand[0];
        int Y = cand[1];
        int oldFlag = trialWorld[X][Y].getFlags().get(Utils.pit);
        if (validateComb(trialWorld, X, Y, Utils.pit, comb)) {
            // include i th candidate or not (k==0 include, k==1 don't include)
            for (int k = 0; k < 2; k++) {
                // mark the flag for a combination.
                trialWorld[X][Y].getFlags().put(Utils.pit, k == 0 ? Utils.pitCellFlag : oldFlag);
                comb.add(new Utils.Coordinates(X, Y));
                // try next level combinations
                findPit(trialWorld, pCandidates, i + 1, comb, result);
                // unmark the flag (backtrack) for next combination
                trialWorld[X][Y].getFlags().put(Utils.pit, oldFlag);
                comb.remove(new Utils.Coordinates(X, Y));
            }
        }
    }

    // validate combination
    private boolean validateComb(Cell[][] trialWorld, int x, int y, String key, Set<Utils.Coordinates> comb) {
        if (comb.size() > (key.equals(Utils.pit) ? Utils.wumpusN : Utils.pitN) || trialWorld[x][y].isVisited()) {
            return false;
        }
        String flagStr = key.equals(Utils.wumpus) ? Utils.stench : Utils.breeze;
        for (int[] direction : directions) {
            int nX = x + direction[0];
            int nY = y + direction[1];
            if (trialWorld[x][y].getFlags().get(key) > 0 && nX >= 0 && nX < world.length && nY >= 0 && nY < world[0].length) {
                if (trialWorld[nX][nY].isVisited() && trialWorld[nX][nY].getFlags().get(flagStr) == 0) {
                    return false;
                }
            }
        }
        return true;
    }


    // check if the given cell is a neighbor of agent's location
    private boolean isNeighborCell(int r, int c) {
        return (agLoc[0] + 1 == r && agLoc[1] == c) || (agLoc[0] - 1 == r && agLoc[1] == c) ||
                (agLoc[0] == r && agLoc[1] + 1 == c) || (agLoc[0] == r && agLoc[1] - 1 == c);
    }

    // update agent's direction
    private void updateAgentDirection(int direction) {
        //Update the agent direction, based on the Turn action..
        switch (agentDirection) {
            case "N":
                if (direction == 0) { //right
                    agentDirection = "E";
                } else { //left
                    agentDirection = "W";
                }
                break;
            case "S":
                if (direction == 0) { //right
                    agentDirection = "W";
                } else { //left
                    agentDirection = "E";
                }
                break;
            case "E":
                if (direction == 0) { //right
                    agentDirection = "S";
                } else { //left
                    agentDirection = "N";
                }
                break;
            case "W":
                if (direction == 0) { //right
                    agentDirection = "N";
                } else { //left
                    agentDirection = "S";
                }
                break;
        }

    }

    // update agent's location when move forward
    private void updateAgentLoc() {
        //Update the agent location for the Forward action..
        if (!path.isEmpty() && path.get(0)[0] == agLoc[0] && path.get(0)[1] == agLoc[1]) {
            path.remove(0);
        }
        switch (agentDirection) {
            case "N":
                if (agLoc[0] - 1 >= 0) //moves up the matrix
                    agLoc[0]--;
                break;
            case "S":
                if (agLoc[0] + 1 < world.length) //moves down the matrix
                    agLoc[0]++;
                break;
            case "E":
                if (agLoc[1] + 1 < world[0].length)
                    agLoc[1]++;
                break;
            case "W":
                if (agLoc[1] - 1 >= 0)
                    agLoc[1]--;
                break;
        }
        // reset noMoveStep when the agent moves
        noMoveStep = 0;
    }

    private ArrayList<int[]> path = new ArrayList<>();

    // find the shortest path to the unvisited safe cell by using breadthfirst search algorithm
    // (can be improved by using A* algorithm).
    public ArrayList<int[]> findNearestUnvisited(int i, int j) {
        Queue<ArrayList<int[]>> queue = new LinkedList<>();
        ArrayList<int[]> path = new ArrayList<>();
        path.add(new int[]{i, j});
        queue.add(path);
        HashSet<Utils.Coordinates> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            ArrayList<int[]> current = queue.remove();
            int x = current.get(current.size() - 1)[0];
            int y = current.get(current.size() - 1)[1];
            if (!world[x][y].isVisited()) {
                current.remove(0);
                return current;
            }
            for (int[] direction : directions) {
                int nx = x + direction[0];
                int ny = y + direction[1];
                if (nx >= 0 && nx < world.length && ny >= 0 && ny < world[0].length
                        && world[nx][ny].isCellOK()
                        && !visited.contains(new Utils.Coordinates(nx, ny))) {
                    ArrayList<int[]> newPath = new ArrayList<>(current);
                    newPath.add(new int[]{nx, ny});
                    queue.add(newPath);
                    visited.add(new Utils.Coordinates(nx, ny));
                }

            }
        }

        return new ArrayList<>();
    }

    // find next cell that the agent can travel safely.
    public int[] nextOkCell() {
        // if an Ok cell calculation has not been used, then return the values in nextLoc[].
        if (nextLoc[0] != -1) {
            return nextLoc;
        }

        boolean flag = false;
        for (int[] direction : directions) {
            int nX = agLoc[0] + direction[0];
            int nY = agLoc[1] + direction[1];
            // if an adjacent cell to the agent has not been visited and is marked Ok, then it will be traveled first.
            if (!flag && nX >= 0 && nY >= 0 && nX < world.length && nY < world[0].length && world[nX][nY].isCellOK()) {
                if (!world[nX][nY].isVisited())
                    flag = true;

                nextLoc[0] = nX;
                nextLoc[1] = nY;
            }
        }
        // if all adjacent cells have been visited then use findNearestUnvisited to find a shortest path
        // to an unvisited Ok cell.
        if (flag) {
            path = findNearestUnvisited(agLoc[0], agLoc[1]);
            if (!path.isEmpty()) {
                nextLoc = path.get(0);
            }
        }

        return nextLoc;
    }

    public void resetNextLoc() {
        nextLoc[0] = -1;
        nextLoc[1] = -1;
    }


    public int[] getAgLoc() {
        return agLoc;
    }

    public void setAgLoc(int[] agLoc) {
        this.agLoc = agLoc;
    }

    public Cell[][] getWorld() {
        return world;
    }

    public void setWorld(Cell[][] world) {
        this.world = world;
    }

    public String getAgentDirection() {
        return agentDirection;
    }

    public void setAgentDirection(String agentDirection) {
        this.agentDirection = agentDirection;
    }

    public int getPrevAction() {
        return prevAction;
    }

    //save the current action
    public void setPrevAction(int prevAction) {
        this.prevAction = prevAction;
    }
}
