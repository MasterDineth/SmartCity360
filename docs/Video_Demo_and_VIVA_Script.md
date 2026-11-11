# SmartCity360 — Video Demonstration & VIVA Defense Guide
## 10–15 Minute Minute-by-Minute Video Script & 15 Expected VIVA Questions
**Course:** Object Oriented Design Patterns (OODP II)  
**Enterprise Entity:** UrbanTech Solutions  
**Platform:** SmartCity360 (Java 17, FlatLaf, Maven, JUnit 5)  

---

## 1. Video Recording Setup & Preparation Checklist

Before initiating your screen recording software (e.g., OBS Studio, Loom, or Zoom):
1. **Resolution & Scaling:** Set screen display to 1080p (1920x1080) with 100% or 125% scaling so code and UI text remain crisp.
2. **IDE Workspace:** Open IntelliJ IDEA or VS Code with `SmartCity360` loaded.
3. **Terminal Ready:** Open terminal inside `c:\Users\dinet\Documents\Assignments\OODP II\SmartCity360` with Maven on PATH.
4. **Clean Build:** Run `mvn clean test` beforehand to verify all 32 automated tests pass.

---

## 2. Minute-by-Minute Video Demonstration Script (Target Duration: 12–14 Minutes)

```
[00:00 - 01:30] Phase 1: Project Overview & Automated Test Verification
[01:30 - 03:45] Phase 2: Part A – Decorator Pattern (Data Stream Security)
[03:45 - 06:00] Phase 3: Part B – Composite Pattern (Smart Zone Power Hierarchy)
[06:00 - 08:15] Phase 4: Part C – Chain of Responsibility (Complaint Escalation)
[08:15 - 10:30] Phase 5: Part D – Flyweight Pattern (High-Density Streetlight Pool)
[10:30 - 12:30] Phase 6: Part E – Interpreter Pattern (Policy Mini-DSL Parser)
[12:30 - 14:00] Phase 7: Architectural Summary, SOLID Recap & Conclusion
```

---

### [00:00 - 01:30] Segment 1: Executive Introduction & Build Verification
- **What to show on screen:**
  - IDE showing the project structure (`partA_decorator`, `partB_composite`, `partC_chain`, `partD_flyweight`, `partE_interpreter`, `ui`).
  - Terminal executing `mvn clean test`.
- **Script to Speak:**
  > *"Hello, examiners. Welcome to the technical demonstration of SmartCity360 – an integrated Smart City Management Platform developed by UrbanTech Solutions for the Object Oriented Design Patterns module.*  
  > *My name is [Your Name], and today I will demonstrate how we engineered five pure Gang of Four design patterns to address real-world smart city infrastructure challenges: Decorator, Composite, Chain of Responsibility, Flyweight, and Interpreter.*  
  > *Before launching our GUI, let us run `mvn clean test` in the terminal to verify the automated test suite. As you can see, all 32 unit and integration tests across all five domain packages execute and pass with zero failures and zero errors.*  
  > *Now, let us launch our desktop application via `mvn exec:java`."*

---

### [01:30 - 03:45] Segment 2: Part A – Decorator Pattern (Dynamic Security Wrappers)
- **What to show on screen:**
  - Switch to the launched application window.
  - Navigate to the **"Part A: Decorator (Energy Security)"** tab.
  - Note that the tab auto-executes transmission on launch, immediately populating the payload and trace log.
  - Highlight the Meter ID (`MTR-104-METRO`), kWh spinner (`42.85`), and the layer checkboxes.
  - Highlight the **Decorator Wrapping Order:** dropdown with options:
    1. `Standard Order (Encrypt -> Authenticate -> Compress)`
    2. `Reverse Order (Compress -> Authenticate -> Encrypt)`
- **Actions to Perform:**
  1. Uncheck all decorators. Click **"Transmit Secured Telemetry Stream"**.
     - Point out the raw JSON payload in the **Final Transmitted Wire Payload** text area.
     - Point out the step-by-step trace in the dedicated **Part A Local Execution Trace Log** panel.
  2. Select `[x] EncryptionDecorator (AES-256 / Base64)` and `[x] AuthenticationDecorator (HMAC-SHA256)`. Click **"Transmit Secured Telemetry Stream"**.
     - Point out the `AUTH[HMAC=...]` signature header and the `ENC[AES256](...)` cipher.
  3. Select all three checkboxes with `Standard Order (Encrypt -> Authenticate -> Compress)`.
     - Click **"Transmit Secured Telemetry Stream"**. Show the payload wrapped in `COMP[GZIP,ratio=XX%](...)`.
  4. Switch to `Reverse Order (Compress -> Authenticate -> Encrypt)` to show dynamic pipeline reordering.
     - Click **"Transmit Secured Telemetry Stream"** and demonstrate that compression occurs before encryption.
- **Script to Speak:**
  > *"In Part A, SmartCity360 must secure continuous telemetry streams emitted by smart energy meters. Under a naive inheritance approach, supporting all combinations and orderings of encryption, authentication, and compression would cause a combinatorial subclass explosion of 15+ subclasses.*  
  > *Instead, we implemented the GoF Decorator Pattern using `DataStream` as our component interface, `EnergyMeterStream` as the concrete component, and `StreamDecorator` as the base decorator.*  
  > *Notice our visual pipeline breadcrumb updating live as I toggle layers. When I transmit with only the raw stream, the output is pure JSON. When I enable encryption, authentication, and compression, each decorator dynamically wraps the payload. In our dedicated Part A Local Execution Trace Log, you can observe the exact chronological execution: EnergyMeterStream emits raw data, EncryptionDecorator encrypts with AES-256, AuthenticationDecorator signs with HMAC-SHA256, and CompressionDecorator compresses with GZIP.*  
  > *Furthermore, we can switch the wrapping order from Standard to Reverse with a single dropdown selection, demonstrating true runtime composition.*  
  > *This satisfies the Open/Closed Principle: we can introduce new security wrappers without altering a single line of the core energy meter."*

---

### [03:45 - 06:00] Segment 3: Part B – Composite Pattern (Hierarchical Smart Zones)
- **What to show on screen:**
  - Click on the **"Part B: Composite (Smart Zones)"** tab.
  - Highlight the compact single-row top toolbar merging real-time status indicators (`Total Grid Load: 800.0 kW` | `Selected: [CITY] MetroSmartCity (800.0 kW)`) and action buttons.
  - Expand the JTree nodes (`MetroSmartCity` -> `North District` & `South District` -> `Streets` -> `Buildings`).
- **Actions to Perform:**
  1. Click **"Report Usage"**.
     - Show the formatted ASCII tree in the "Recursive Hierarchy Usage Tree (reportUsage)" panel with rolled-up power totals ($800.0\text{ kW}$).
  2. Select `North District` in the tree. Click **"Shutdown Power"**.
     - Show North District's buildings drop to `0.0 kW` (colored red in the tree).
     - Show that South District remains active ($300.0\text{ kW}$).
     - Point out that Total Grid Load rolled up dropped from $800.0\text{ kW}$ to $300.0\text{ kW}$.
     - Show the detailed cascade log in the "Composite Traversal Log (shutdownPower / restorePower)" panel.
  3. Select `North District` and click **"Restore Power"**. Total returns to $800.0\text{ kW}$.
  4. Click **"+ Add Building / Zone"** and add a new research building (e.g., $100\text{ kW}$) under `Innovation Avenue`. Show the power update automatically to $900.0\text{ kW}$.
- **Script to Speak:**
  > *"Part B addresses the structural hierarchy of our city: City contains Districts, Districts contain Streets, and Streets contain Buildings.*  
  > *By applying the GoF Composite Pattern, `SmartZoneComponent` defines a uniform interface for both individual facilities (`BuildingLeaf`) and composite zones (`ZoneComposite`).*  
  > *When I click 'Report Usage', the method recursively traverses the composite tree, calculating aggregated electrical consumption at each node. Notice the total city demand is 800 kW.*  
  > *Now watch what happens when I select 'North District' and click 'Shutdown Power'. The command cascades down exclusively to North District's sub-nodes. Tech Tower A and Civic Data Center disconnect their breakers, dropping to 0 kW, while South District remains completely unaffected. The total city consumption rolls up to 300 kW automatically.*  
  > *This demonstrates polymorphic uniformity: client code treats single buildings and entire metropolitan areas identically."*

---

### [06:00 - 08:15] Segment 4: Part C – Chain of Responsibility (Complaint Escalation)
- **What to show on screen:**
  - Click on the **"Part C: Chain of Resp. (Complaints)"** tab.
  - Highlight the streamlined single-row citizen complaint form:
    - Read-only auto-incrementing `Complaint ID:` (`CMP-0101`)
    - `Severity Tier:` dropdown (`LOW`, `MEDIUM`, `HIGH`, `EXTREME`)
    - `Incident Description:` (`Water main pressure surge on 4th Avenue`)
    - Clean **"Submit Complaint"** button.
  - Highlight the 4 visual authority cards:
    - `Tier 1: Local Staff (Handles: LOW)`
    - `Tier 2: District Supervisor (Handles: MEDIUM)`
    - `Tier 3: City Manager (Handles: HIGH)`
    - `Fallback: Executive / Unhandled (Handles: EXTREME (Chain Exhausted))`
  - Note that a default sample complaint executes automatically on launch, populating the status banner and audit trail.
- **Actions to Perform:**
  1. Select Severity `LOW`. Click **"Submit Complaint"**.
     - Local Staff card turns **Green (Resolved)**. Banner displays "RESOLVED by Local Staff (Metro Local Crew)".
  2. Change Severity to `MEDIUM`. Click **"Submit Complaint"**.
     - Local Staff card turns **Yellow (Escalated)**, Supervisor card turns **Green (Resolved)**.
  3. Change Severity to `HIGH`. Click **"Submit Complaint"**.
     - Local Staff and Supervisor cards turn **Yellow (Escalated)**, City Manager turns **Green (Resolved)**.
  4. Change Severity to `EXTREME`. Click **"Submit Complaint"**.
     - All 3 municipal handlers escalate; Fallback card turns **Red (Chain Exhausted)**.
  5. Uncheck `[ ] Tier 2: Supervisor (Active)`. Submit a `MEDIUM` complaint.
     - Show Local Staff escalate, Supervisor card display **Orange (BYPASS / OFFLINE)**, and the request exhaust cleanly to fallback.
- **Script to Speak:**
  > *"Part C manages citizen complaints. Hardcoding routing logic tightly couples complaint submission to concrete departments. Instead, we use the GoF Chain of Responsibility Pattern.*  
  > *Our chain is configured as: `LocalStaffHandler` -> `SupervisorHandler` -> `CityManagerHandler`, backed by an executive fallback mechanism.*  
  > *When a citizen submits a LOW severity issue, Local Staff handles it immediately. When we submit a MEDIUM severity water burst, Local Staff recognizes that it exceeds their authority threshold, escalates it, and District Supervisor resolves it.*  
  > *For a HIGH severity emergency, it escalates to the City Manager. And if an unprecedented EXTREME event occurs, the request traverses the entire chain without crashing, cleanly exhausting to our municipal emergency fallback.*  
  > *Furthermore, if the District Supervisor is offline, I can uncheck the supervisor active toggle. The chain automatically logs a BYPASS event and forwards the request to the next authority."*

---

### [08:15 - 10:30] Segment 5: Part D – Flyweight Pattern (Streetlight Memory Optimization)
- **What to show on screen:**
  - Click on the **"Part D: Flyweight (Streetlights)"** tab.
  - Highlight the clean **Pole Count:** selector dropdown (`10,000 Poles`, `50,000 Poles`, `100,000 Poles`) and the **"Run Flyweight Benchmark"** button.
  - Point out that the 50,000 pole benchmark executed automatically on startup.
  - Highlight the 5 KPI summary cards:
    - Total Streetlights: `50,000`
    - Flyweight Pool Size: `4 models`
    - Unoptimized RAM: `119.78 MB`
    - Flyweight RAM: `5.35 MB`
    - **RAM Saved: 95.53%**
  - Highlight the expanded physical streetlight JTable occupying 80% vertical window height.
- **Actions to Perform:**
  1. Select **"50,000 Poles"** (or switch to **"100,000 Poles"** and click **"Run Flyweight Benchmark"**).
     - Point out the KPI metrics verifying over 95.5% memory savings.
  2. Scroll through the JTable:
     - Point to the Extrinsic columns (Pole ID, Street, Coordinates, Power, Brightness).
     - Point to the Intrinsic columns (Model Code, Manufacturer, Optics).
     - Point to the **Shared Flyweight (@Hash)** column (e.g., `@766220FD`) showing that consecutive streetlight rows share the exact same object reference in memory.
  3. Select a row and click **"Toggle Selected Pole Power State (Extrinsic Mutation)"** or **"Cycle Selected Pole Brightness (100% -> 50% -> 20%)"**.
     - Show the state update to OFF / dimmed in the table and trace, proving extrinsic mutation modifies only that pole's context without affecting the shared flyweight model.
- **Script to Speak:**
  > *"In Part D, SmartCity360 manages 50,000 to 100,000 smart streetlights. Each streetlight requires a 2 KB photometric lookup table (LUT) and optical configuration. In a naive implementation, duplicating this heavy data across 50,000 objects consumes over 119 MB of heap memory.*  
  > *We implemented the GoF Flyweight Pattern: `SharedLightModel` stores the immutable intrinsic state—the model code, lumens, and the 2 KB LUT. `StreetLightContext` stores only the lean extrinsic state—pole ID, latitude, longitude, and brightness level.*  
  > *Notice the 'Shared Flyweight (@Hash)' column in our table: every single one of these 50,000 poles shares one of just four memory references managed by `StreetLightFactory`.*  
  > *Our live memory benchmark confirms: unoptimized allocation would consume 119.8 MB, whereas our flyweight pool consumes only 5.35 MB—achieving an extraordinary 95.53% heap memory reduction."*

---

### [10:30 - 12:30] Segment 6: Part E – Interpreter Pattern (Policy Mini-DSL Parser)
- **What to show on screen:**
  - Click on the **"Part E: Interpreter (Policy DSL)"** tab.
  - Show the preset dropdown, active DSL rule text box, sensor simulator controls, AST hierarchy box, and compact verdict banner.
- **Actions to Perform:**
  1. Select Preset 1: `ZONE == 'RESIDENTIAL' AND DECIBELS > 55`.
     - Set `ZONE = RESIDENTIAL`, `DECIBELS = 65.0`. Click **"Parse & Evaluate Policy"**.
     - Show the compact **Red Violation Banner**: `[VIOLATION] POLICY VIOLATION DETECTED (Condition Evaluated to TRUE)`.
     - Point out the AST structure (`[NonTerminal: AND]`, `[Terminal] ZONE`, `[Terminal] DECIBELS`).
     - Show the step-by-step boolean trace: `true AND true -> true`.
  2. Change `DECIBELS` to `42.0` (quiet). Click **"Parse & Evaluate Policy"**.
     - Show the compact **Green Compliant Banner**: `[OK] ENVIRONMENT COMPLIANT (Condition Evaluated to FALSE)`.
  3. Select Preset 2: `ZONE == 'SCHOOL_ZONE' AND SPEED_KMH > 30`.
     - Evaluate with `SPEED_KMH = 45` (Violation) and `25` (Compliant).
  4. Select Preset 4: `ZONE == 'COMMERCIAL' AND PARKING_MINS > 120 AND NOT PERMIT == 'VALID'`.
     - Demonstrate `PERMIT = EXPIRED` (Violation) vs `PERMIT = VALID` (Compliant).
- **Script to Speak:**
  > *"In Part E, municipal bylaws change frequently. Hardcoding these rules into conditional statements requires continuous recompilation.*  
  > *We implemented the GoF Interpreter Pattern along with a recursive-descent parser (`PolicyDSLParser`). The parser builds an Abstract Syntax Tree (AST) composed of `ComparisonExpression` terminal nodes and `AndExpression`, `OrExpression`, and `NotExpression` non-terminal nodes.*  
  > *When I evaluate our residential noise curfew rule against a sensor reading of 65 decibels, the AST evaluates both terminals to true, resulting in an immediate violation alert.*  
  > *When noise drops to 42 decibels, the condition evaluates to false, displaying compliant status. Notice the complete evaluation trace: every AST node logs its intermediate boolean result, ensuring full auditability and explainability for civic enforcement."*

---

### [12:30 - 14:00] Segment 7: SOLID Alignment, Theme Toggle & Conclusion
- **What to show on screen:**
  - Click the **"Light Theme"** / **"Dark Theme"** button in the top header bar to demonstrate instant FlatLaf look-and-feel switching.
  - Explain the simplified, full-height UI layout: the tabbed pane occupies the full central window area, giving each pattern maximum vertical space for data inspection and controls, while each tab provides its own dedicated local execution trace log.
  - Note that `ExecutionLogBus` operates in the background as a decoupled thread-safe event bus, streaming events for system logging and automated smoke test verification.
  - Return to IDE showing `pom.xml` and package layout.
- **Script to Speak:**
  > *"To conclude our demonstration, SmartCity360 illustrates how the five classic Gang of Four design patterns work in concert to build maintainable, resilient, and high-performance municipal software.*  
  > *Each pattern directly reinforces SOLID principles: Decorator ensures Open/Closed extensibility for security; Composite provides Liskov Substitution and Single Responsibility for urban zoning; Chain of Responsibility decouples citizen dispatching; Flyweight optimizes memory scalability by 95%; and Interpreter decouples business policy rules from core application logic.*  
  > *Our FlatLaf desktop interface features a full-height center layout, dedicated per-tab execution logs, and seamless dark and light theme toggling.*  
  > *Thank you for your time, and I look forward to answering your questions in the VIVA defense session."*

---

## 3. Top 15 Expected VIVA Questions & Comprehensive Model Answers

### Question 1: What is the primary intent of the GoF Decorator Pattern, and why is it preferred over class inheritance in Part A?
**Model Answer:**  
The GoF Decorator pattern attaches additional responsibilities to an object dynamically at runtime without altering its interface. In Part A, we chose Decorator over inheritance because inheritance creates static, compile-time relationships that cause a combinatorial class explosion. If we used inheritance to support all combinations of encryption, authentication, and compression, we would need 15+ specialized subclasses, and supporting dynamic reordering at runtime would be impossible. The Decorator pattern relies on object composition: every decorator implements `DataStream` and maintains a `wrappedStream` pointer, allowing arbitrary nesting and ordering at runtime.

---

### Question 2: In Part B, what is the architectural difference between a Leaf and a Composite, and how did you resolve the "Transparency vs. Safety" trade-off?
**Model Answer:**  
In the Composite pattern, a **Leaf** (`BuildingLeaf`) represents an atomic component that has no children and performs operations directly on its local domain state (such as opening its circuit breaker). A **Composite** (`ZoneComposite`) represents a container holding subordinate components and implements operations by recursively delegating them to its children.  
In terms of the classic GoF trade-off:
- **Transparency** places child management methods (`add`, `remove`, `getChildren`) in the root abstract class (`SmartZoneComponent`), allowing clients to treat leaves and composites identically.
- **Safety** places child management methods exclusively in `ZoneComposite`, preventing invalid method calls on leaves at compile-time.  
We adopted the **Transparency approach with safe default implementations**: `SmartZoneComponent` defines `add()` and `remove()`, but default implementations throw `UnsupportedOperationException`. This retains uniform polymorphism across clients while delivering explicit fail-fast safety.

---

### Question 3: How does the Composite pattern satisfy the Open/Closed Principle when the city expands?
**Model Answer:**  
SmartCity360 is open for extension but closed for modification. When the municipality constructs a new district, street, or facility, new `ZoneComposite` or `BuildingLeaf` instances are added dynamically to the tree via `composite.add(newComponent)`. The existing client code—such as the total power aggregator `getPowerUsageKw()`, emergency shutdown `shutdownPower()`, and report generator `reportUsage()`—requires zero modification or recompilation because it operates polymorphically on the abstract `SmartZoneComponent` type.

---

### Question 4: How does the Chain of Responsibility pattern decouple the sender of a complaint from its receiver in Part C?
**Model Answer:**  
In Part C, the citizen submission portal creates an immutable `ComplaintRequest` object and passes it solely to the head of the chain (`localStaff.handle(request, trail)`). The client has zero direct coupling to `SupervisorHandler`, `CityManagerHandler`, or any external emergency bureau. Each handler inspects the request's `SeverityLevel` and either processes it if within its authority, or forwards it to its configured successor `nextHandler`. The sender does not know—and does not need to know—which specific municipal tier ultimately resolves the complaint.

---

### Question 5: What happens in Part C if a request cannot be handled by any handler in the chain?
**Model Answer:**  
In classic GoF, a request can fall off the end of an unhandled chain silently. In SmartCity360, we engineered a defensive municipal fallback: when `nextHandler == null`, the template method `handle()` intercepts the unhandled request, logs a `[CHAIN-EXHAUSTED]` event into the audit trail, and returns an explicit `ComplaintResolution(false, "UNRESOLVED / EXTERNAL ESCALATION", "Forwarded to Emergency Operations Center / External State Bureau for Manual Intervention.", trail)`. This guarantees total traceability and prevents unhandled request dropouts.

---

### Question 6: How does Part C handle dynamic runtime bypassing of municipal handlers?
**Model Answer:**  
Each handler extends `ComplaintHandler`, which maintains a `protected boolean active = true;` flag with `setActive(boolean)` mutator. When an administrative department is offline, `active` is set to false. During traversal, the handler checks `if (active && canHandle(request))`. If inactive, it records an explicit `[BYPASSED: Handler is OFFLINE]` audit log into the escalation trail and immediately forwards the request to `nextHandler`.

---

### Question 7: What constitutes Intrinsic vs. Extrinsic state in the GoF Flyweight Pattern in Part D?
**Model Answer:**  
- **Intrinsic State:** Invariant, context-independent data that is identical across multiple objects and can be shared safely. In `SharedLightModel`, intrinsic state includes `modelCode`, `manufacturer`, `wattage`, `lumens`, `colorTempK`, and the heavy **2048-byte photometric LUT array**.
- **Extrinsic State:** Unique, context-dependent data that varies with every physical installation and cannot be shared. In `StreetLightContext`, extrinsic state includes `poleId`, `streetName`, `latitude`, `longitude`, `poweredOn`, and `brightnessPercent`.

---

### Question 8: How does the Flyweight Pattern achieve over 95% memory savings in Part D?
**Model Answer:**  
In an unoptimized architecture, each streetlight pole duplicates the heavy 2400-byte intrinsic profile plus its 112-byte extrinsic data ($\approx 2512\text{ bytes per pole}$). For 50,000 poles, this requires $50,000 \times 2512 \approx 125.6\text{ MB}$.  
With Flyweight pooling, the 4 unique `SharedLightModel` instances are instantiated once in `StreetLightFactory` ($4 \times 2400 = 9,600\text{ bytes}$). The 50,000 `StreetLightContext` objects store only the 112 bytes of extrinsic state plus an 8-byte reference pointer to the shared model ($50,000 \times 112 = 5,600,000\text{ bytes}$). Total memory is $5.61\text{ MB}$, yielding a quantified memory reduction of:
$$\frac{125.6\text{ MB} - 5.61\text{ MB}}{125.6\text{ MB}} = 95.53\% \text{ RAM saved}$$

---

### Question 9: Is the Flyweight Pattern thread-safe in our implementation?
**Model Answer:**  
Yes, absolutely. Thread-safety is achieved at two levels:
1. **Immutable Flyweights:** `SharedLightModel` has strictly `final` fields and no mutator methods. Multiple worker threads can invoke `renderOperation()` concurrently without race conditions because extrinsic state is passed as method arguments on the call stack.
2. **Thread-Safe Factory Pool:** `StreetLightFactory` uses a `ConcurrentHashMap` with atomic `computeIfAbsent()` to guarantee thread-safe flyweight retrieval and single-instance creation under concurrent load.

---

### Question 10: In Part E, what are Terminal vs. Non-Terminal expressions in the Interpreter Pattern?
**Model Answer:**  
- **Terminal Expression:** Represents leaf nodes of the Abstract Syntax Tree that do not contain child expressions and evaluate directly against context data. In our system, `ComparisonExpression` is a terminal expression; it retrieves a sensor value from `PolicyContext` and evaluates operators like `==`, `!=`, `>`, `<`, `>=`, `<=`.
- **Non-Terminal Expression:** Represents composite nodes of the AST that combine one or more child expressions. `AndExpression`, `OrExpression`, and `NotExpression` are non-terminal expressions that execute logical conjunction, disjunction, and negation across their sub-trees.

---

### Question 11: How does the Interpreter Pattern handle operator precedence in `PolicyDSLParser`?
**Model Answer:**  
We implemented a recursive descent parser that enforces standard Boolean precedence through grammar layering:
1. `OrExpr` (Lowest precedence, evaluates `OR`).
2. `AndExpr` (Middle precedence, evaluates `AND`).
3. `NotExpr` (High precedence, evaluates unary `NOT`).
4. `Primary` (Highest precedence, evaluates parenthesized `( Expression )` or atomic `ComparisonExpression`).  
This ensures that `A OR B AND C` evaluates correctly as `A OR (B AND C)`, and parentheses `(A OR B) AND C` override default precedence.

---

### Question 12: When should you NOT use the GoF Interpreter Pattern?
**Model Answer:**  
The Interpreter pattern should be avoided when:
1. **The grammar is complex or large:** If the language has dozens or hundreds of production rules, creating an object class for every rule results in an unmaintainable class hierarchy. Parser generators like ANTLR or compiler tools should be used instead.
2. **Performance-critical high-throughput parsing:** Deeply nested ASTs involve heavy virtual method dispatching and call stack overhead compared to byte-code compilation or state-machine token evaluation.

---

### Question 13: How does the Observer-like `ExecutionLogBus` work with the GoF domain patterns and UI?
**Model Answer:**  
`ExecutionLogBus` is a thread-safe singleton event bus that implements a decoupled Publisher-Subscriber mechanism. Domain pattern operations record trace messages into local lists. The GUI panels and test harnesses publish these traces to `ExecutionLogBus`, which dispatches them onto the Swing Event Dispatch Thread (EDT) via `SwingUtilities.invokeLater()`.  
In the Phase 4.5 UI architecture, each tab panel hosts its own dedicated local execution trace text area to give users immediate, uncluttered contextual feedback, while `ExecutionLogBus` operates concurrently in the background. This architecture guarantees full auditability and automated smoke test verification while completely isolating our domain pattern logic from Swing GUI presentation components, upholding both the Single Responsibility and Dependency Inversion principles.

---

### Question 14: How does SmartCity360 embody the Dependency Inversion Principle (DIP)?
**Model Answer:**  
DIP states that high-level modules should not depend on low-level modules; both should depend on abstractions. In SmartCity360:
- Presentation panels and client code depend on abstract interfaces (`DataStream`, `SmartZoneComponent`, `ComplaintHandler`, `Expression`, `StreetLightFlyweight`).
- They never directly instantiate or couple to concrete implementations like `EncryptionDecorator`, `BuildingLeaf`, or `ComparisonExpression`.
- Factory methods, parsers, and composition roots inject dependencies cleanly.

---

### Question 15: If municipal requirements changed to add an audit log decorator in Part A and a new zone type in Part B, how much code would need to be changed?
**Model Answer:**  
- **Part A (Decorator):** Zero lines of existing code would change. We would simply author a new class `AuditLogDecorator extends StreamDecorator` and wrap it into the pipeline at runtime.
- **Part B (Composite):** Zero lines of existing code would change. We would add an enum literal to `ZoneType` (e.g., `WARD` or `CAMPUS`) and instantiate `new ZoneComposite("East Campus", ZoneType.CAMPUS)`. All existing recursive methods (`getPowerUsageKw`, `shutdownPower`, `reportUsage`) would handle it automatically.  
This proves our architectural adherence to the Open/Closed Principle.

---
*End of Video Demo & VIVA Script Guide.*
