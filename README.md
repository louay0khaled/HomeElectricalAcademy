# HomeElectricalAcademy

## Home Electrical Academy

An Android-first, simulation-driven residential electrical training system.

The project is **not a video-course app**. Its primary teaching method is interactive electrical simulation combined with scientific explanations, guided exercises, inspection, troubleshooting, and complete residential projects.

## Product Goal

Train a beginner progressively toward professional-level understanding of a residential electrical installation, from the service entrance and distribution system through final circuits, loads, testing, and fault diagnosis.

The application teaches through:

- Scientific interactive lessons
- Electrical circuit simulation
- Virtual instruments and measurements
- Residential blueprint/wiring simulation
- Distribution-board simulation
- Fault injection and troubleshooting
- Design and inspection exercises
- Calculators and engineering tools
- Progressive practical examinations
- Complete capstone house projects

## Safety Boundary

The simulator is educational. It must never encourage unsafe work on energized utility infrastructure. Any real-world work on utility/service equipment must be handled according to local regulations, utility requirements, isolation procedures, and qualified-person requirements.

## Core Principles

1. Simulation before real-world practice.
2. Explanation of **why**, not only **how**.
3. Every practical action should have an underlying electrical model.
4. The learner should discover faults rather than memorize answers.
5. Standards and electrical rules must be represented as configurable profiles rather than hard-coded to one country.
6. Offline-first for core learning and simulation.

## Planned Architecture

- Kotlin
- Jetpack Compose
- Material 3
- Hilt
- Room
- DataStore
- Kotlin Coroutines / Flow
- Modular clean architecture
- Dedicated electrical simulation engine
- Data-driven course/content system

See `docs/` for the product and engineering specifications.
