# Maze Explorer-Learning Agent
A learning agent in the Maze Explorer project is designed to adapt its strategy based on experience as it navigates the Wumpus World. Unlike other agents that rely on predefined rules or models, the learning agent uses reinforcement learning to improve its decision-making over time.

As the learning agent explores the Wumpus World, it learns from its actions by receiving feedback from the environment. Positive feedback, such as finding gold, reinforces successful actions, while negative feedback, like falling into a trap or encountering the Wumpus, penalizes undesirable behaviors. By repeatedly interacting with the environment, the learning agent gradually develops a policy that maximizes its chances of finding gold while minimizing the risk of encountering dangers. This adaptive approach allows the learning agent to become more efficient and effective in navigating the Wumpus World with each exploration.

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