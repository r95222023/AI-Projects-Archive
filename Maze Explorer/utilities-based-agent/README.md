# Maze Utilities-Based Agent
A utilities-based agent in the Maze Explorer project solves the challenge of navigating the Wumpus World by evaluating and maximizing the overall utility of different actions.

This type of agent operates based on a utility function that quantifies the desirability of different states and actions. The utility function takes into account factors such as the proximity to gold, the risk of encountering traps or the Wumpus, and other relevant environmental factors.

When making decisions, the utilities-based agent calculates the expected utility of possible actions and chooses the one that provides the highest utility. For example, it may prioritize actions that bring it closer to gold while avoiding areas with high risk of traps or Wumpus encounters. By continuously assessing and maximizing utility, the agent effectively balances between pursuing goals and avoiding dangers, leading to an optimal strategy for exploring the Wumpus World. For more detailed information, refer to Utilities-Based Agent for Wumpus World.pdf.

## Getting Started

### Building and Running the Simulator

To build and run the simulator, follow these steps:

1. **Compile the Simulator:**

   ```bash
   javac WorldApplication.java
   ```

2. **Run the Simulator:**

   ```bash
   java WorldApplication [options]
   ```

### Command-Line Options

The simulator supports several command-line options to customize the Wumpus World:

- `-d <dimension>`: Sets the dimensions of the Wumpus World to be `dimension x dimension`.  
  **Default:** 4 (a 4x4 world)

- `-s <steps>`: Sets the maximum number of time steps.  
  **Default:** 50

- `-t <trials>`: Sets the number of trials.  
  **Default:** 1

- `-a <randAgent>`: Determines whether the agent's location and orientation are randomly generated.  
  **Default:** true

- `-r <seed>`: Sets the seed for the random Wumpus World generator.  
  **Default:** A random integer

- `-n <nonDeterm>`: Controls whether the agent's `GO_FORWARD` action behavior is non-deterministic.  
  **Default:** false

### Customizing the Agent

To implement your own agent, modify the `AgentFunction.java` file according to your desired logic and behavior.

## Gameplay Legend

- **P**: Pit
- **W**: Live Wumpus
- **\***: Dead Wumpus
- **G**: Gold
- **A**: Agent facing North
- **>**: Agent facing East
- **V**: Agent facing South
- **<**: Agent facing West

Explore the Wumpus World and test your AI algorithms with Wumpus-Lite!