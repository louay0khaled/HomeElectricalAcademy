# Product Specification

## 1. Product Identity

Home Electrical Academy (HEA) is a practical and scientific residential-electrical training simulator for Android.

The learner does not watch a sequence of videos. Instead, each concept is presented as a compact scientific explanation followed by an interactive model where the learner manipulates electrical components and observes measurable consequences.

## 2. Learning Loop

Every major concept follows:

`Explain -> Observe -> Manipulate -> Predict -> Simulate -> Measure -> Diagnose -> Explain Why -> Assessment`

Example:

The lesson explains voltage, current and resistance. The learner then changes a simulated supply voltage or load resistance, predicts current, runs the simulation, measures the circuit, and receives an explanation comparing prediction with observed behavior.

## 3. End-State Competency

A successful learner should understand a complete residential installation as a system:

`Utility source -> service connection -> metering -> main isolation/protection -> distribution -> branch circuits -> wiring methods -> switches/outlets/luminaires -> dedicated loads -> protective earthing/bonding -> verification -> fault finding`

The application must not claim that simulation alone licenses a person to work on a live public utility network. The final curriculum distinguishes knowledge, simulated competence, supervised practical competence, and legal authorization.

## 4. Curriculum

### Level 0 — Electrical Foundations

- Matter, charge and electron flow
- Electric potential / voltage
- Current
- Resistance
- Power and energy
- AC and DC
- Frequency
- Ohm's law
- Series and parallel circuits
- Open circuits and short circuits

### Level 1 — Measurement and Components

- Voltage measurement
- Current measurement
- Resistance and continuity
- Multimeter model
- Clamp meter model
- Basic protective devices
- Switches, sockets and luminaires
- Conductors, insulation and terminals

### Level 2 — Residential Distribution

- Service concept
- Metering concept
- Main disconnect/isolation
- Distribution board architecture
- Neutral and protective conductor concepts
- Protective devices
- Surge protection concepts
- Earthing/bonding concepts

### Level 3 — Circuit Design

- Load inventory
- Lighting circuits
- General-purpose receptacle/socket circuits
- Dedicated appliance circuits
- Motor loads
- Heating loads
- Electronic loads
- Circuit grouping
- Demand/load estimation
- Conductor and protection selection under a selected standard profile

### Level 4 — Installation Simulation

- House plan interpretation
- Component placement
- Conduit/routing simulation
- Cable/conductor routing
- Junction/termination simulation
- Switch and outlet wiring
- Lighting circuits
- Dedicated loads
- Distribution-board assembly simulation

### Level 5 — Verification

- Visual inspection
- Continuity
- Polarity concepts
- Protective-conductor continuity
- Insulation testing concepts
- Voltage verification concepts
- Functional testing
- Documentation and labeling

### Level 6 — Troubleshooting

- Open conductor
- Incorrect termination
- Short circuit
- Overload
- Protective-device operation
- Neutral-related faults
- Earthing/protective-conductor faults
- Intermittent faults
- Multi-fault scenarios

### Level 7 — Capstone Projects

The learner receives unseen residential projects and must:

1. Survey the virtual building.
2. Identify loads.
3. Design circuits.
4. Select components under the active standard profile.
5. Route the installation.
6. Build the distribution board.
7. Run verification tests.
8. Diagnose injected faults.
9. Produce a final project record.

## 5. Simulation Modes

### Learn Mode

The system provides guidance and visual explanations.

### Practice Mode

The learner is guided only when needed. Errors are explained after the learner has a chance to reason.

### Exam Mode

No step-by-step assistance. Scoring is based on correctness, safety, measurements, efficiency and reasoning.

### Free Lab

The learner can experiment with components and observe circuit behavior without score pressure.

## 6. Scientific Visualization

Whenever useful, simulation should show:

- Voltage at nodes
- Current through branches
- Power consumed
- Circuit state
- Protection-device state
- Fault state
- Energy flow visualization
- Measurement instrument readings

Visual effects must remain scientifically interpretable; decorative animation must never imply incorrect electrical behavior.

## 7. Virtual Instruments

Initial instruments:

- Digital multimeter
- Two-pole voltage tester concept
- Clamp meter
- Continuity tester
- Insulation-resistance tester concept

Instrument behavior should include realistic limitations, correct/incorrect measurement setup, and safety-related interlocks in the simulator.

## 8. Residential Blueprint System

A project consists of structured objects rather than a flat image:

- Rooms
- Walls/openings
- Electrical points
- Distribution boards
- Routes
- Conductors
- Circuits
- Loads
- Protection devices
- Labels
- Project metadata

Users can toggle electrical layers and trace a selected circuit through the house.

## 9. Fault Engine

Faults are first-class simulation objects. Examples include:

- Open circuit
- Short circuit
- Wrong terminal
- Missing protective conductor
- Excessive load
- Incorrect protection selection
- Connection resistance increase
- Intermittent connection

Fault scenarios should be reproducible and scored.

## 10. Standards Profiles

Electrical rules vary by jurisdiction. The engine therefore uses a profile abstraction.

A profile may define:

- Nominal voltage
- Frequency
- Supply arrangement
- Conductor terminology
- Wiring conventions
- Protection rules
- Calculation assumptions
- Required tests
- Documentation requirements

Content must clearly label which statements are universal electrical principles and which are jurisdiction-specific requirements.

## 11. Content Model

Lessons, simulations, quizzes, components and projects are data-driven. This permits new curriculum content without rewriting the application architecture.

Suggested content structure:

`content/lessons/`
`content/simulations/`
`content/components/`
`content/projects/`
`content/quizzes/`
`content/standards/`

## 12. Assessment

Assessment combines:

- Scientific knowledge
- Circuit reasoning
- Practical simulation
- Measurement technique
- Fault diagnosis
- Safety decisions
- Project quality

A learner must pass critical safety and core reasoning competencies; raw quiz score alone is insufficient.
