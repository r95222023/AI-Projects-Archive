import java.util.HashMap;

public class Utils {
	/*
	Percepts tuple - <glitter, stench, scream, bump, breeze>
	 */
    public static void createCARules(HashMap<String, ProbabilisticAction[]> rules){
        rules.put("00000", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.6f),
                new ProbabilisticAction(Action.TURN_RIGHT, 0.2f), new ProbabilisticAction(Action.TURN_LEFT, 0.2f)});

        rules.put("00001", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.1f),
                new ProbabilisticAction(Action.TURN_LEFT, 0.2f), new ProbabilisticAction(Action.NO_OP, 0.5f),
                new ProbabilisticAction(Action.TURN_RIGHT, 0.2f)});

        rules.put("00010", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.0f),
                new ProbabilisticAction(Action.TURN_LEFT, 0.5f), new ProbabilisticAction(Action.TURN_RIGHT, 0.5f)});

        rules.put("00011", new ProbabilisticAction[] {new ProbabilisticAction(Action.NO_OP, 0.0f),
                new ProbabilisticAction(Action.TURN_LEFT, 0.5f), new ProbabilisticAction(Action.TURN_RIGHT, 0.5f)});

        rules.put("00100", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.6f),
                new ProbabilisticAction(Action.TURN_LEFT, 0.2f), new ProbabilisticAction(Action.TURN_RIGHT, 0.2f)});

        rules.put("00101", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.2f),
                new ProbabilisticAction(Action.TURN_LEFT, 0.3f), new ProbabilisticAction(Action.NO_OP, 0.3f),
                new ProbabilisticAction(Action.TURN_RIGHT, 0.2f)});

        rules.put("00110", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.0f),
                new ProbabilisticAction(Action.TURN_LEFT, 0.5f), new ProbabilisticAction(Action.TURN_RIGHT, 0.5f)});

        rules.put("00111", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.0f),
                new ProbabilisticAction(Action.TURN_LEFT, 0.5f), new ProbabilisticAction(Action.TURN_RIGHT, 0.5f)});

        /*
        Percepts tuple - <glitter, stench, scream, bump, breeze>
         */

        rules.put("01000", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.1f),
                new ProbabilisticAction(Action.SHOOT, 0.3f), new ProbabilisticAction(Action.TURN_LEFT, 0.3f),
                new ProbabilisticAction(Action.TURN_RIGHT, 0.3f)});

        rules.put("01001", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.1f),
                new ProbabilisticAction(Action.SHOOT, 0.1f), new ProbabilisticAction(Action.TURN_LEFT, 0.1f),
                new ProbabilisticAction(Action.NO_OP, 0.7f), new ProbabilisticAction(Action.TURN_RIGHT, 0.0f)});

        rules.put("01010", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.0f),
                new ProbabilisticAction(Action.SHOOT, 0.0f), new ProbabilisticAction(Action.TURN_LEFT, 0.5f),
                new ProbabilisticAction(Action.TURN_RIGHT, 0.5f)});

        rules.put("01011", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.0f),
                new ProbabilisticAction(Action.SHOOT, 0.0f), new ProbabilisticAction(Action.TURN_LEFT, 0.2f),
                new ProbabilisticAction(Action.NO_OP, 0.6f), new ProbabilisticAction(Action.TURN_RIGHT, 0.2f)});

        rules.put("01100", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.6f),
                new ProbabilisticAction(Action.SHOOT, 0.0f), new ProbabilisticAction(Action.TURN_LEFT, 0.2f),
                new ProbabilisticAction(Action.TURN_RIGHT, 0.2f)});

        rules.put("01101", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.2f),
                new ProbabilisticAction(Action.SHOOT, 0.0f), new ProbabilisticAction(Action.TURN_LEFT, 0.2f),
                new ProbabilisticAction(Action.NO_OP, 0.4f), new ProbabilisticAction(Action.TURN_RIGHT, 0.2f)});

        rules.put("01110", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.0f),
                new ProbabilisticAction(Action.SHOOT, 0.0f), new ProbabilisticAction(Action.TURN_LEFT, 0.5f),
                new ProbabilisticAction(Action.TURN_RIGHT, 0.5f)});

        rules.put("01111", new ProbabilisticAction[] {new ProbabilisticAction(Action.GO_FORWARD, 0.0f),
                new ProbabilisticAction(Action.SHOOT, 0.0f), new ProbabilisticAction(Action.NO_OP, 0.6f),
                new ProbabilisticAction(Action.TURN_LEFT, 0.2f), new ProbabilisticAction(Action.TURN_RIGHT, 0.2f)});
    }
}