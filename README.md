# SmartCity360 – Smart City Management Platform
### Enterprise Object-Oriented Design Patterns Suite
**Developed by:** UrbanTech Solutions  
**Technology Stack:** Java 17 LTS, Apache Maven, FlatLaf 3.4, JUnit 5.10.2  

---

## 🏛️ Project Overview

**SmartCity360** is an integrated municipal command platform designed to monitor, secure, and operate metropolitan infrastructure through five classic **Gang of Four (GoF)** design patterns:

| Part | GoF Design Pattern | Pattern Type | Municipal Infrastructure Application | Key Class Participants |
| :---: | :--- | :--- | :--- | :--- |
| **Part A** | **Decorator Pattern** | Structural | Dynamic layered security (AES-256 Encryption, HMAC-SHA256 Authentication, GZIP Compression) for smart energy meter telemetry. | `DataStream`, `EnergyMeterStream`, `StreamDecorator`, `EncryptionDecorator`, `AuthenticationDecorator`, `CompressionDecorator` |
| **Part B** | **Composite Pattern** | Structural | Recursive 4-level administrative hierarchy (`City` $\rightarrow$ `District` $\rightarrow$ `Street` $\rightarrow$ `Building`) with uniform power aggregation and emergency shutdown propagation. | `SmartZoneComponent`, `ZoneComposite`, `BuildingLeaf`, `ZoneType` |
| **Part C** | **Chain of Responsibility** | Behavioral | Decoupled municipal complaint escalation (`Local Staff` $\rightarrow$ `Supervisor` $\rightarrow$ `City Manager`) with dynamic runtime bypassing and emergency fallback. | `ComplaintHandler`, `ComplaintRequest`, `ComplaintResolution`, `LocalStaffHandler`, `SupervisorHandler`, `CityManagerHandler` |
| **Part D** | **Flyweight Pattern** | Structural | High-density memory pooling of 50,000+ smart streetlights across 4 shared optical models, achieving **>95.5% heap RAM savings**. | `StreetLightFlyweight`, `SharedLightModel`, `StreetLightFactory`, `StreetLightContext`, `MemoryBenchmarkService` |
| **Part E** | **Interpreter Pattern** | Behavioral | Mini-DSL grammar parser and Abstract Syntax Tree (AST) evaluator for dynamic municipal policy and curfew enforcement without code recompilation. | `Expression`, `ComparisonExpression`, `AndExpression`, `OrExpression`, `NotExpression`, `PolicyContext`, `PolicyDSLParser` |

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java SE Development Kit 17+** (`javac -version`)
- **Apache Maven 3.8+** (`mvn -version`)

### 1. Compile Domain & Presentation Classes
```powershell
mvn clean compile
```

### 2. Execute Automated Verification Suite (JUnit 5)
Run all 31 automated tests across all 5 design patterns and UI smoke tests:
```powershell
mvn test
```
*Expected Result:* `Tests run: 31, Failures: 0, Errors: 0, Skipped: 0` (`BUILD SUCCESS`).

### 3. Launch Interactive FlatLaf Desktop Application
```powershell
mvn exec:java
```

### 4. Build Standalone Executable Fat JAR
```powershell
mvn package
java -jar target/smartcity360-1.0.0.jar
```

---

## 📂 Project Repository Layout

```
SmartCity360/
├── pom.xml                                   # Maven JDK 17, FlatLaf 3.4 & JUnit 5 build configuration
├── README.md                                 # Master technical reference & quickstart
├── AGENT_SPEC.md                             # Architectural specification & execution blueprint
├── docs/
│   ├── SmartCity360_Final_Report.md          # 15-mark answers for Parts A-E + Part F synthesis
│   ├── UML_Diagrams.puml                     # Pure PlantUML class & architecture diagrams
│   ├── UML_Diagrams.md                       # Mermaid.js & PlantUML diagram specifications
│   └── Video_Demo_and_VIVA_Script.md         # Minute-by-minute video guide & 15 VIVA Q&As
└── src/
    ├── main/java/com/urbantech/smartcity360/
    │   ├── SmartCity360App.java              # Main desktop application launcher
    │   ├── partA_decorator/                  # Pure GoF Decorator Pattern domain classes
    │   ├── partB_composite/                  # Pure GoF Composite Pattern domain classes
    │   ├── partC_chain/                      # Pure GoF Chain of Responsibility domain classes
    │   ├── partD_flyweight/                  # Pure GoF Flyweight Pattern domain classes
    │   ├── partE_interpreter/                # Pure GoF Interpreter Pattern domain classes
    │   └── ui/                               # FlatLaf Swing interactive GUI panels & log bus
    └── test/java/com/urbantech/smartcity360/
        ├── PartADecoratorTest.java           # 7 tests for dynamic security wrapper stacking
        ├── PartBCompositeTest.java           # 4 tests for recursive power aggregation & cutoffs
        ├── PartCChainTest.java               # 5 tests for complaint routing & dynamic bypasses
        ├── PartDFlyweightTest.java           # 4 tests for streetlight pooling & 95%+ RAM savings
        ├── PartEInterpreterTest.java         # 8 tests for mini-DSL parsing & AST interpretation
        └── SmartCityDashboardSmokeTest.java  # 3 tests for event bus & UI component initialization
```

---

## 📚 Complete Academic Documentation

Full academic submissions and presentation resources are located under the [`docs/`](docs/) directory:
1. **[Academic Final Report (`docs/SmartCity360_Final_Report.md`)](docs/SmartCity360_Final_Report.md):**
   - In-depth, 15-mark answers to Question 1 (3 marks), Question 2 (4 marks), and Question 3 (8 marks) for Parts A, B, C, D, and E.
   - Rigorous mathematical formulations for Flyweight heap memory conservation.
   - SOLID design principles alignment matrix and architectural trade-off evaluations.
2. **[Standalone UML Diagrams (`docs/UML_Diagrams.puml`)](docs/UML_Diagrams.puml):**
   - High-fidelity PlantUML and Mermaid.js diagrams for all individual patterns and the Master Integrated System Architecture.
3. **[Video Demo & VIVA Defense Script (`docs/Video_Demo_and_VIVA_Script.md`)](docs/Video_Demo_and_VIVA_Script.md):**
   - Minute-by-minute 12–14 minute recording guide indicating exact IDE classes to show and Swing UI controls to click.
   - Top 15 anticipated VIVA examiner questions with comprehensive model answers.

---
*UrbanTech Solutions © 2026. All rights reserved.*
