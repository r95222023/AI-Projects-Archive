# Maze Explorer
Maze Explorer is a project that features various types of AI agents designed to explore the Wumpus World, find gold, and avoid traps and the Wumpus. Each folder in this project contains a different type of AI agent, including a simple reflex agent, a model-based agent, a utility-based agent, and a learning agent.
## Environment
Wumpus-Lite is a lightweight, Java-based simulator for the Wumpus World, a popular environment used to study artificial intelligence and decision-making algorithms.

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