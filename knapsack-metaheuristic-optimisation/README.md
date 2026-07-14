# 0/1 Knapsack Problem — GA & ILS

## Overview
This project implements two metaheuristic algorithms to solve 10 benchmark instances of the 0/1 Knapsack Problem from the low-dimensional dataset:
- **Genetic Algorithm (GA)** — a population-based metaheuristic
- **Iterated Local Search (ILS)** — a trajectory-based metaheuristic

## Requirements
- Java 8 or higher (JDK for compiling, JRE for running)

## Project Structure
```
AI-2/
├── src/                         # Java source files
│   ├── Main.java                # Entry point (runs both GA and ILS)
│   ├── GeneticAlgorithm.java
│   ├── IteratedLocalSearch.java
│   ├── KnapsackInstance.java    # Problem instance loader
│   └── KnapsackSolution.java   # Solution representation
├── data/                        # Problem instance files
│   ├── f1_l-d_kp_10_269 ... f10_l-d_kp_20_879
├── MANIFEST.MF
└── README.md
```

## How to Build
From the project root directory (`AI-2`):

```bash
# Compile all source files
javac -d out src/*.java

# Package the JAR
jar cfm Knapsack.jar MANIFEST.MF -C out .
```

## How to Run

### Option 1: Run the JAR (recommended)
```bash
java -jar Knapsack.jar
```

### Option 2: Run from compiled classes (terminal)
```bash
javac -d out src/*.java
java -cp out Main
```

### Running
1. The program will prompt you to enter a **seed value** (integer).
2. It will then solve all 10 knapsack instances using both GA and ILS.
3. Results are displayed in a comparison table showing:
   - Problem instance name
   - Algorithm (ILS and GA)
   - Seed value used
   - Best solution found
   - Known optimum
   - Runtime in seconds

## Algorithm Configuration

### Genetic Algorithm (GA)
| Parameter             | Value                                          |
|-----------------------|------------------------------------------------|
| Max Generations       | 1000                                           |
| Population Size       | 100                                            |
| Selection             | Binary tournament (size 2)                     |
| Crossover             | Uniform crossover (rate = 0.85)                |
| Mutation              | Bit-flip (rate = 1/n per gene)                 |
| Elitism               | Top 2 individuals carried forward unchanged    |
| Repair                | Remove items by ascending value/weight ratio   |
| Fill                  | Insert items by descending value/weight ratio  |

### Iterated Local Search (ILS)
| Parameter             | Value                                              |
|-----------------------|----------------------------------------------------|
| Max Iterations        | 5000                                               |
| Perturbation Strength | 3                                                  |
| Initial Solution      | Greedy randomised construction                     |
| Local Search          | First-improvement 1-flip + swap (1-1 exchange)     |
| Acceptance Criterion  | Better or probabilistic (SA-like)                  |
| Repair                | Remove items by ascending value/weight ratio       |

## Notes
- The `data/` folder must be in the same directory as the JAR file when running.
- The program is seeded for reproducibility: the same seed always produces the same results.
