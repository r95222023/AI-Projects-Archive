# Maze Explorer-Model-Based Agent
A model-based agent in the Maze Explorer project solves the challenge of navigating the Wumpus World by using an internal model of the environment to make informed decisions.

The model-based agent maintains a representation of the Wumpus World, which includes information about the agent’s current state, observed features of the environment (such as the presence of pits, the Wumpus, and gold), and the outcomes of previous actions. This internal model is continuously updated based on the agent's experiences and observations.

When making decisions, the model-based agent uses this internal model to simulate possible actions and predict their outcomes. By evaluating these simulations, the agent can anticipate the consequences of its actions and choose the most advantageous course of action. For example, it can plan a path that avoids known hazards and moves it closer to gold, taking into account both the immediate environment and the accumulated knowledge about the world.

This approach allows the model-based agent to systematically explore the Wumpus World and adapt its strategy based on a comprehensive understanding of the environment. For more details on the model-based approach, check Model-Based Agent for Wumpus World.pdf.

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