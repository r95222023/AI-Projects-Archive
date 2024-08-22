import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

public class Testing {
    public StringBuilder beliefState;
    private int frontierSize;
    private int searchDepth = 2;
    private int seed;
    private int numTrials = 5;
    private int maxSteps = 50;
    private WumpusWorld trialWorld;

    public static char[][][] generateRandomWumpusWorldByBeliefState(int seed, WumpusWorld ww, int level, int frontierSize) {
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
                newWorld[i][j][0] = cell.pit == 4 ? 'P' : ' ';
                newWorld[i][j][1] = cell.wumpus == 4 ? 'W' : ' ';
                newWorld[i][j][2] = ' ';
                newWorld[i][j][3] = ' ';

                if (cell.visited != 0) {
                    newWorld[i][j][0] = ' ';
                    newWorld[i][j][1] = ' ';
                    newWorld[i][j][2] = ' ';
                    newWorld[i][j][3] = ' ';
                } else {
                    if (cell.frontier > level) {
//                        newWorld[i][j][0] = '+';
//                        newWorld[i][j][1] = '+';
//                        newWorld[i][j][2] = '+';
//                        newWorld[i][j][3] = '+';

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
        newWorld[0][0][3] = '>';
        return newWorld;
    }

    public static char[][][] showWumpusWorld(WumpusWorld ww) {
        int size = 4;
        char[][][] newWorld = new char[size][size][4];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                WumpusCell cell = ww.cells[j][i];
                newWorld[i][j][0] = cell.pit == 4 ? 'P' : ' ';
                newWorld[i][j][1] = cell.wumpus == 4 ? 'W' : ' ';
                newWorld[i][j][2] = ' ';
                newWorld[i][j][3] = ' '/*(char)cell.frontier*/;
            }
        }
        try{
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter("temp"));
            Environment wumpusEnvironment = new Environment(size, newWorld, outputWriter);
            wumpusEnvironment.printEnvironment();
        } catch (Exception e) {
            System.out.println("An exception was thrown: " + e);
        }

        return newWorld;
    }

    public static void printRandomWumpusWorldByBeliefState(int seed, WumpusWorld ww, int level, int frontierSize) {
        try {
            char[][][] rWW = generateRandomWumpusWorldByBeliefState(seed, ww, level, frontierSize);
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
        showWumpusWorld(this.trialWorld);
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
                System.out.println("Learning sample1");
                WumpusWorld simpleCopy = new WumpusWorld();
//                char[][][] rww = generateRandomWumpusWorldByBeliefState2(seed, trialWorld, searchDepth, frontiers.size());
                simpleCopy.pitFound = trialWorld.pitFound;
                simpleCopy.wumpusFound = trialWorld.wumpusFound;
                simpleCopy.known = trialWorld.known;
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        simpleCopy.cells[j][i].pit = trialWorld.cells[j][i].pit;
                        simpleCopy.cells[j][i].wumpus = trialWorld.cells[j][i].wumpus;
                        simpleCopy.cells[j][i].frontier = trialWorld.cells[j][i].frontier;
                    }
                }
                res.add(simpleCopy);
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
        return generateRandomWumpusWorldByBeliefState(seed, trialWorld, searchDepth, frontierSize);
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
                System.out.println("count:"+currTrial);
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
        return this.beliefState + String.valueOf(avgScore) + "\n";
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
