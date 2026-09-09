# Electrical Simulation Engine Specification

## Objective

The simulator is the technical heart of Home Electrical Academy. It models an educational electrical network sufficiently well for learning, prediction, measurement, protection behavior, fault finding and project assessment.

It is not intended to replace professional engineering software or a legally required inspection instrument.

## Simulation Layers

### Layer 1 — Circuit Topology

Represents:

- Nodes
- Terminals
- Conductive connections
- Switch state
- Open/closed paths
- Component identity

This layer answers: **Is there a conductive path?**

### Layer 2 — Electrical State

Represents educational quantities such as:

- Node voltage
- Branch current
- Resistance
- Apparent/active power where supported
- Load state

This layer answers: **What is electrically happening?**

### Layer 3 — Protection

Represents modeled response of protective devices such as:

- Overcurrent protection
- Residual-current protection concepts
- Isolation/switching state
- Surge-protection concepts

Protection behavior must be explicitly documented as a training model and not presented as a universal replacement for the exact characteristics of a real device.

### Layer 4 — Fault Injection

A fault modifies the model without rewriting the project itself. This allows the same house project to be tested under many scenarios.

### Layer 5 — Measurement

A virtual instrument observes the simulation state through defined measurement points. Incorrect measurement setup must itself be teachable and, where appropriate, produce a warning or invalid reading.

## Core Domain Objects

```text
SimulationProject
ElectricalNode
Terminal
Connection
Source
Load
Switch
ProtectiveDevice
Conductor
MeasurementPoint
Instrument
Fault
SimulationResult
```

## Simulation Tick

A simulation step conceptually performs:

1. Build the active network.
2. Apply component states and faults.
3. Solve the selected educational electrical model.
4. Evaluate protection behavior.
5. Re-solve if a protective device changes state.
6. Publish observable measurements.
7. Generate diagnostic events.

## Model Progression

The engine should not begin with maximum physical complexity. Educational models increase in sophistication as curriculum levels advance.

### Model A — Ideal DC circuits

For introductory lessons.

### Model B — Idealized AC residential circuits

For teaching RMS voltage, current, power and frequency concepts.

### Model C — Protection and fault behavior

Adds modeled device thresholds and trip logic.

### Model D — Residential project model

Adds branches, distribution boards, multiple loads, labeled circuits and project rules.

## Example Learning Scenario

Scenario: one source, one switch and one lamp.

The learner changes the source voltage and lamp resistance.

The application asks:

> Predict the current before running the simulation.

After simulation it shows:

- Source voltage
- Lamp voltage
- Circuit current
- Calculated lamp power

Then it explains the difference between the prediction and result.

## Example Troubleshooting Scenario

A lamp does not operate.

Possible hidden faults:

- Open switch
- Open conductor
- Incorrect terminal
- Failed lamp model
- Missing return path

The learner chooses measurement points and uses the virtual instrument to narrow the fault.

## Assessment Events

The engine can emit events such as:

```text
MEASUREMENT_CORRECT
MEASUREMENT_INVALID
PREDICTION_CORRECT
PREDICTION_INCORRECT
FAULT_LOCATED
FAULT_MISDIAGNOSED
PROTECTION_OPERATED
UNSAFE_ACTION_ATTEMPTED
```

## Safety-Critical UX

The simulator may represent dangerous mistakes for educational purposes, but it must not reward unsafe behavior. Unsafe-action attempts should produce clear explanatory feedback and should never be framed as an optimal technique.
