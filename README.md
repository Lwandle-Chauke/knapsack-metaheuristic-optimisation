<div align="center">

# Knapsack Metaheuristic Optimisation

### Comparative implementation of a Genetic Algorithm and Iterated Local Search for solving the 0/1 Knapsack Problem.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Artificial Intelligence](https://img.shields.io/badge/Artificial%20Intelligence-4285F4?style=for-the-badge)
![Optimisation](https://img.shields.io/badge/Optimisation-43A047?style=for-the-badge)
![Genetic Algorithm](https://img.shields.io/badge/Genetic%20Algorithm-6A1B9A?style=for-the-badge)

**Authors:** **Lwandle Chauke and Team**

</div>

---

# Overview

This project investigates the performance of two popular metaheuristic optimisation algorithms for solving the classical **0/1 Knapsack Problem**.

The implementation compares:

- Genetic Algorithm (GA)
- Iterated Local Search (ILS)

Both algorithms were evaluated on multiple benchmark problem instances to determine their ability to maximise solution quality while maintaining efficient execution times.

The project also includes statistical analysis using the **Wilcoxon Signed-Rank Test** to compare the performance of both optimisation techniques.

---

# Project Highlights

- Solves the 0/1 Knapsack optimisation problem
- Genetic Algorithm implementation
- Iterated Local Search implementation
- Performance benchmarking
- Seed-based reproducible experiments
- Statistical comparison using the Wilcoxon test
- Comprehensive experimental report

---

# Algorithms Implemented

## Genetic Algorithm

The Genetic Algorithm uses evolutionary principles to evolve increasingly better solutions.

Features include:

- Population initialisation
- Fitness evaluation
- Parent selection
- Crossover
- Mutation
- Survivor selection
- Termination criteria

---

## Iterated Local Search

The Iterated Local Search algorithm repeatedly improves candidate solutions through local optimisation.

Features include:

- Initial solution generation
- Local search
- Perturbation
- Acceptance criterion
- Iterative refinement

---

# Problem Definition

The objective is to maximise the total value of items placed into a knapsack while ensuring that the combined weight does not exceed the knapsack's capacity.

Each item has:

- Weight
- Value

Each item may only be selected once.

---

# Performance Evaluation

The algorithms were compared using:

- Best solution found
- Known optimum
- Runtime
- Solution quality
- Statistical significance

---

# Tech Stack

- Java
- Object-Oriented Programming
- Artificial Intelligence
- Metaheuristics
- Evolutionary Computing
- Statistical Analysis

---

# Repository Structure

```text
knapsack-metaheuristic-optimisation/

├── src/
│
├── datasets/
│
├── docs/
│   ├── Project-Report.pdf
│   ├── Experimental-Results.pdf
│   └── Wilcoxon-Analysis.pdf
│
├── README.md
├── .gitignore
└── LICENSE
```

---

# Running the Project

Clone the repository

```bash
git clone https://github.com/Lwandle-Chauke/knapsack-metaheuristic-optimisation.git
```

Compile

```bash
javac Main.java
```

Run

```bash
java Main
```

The application prompts for:

- Random seed
- Dataset selection

before executing the optimisation algorithms.

---

# Experimental Results

The project evaluates algorithm performance across multiple benchmark instances.

Metrics collected include:

- Best Solution
- Known Optimum
- Runtime
- Seed Value

The complete results are available in the documentation.

---

# Documentation

The repository includes detailed documentation covering:

- Genetic Algorithm implementation
- Iterated Local Search implementation
- Experimental configuration
- Performance evaluation
- Statistical analysis
- Critical discussion of results

---

# Key Concepts Demonstrated

- Artificial Intelligence
- Metaheuristics
- Evolutionary Algorithms
- Optimisation
- Local Search
- Statistical Analysis
- Benchmark Testing
- Experimental Design

---

# Team Project

This project was completed collaboratively as part of a university team assignment.

It demonstrates experience working within a software development team while implementing and evaluating optimisation algorithms.

---

# What I Learned

Working on this project strengthened my understanding of:

- Evolutionary computation
- Metaheuristic optimisation
- Search strategies
- Benchmark evaluation
- Statistical testing
- Java development
- Experimental analysis

It also provided practical experience comparing different optimisation techniques for solving NP-hard problems.

---

# Future Improvements

Potential future enhancements include:

- Simulated Annealing
- Particle Swarm Optimisation
- Ant Colony Optimisation
- Differential Evolution
- Hybrid Metaheuristics
- Parallel Genetic Algorithms
- Interactive visualisations
- Larger benchmark datasets

---

# About Me

I'm **Lwandle Chauke**, a Computer Science graduate with interests in:

- Artificial Intelligence
- Software Engineering
- Cybersecurity
- Optimisation Algorithms
- Machine Learning

I enjoy developing efficient algorithms and applying computational intelligence techniques to complex real-world problems.

**GitHub**

https://github.com/Lwandle-Chauke

---

<div align="center">

If you found this project interesting, feel free to star the repository!

</div>
