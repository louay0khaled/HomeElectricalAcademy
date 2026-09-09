# Architecture

## Target Platform

Android, Kotlin, Jetpack Compose, Material 3.

## Modules

```text
app/
core/
  common/
  model/
  ui/
  database/
  network/
  simulator/
  standards/
feature/
  onboarding/
  dashboard/
  courses/
  lessons/
  quizzes/
  simulator/
  blueprint/
  wiring/
  panel/
  faultlab/
  calculator/
  projects/
  progress/
  settings/
content/
docs/
```

## Separation of Concerns

### UI Layer

Compose screens and state rendering only. It must not contain electrical equations or circuit-rule logic.

### Domain Layer

Educational use cases, progress rules, quiz evaluation, project evaluation and simulation orchestration.

### Simulation Layer

The electrical engine owns components, terminals, connections, circuit state, measurements, protection behavior and faults.

### Data Layer

Room stores progress, user-created projects, simulation snapshots and local settings. Data-driven curriculum assets remain versioned with the application initially.

### Standards Layer

A standards profile supplies jurisdiction-specific rules to the domain/simulation engines. No UI should hard-code a country-specific rule.

## Electrical Simulation Model

Core concepts:

```text
ElectricalNetwork
  Node[]
  Branch[]
  Component[]
  Source[]
  Load[]
  Protection[]
  Fault[]
  MeasurementPoint[]
```

Every component exposes terminals and a simulation model. The engine should calculate an internally consistent state and return observable quantities rather than directly manipulating UI objects.

## Determinism

The same project + component configuration + fault configuration + timestep/solver settings must produce deterministic results where the selected simulation model allows it. This is essential for assessments and regression tests.

## Persistence

Projects and simulator state must be serializable so a learner can exit a scenario and resume it later.

## Testing Strategy

- Unit tests for equations and domain rules.
- Simulation regression tests for known circuits.
- Property-based tests where practical for circuit invariants.
- Compose UI tests for critical learning flows.
- Instrumented tests for persistence and navigation.

## First Vertical Slice

The first implementation should prove the architecture with one complete lesson:

`DC/AC foundation -> simple circuit -> interactive component changes -> run simulation -> measure voltage/current -> predict result -> compare -> quiz -> save progress`

Only after this vertical slice is stable should large curriculum expansion begin.
