# Maze Explorer-Simple-Reflex Agent
A simple reflex agent in the Maze Explorer project navigates the Wumpus World using predefined rules and immediate perceptual information.

This agent operates based on a set of condition-action rules that dictate its behavior in response to specific environmental cues. For example, if the agent detects a nearby pit or the presence of the Wumpus, it will follow predefined actions to avoid these dangers. The agent's decisions are made solely based on its current observations without considering past experiences or future consequences.

When exploring the Wumpus World, the simple reflex agent uses these rules to react to immediate sensory inputs. For instance, it might move away from detected pits or the Wumpus and move towards gold if it is nearby. While straightforward and limited by its reliance on current perceptions, this approach allows the agent to make quick decisions based on a fixed set of rules.

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