import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

public class Learning {
    public static int pitKnown = 0;
    public static int wumpusKnown = 0;
    public static int unknownCell = 2;
    public char[][] state = new char[4][4];
    public boolean[][] visitedCell = new boolean[4][4];
    public StringBuilder beliefState;
    private int frontierSize;
    private int searchDepth;
    private int seed;
    private int numTrials = 1000;
    private int maxSteps = 50;
    private WumpusWorld trialWorld;

    public void updateState(int X, int Y, boolean visited, char s) {
        visitedCell[X][Y] = visited;
        state[X][Y] = s;
    }

    ;

    //    public char[][][] world;
//    public int gold;
//    public int safe;
//    public int pit;
//    public int wumpus;
//    public int visited;
//    public int pitExists;
//    public int wumpusExists;
//    public int frontier;
//    public int pitFound;
//    public int wumpusFound;
//    public int valid;
//    public int known;
//    public HashSet<String> pitLocs;
//    public HashSet<String> wumpusLoc;
//
//    public StateSnapshot(int X, int Y, WumpusWorld trialWolrd) {
////        WumpusCell cell = trialWolrd.cells[X][Y];
////        pitFound = trialWolrd.pitFound;
////        wumpusFound = trialWolrd.wumpusFound;
////        pitLocs = (HashSet<String>) trialWolrd.pits.stream().map(String::new).collect(Collectors.toSet());
////        wumpusLoc = (HashSet<String>) trialWolrd.wumpus.stream().map(String::new).collect(Collectors.toSet());
////        valid = trialWolrd.valid;
////        known = trialWolrd.known;
////
////        gold = cell.gold;
////        safe = cell.safe;
////        pit = cell.pit;
////        wumpus = cell.wumpus;
////        visited = cell.visited;
////        pitExists = cell.pitExists;
////        wumpusExists = cell.wumpusExists;
////        frontier = cell.frontier;
//    }
    public static char[][][] generateRandomWumpusWorldByBeliefState(int seed, int size, char[][][] beliefState, boolean[][] visited) {

        char[][][] newWorld = new char[size][size][4];
        int unknown = unknownCell;

        int x, y;
        int pitLeft = 2 - pitKnown;
        int wumpusLeft = 1 - wumpusKnown;
        int goldLeft = 1;
        Random randGen = new Random(seed);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (newWorld[i][j][2] == ' ' && !visited[i][j]) { // not agent location
                    if (pitLeft > 0 && newWorld[i][j][0] != 'P') {
                        if (randGen.nextInt(unknown) == 0) {
                            newWorld[i][j][0] = 'P';
                            pitLeft -= 1;
                            unknown -= 1;
                        }
                    }
                    if (wumpusLeft > 0) {
                        if (randGen.nextInt(unknown) == 0) {
                            newWorld[i][j][1] = 'W';
                            wumpusLeft -= 1;
                            unknown -= 1;
                        }
                    }
                    if (goldLeft > 0) {
                        if (randGen.nextInt(unknown) == 0) {
                            newWorld[i][j][2] = 'G';
                            goldLeft -= 1;
                        }
                    }
                }
            }
        }

        return newWorld;
    }

    public static char[][][] generateRandomWumpusWorldByBeliefState2(int seed, WumpusWorld ww, int level, int frontierSize) {
        int size = 4;
        char[][][] newWorld = new char[size][size][4];
        int pitLeft = 2 - ww.pitFound;
        int wumpusLeft = 1 - ww.wumpusFound;
        int goldLeft = 1;
        int unknown = 16 - ww.known - frontierSize;
        int goldSlots = 16 - ww.known;

        Random randGen = new Random(seed);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                WumpusCell cell = ww.cells[j][i];
                newWorld[i][j][0] = cell.pit == 4 ? 'P' : '-';
                newWorld[i][j][1] = cell.wumpus == 4 ? 'W' : '-';
                newWorld[i][j][2] = '-';
                newWorld[i][j][3] = '-';

//                beliefState[i][j] = "";
//                beliefState[i][j] += cell.pit == 4 ? "P" : "";
//                beliefState[i][j] += cell.wumpus == 4 ? "W" : "";

                if (cell.visited != 0) {
                    newWorld[i][j][0] = ' ';
                    newWorld[i][j][1] = ' ';
                    newWorld[i][j][2] = ' ';
                    newWorld[i][j][3] = ' ';

//                    beliefState[i][j] = "v";
                } else {
                    if (cell.frontier > level) {
                        newWorld[i][j][0] = '+';
                        newWorld[i][j][1] = '+';
                        newWorld[i][j][2] = '+';
                        newWorld[i][j][3] = '+';

                        if (pitLeft > 0 && cell.pit != 4) {
                            if (randGen.nextInt(unknown) == 0 || pitLeft == unknown) {
                                newWorld[i][j][0] = 'P';

                                pitLeft -= 1;
                            }
                        }
                        if (wumpusLeft > 0) {
                            if (randGen.nextInt(unknown) == 0 || wumpusLeft == unknown) {
                                newWorld[i][j][1] = 'W';
                                wumpusLeft -= 1;
                            }
                        }
                        unknown -= 1;
                    }
                    if (goldLeft > 0) {
                        if (randGen.nextInt(goldSlots) == 0 || goldLeft == goldSlots) {
                            newWorld[i][j][2] = 'G';
                            goldLeft -= 1;
                        }
                        goldSlots -= 1;
                    }
                }
            }
        }

        return newWorld;
    }

    public static void printRandomWumpusWorldByBeliefState2(int seed, WumpusWorld ww, int level, int frontierSize) {
        try {
            char[][][] rWW = generateRandomWumpusWorldByBeliefState2(seed, ww, level, frontierSize);
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter("random_wumpus_world_bs.txt"));
            Environment wumpusEnvironment = new Environment(4, rWW, outputWriter);
            wumpusEnvironment.printEnvironment();
        } catch (Exception e) {
            System.out.println("An exception was thrown: " + e);
        }
    }

    public void setTrialWolrd(WumpusWorld trialWorld, int seed, int searchDepth) {
        Random randGen = new Random(seed);
        ArrayList<WumpusWorld> res = new ArrayList<>();
        ArrayList<int[]> frontiers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                WumpusCell cell = trialWorld.cells[i][j];
                if (cell.frontier > 0 && cell.frontier < searchDepth + 1) {
                    frontiers.add(new int[]{i, j});
                }
            }
        }
        generateWorldsForGivernFrontiers(frontiers, 0, trialWorld, res);
        this.seed = seed;
        this.searchDepth = searchDepth;
        frontierSize = frontiers.size();
        this.trialWorld = res.get(randGen.nextInt(res.size()));
        StringBuilder beliefState = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                WumpusCell cell = this.trialWorld.cells[j][i];
                beliefState.append(cell.frontier + ",");
                beliefState.append(cell.pit == 4 ? "1," : "0,");
                beliefState.append((cell.wumpus == 4 ? "1," : "0,"));
            }
        }
        this.beliefState = beliefState;
    }

    public void generateWorldsForGivernFrontiers(ArrayList<int[]> frontiers, int index, WumpusWorld trialWorld, ArrayList<WumpusWorld> res) {
        if (index == frontiers.size()) {
            // Calculate utility for each frontier cell
            if (trialWorld.valid == 1 && trialWorld.verifyWorld(frontiers)) {
                System.out.println("Learning sample");
//                char[][][] rww = generateRandomWumpusWorldByBeliefState2(seed, trialWorld, searchDepth, frontiers.size());
                res.add(trialWorld);
            }
            return;
        }
        int X = frontiers.get(index)[0];
        int Y = frontiers.get(index)[1];
        StateSnapshot snap = new StateSnapshot(X, Y, trialWorld);

        if (trialWorld.valid == 1) { // If a cell is tagged as safe, having pit or wumpus will cause invalidity

            // Include i th candidate or not (k==0 include, k==1 don't include)
            for (int k = 0; k < 4; k++) {
                // Mark the flag for a configuration.
                trialWorld.tryFrontierConfiguration(X, Y, k);
                // Try next level combinations
                generateWorldsForGivernFrontiers(frontiers, index + 1, trialWorld, res);
                // Unmark the flag (backtrack) for next configuration
                trialWorld.backtrack(X, Y, snap);
            }
        }
    }


    public char[][][] generateWorldSample(int seed) {
        return generateRandomWumpusWorldByBeliefState2(seed, trialWorld, searchDepth, frontierSize);
    }

    public double getAverageScore(String outFilename) {
        int totalScore = 0;
        try {
            Random rand = new Random();
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outFilename));
            int size = 4;
            char[][][] wumpusWorld = generateWorldSample(seed);
            Environment wumpusEnvironment = new Environment(size, wumpusWorld, outputWriter);
            int trialScores[] = new int[numTrials];

            for (int currTrial = 0; currTrial < numTrials; currTrial++) {

                Simulation trial = new Simulation(wumpusEnvironment, maxSteps, outputWriter, false, false);
                trialScores[currTrial] = trial.getScore();

                wumpusWorld = generateWorldSample(rand.nextInt());

                wumpusEnvironment = new Environment(size, wumpusWorld, outputWriter);

                System.runFinalization();
            }

            for (int i = 0; i < numTrials; i++) {
                totalScore += trialScores[i];
            }

        } catch (Exception e) {
            System.out.println("An exception was thrown: " + e);
        }
        return (double) totalScore / (double) numTrials;
    }

    public String generateTrainingPair(WumpusWorld trialWorld) {
        setTrialWolrd(trialWorld, seed, searchDepth);
        double avgScore = getAverageScore("avgScore.txt");
        return this.beliefState+ String.valueOf(avgScore)+"\n";
    }

    public void generatePrintTrainingPair(WumpusWorld trialWorld) {
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter("trainingData.csv", true));
            String trainingSample = generateTrainingPair(trialWorld);
            outputWriter.write(trainingSample);
            outputWriter.close();
        } catch (Exception e) {
            System.out.println("An exception was thrown: " + e);
        }

    }
}
