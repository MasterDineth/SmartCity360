# SmartCity360 – Standalone UML Class Diagrams
## Complete Mermaid.js & PlantUML Specifications

This document contains standalone, publication-ready UML diagrams for all five Gang of Four design patterns and the Master Integrated System Architecture in both **Mermaid.js** and **PlantUML**.

---

## 1. Part A: Decorator Pattern (Data Stream Security)

### Mermaid Diagram
```mermaid
classDiagram
    direction TB
    class DataStream {
        <<interface>>
        +transmit() String
        +getExecutionTrace() List~String~
        +getLayerDescription() String
    }
    class EnergyMeterStream {
        -String meterId
        -double kilowattHours
        -String timestamp
        -List~String~ executionTrace
        +transmit() String
        +getExecutionTrace() List~String~
        +getLayerDescription() String
    }
    class StreamDecorator {
        <<abstract>>
        #DataStream wrappedStream
        +StreamDecorator(DataStream wrappedStream)
        +transmit() String
        +getExecutionTrace() List~String~
        +getLayerDescription() String
    }
    class EncryptionDecorator {
        +transmit() String
        +getLayerDescription() String
    }
    class AuthenticationDecorator {
        -String secretKey
        +transmit() String
        +getLayerDescription() String
        -computeHmacSha256(String, String) String
    }
    class CompressionDecorator {
        +transmit() String
        +getLayerDescription() String
        -gzipCompress(byte[]) byte[]
    }

    DataStream <|.. EnergyMeterStream : implements
    DataStream <|.. StreamDecorator : implements
    StreamDecorator <|-- EncryptionDecorator : extends
    StreamDecorator <|-- AuthenticationDecorator : extends
    StreamDecorator <|-- CompressionDecorator : extends
    StreamDecorator o--> DataStream : wraps (1..1)
```

### PlantUML
```plantuml
@startuml
interface DataStream {
    +transmit() : String
    +getExecutionTrace() : List<String>
    +getLayerDescription() : String
}

class EnergyMeterStream implements DataStream {
    -meterId : String
    -kilowattHours : double
    -timestamp : String
    -executionTrace : List<String>
    +transmit() : String
    +getExecutionTrace() : List<String>
    +getLayerDescription() : String
}

abstract class StreamDecorator implements DataStream {
    #wrappedStream : DataStream
    +StreamDecorator(wrappedStream : DataStream)
    +transmit() : String
    +getExecutionTrace() : List<String>
    +getLayerDescription() : String
}

class EncryptionDecorator extends StreamDecorator {
    +transmit() : String
    +getLayerDescription() : String
}

class AuthenticationDecorator extends StreamDecorator {
    -secretKey : String
    +transmit() : String
    +getLayerDescription() : String
    -computeHmacSha256(data : String, key : String) : String
}

class CompressionDecorator extends StreamDecorator {
    +transmit() : String
    +getLayerDescription() : String
    -gzipCompress(data : byte[]) : byte[]
}

StreamDecorator o--> DataStream : wraps (1..1)
@enduml
```

---

## 2. Part B: Composite Pattern (Smart Zone Power Tree)

### Mermaid Diagram
```mermaid
classDiagram
    direction TB
    class ZoneType {
        <<enumeration>>
        CITY
        DISTRICT
        STREET
        BUILDING
        -String description
        +getDescription() String
    }
    class SmartZoneComponent {
        <<abstract>>
        +getName()* String
        +getZoneType()* ZoneType
        +getPowerUsageKw()* double
        +shutdownPower(List~String~ traceLog)* void
        +restorePower(List~String~ traceLog)* void
        +reportUsage(int indentLevel)* String
        +add(SmartZoneComponent c) void
        +remove(SmartZoneComponent c) void
        +getChildren() List~SmartZoneComponent~
    }
    class BuildingLeaf {
        -String name
        -String buildingCategory
        -double nominalKw
        -boolean powerActive
        +BuildingLeaf(String, String, double)
        +getName() String
        +getZoneType() ZoneType
        +getPowerUsageKw() double
        +shutdownPower(List~String~ traceLog) void
        +restorePower(List~String~ traceLog) void
        +reportUsage(int indentLevel) String
        +isPowerActive() boolean
        +getBuildingCategory() String
    }
    class ZoneComposite {
        -String name
        -ZoneType zoneType
        -List~SmartZoneComponent~ children
        +ZoneComposite(String, ZoneType)
        +getName() String
        +getZoneType() ZoneType
        +getPowerUsageKw() double
        +shutdownPower(List~String~ traceLog) void
        +restorePower(List~String~ traceLog) void
        +reportUsage(int indentLevel) String
        +add(SmartZoneComponent c) void
        +remove(SmartZoneComponent c) void
        +getChildren() List~SmartZoneComponent~
    }

    SmartZoneComponent <|-- BuildingLeaf : extends
    SmartZoneComponent <|-- ZoneComposite : extends
    ZoneComposite o--> SmartZoneComponent : children (0..*)
    SmartZoneComponent --> ZoneType : has
```

### PlantUML
```plantuml
@startuml
enum ZoneType {
    CITY
    DISTRICT
    STREET
    BUILDING
}

abstract class SmartZoneComponent {
    +getName()* : String
    +getZoneType()* : ZoneType
    +getPowerUsageKw()* : double
    +shutdownPower(traceLog : List<String>)* : void
    +restorePower(traceLog : List<String>)* : void
    +reportUsage(indentLevel : int)* : String
    +add(component : SmartZoneComponent) : void
    +remove(component : SmartZoneComponent) : void
    +getChildren() : List<SmartZoneComponent>
}

class BuildingLeaf extends SmartZoneComponent {
    -name : String
    -buildingCategory : String
    -nominalKw : double
    -powerActive : boolean
    +BuildingLeaf(name, category, nominalKw)
    +getPowerUsageKw() : double
    +shutdownPower(traceLog) : void
    +restorePower(traceLog) : void
    +reportUsage(indentLevel) : String
}

class ZoneComposite extends SmartZoneComponent {
    -name : String
    -zoneType : ZoneType
    -children : List<SmartZoneComponent>
    +ZoneComposite(name, zoneType)
    +getPowerUsageKw() : double
    +shutdownPower(traceLog) : void
    +restorePower(traceLog) : void
    +reportUsage(indentLevel) : String
    +add(c) : void
    +remove(c) : void
    +getChildren() : List<SmartZoneComponent>
}

ZoneComposite o--> SmartZoneComponent : children (0..*)
SmartZoneComponent --> ZoneType
@enduml
```

---

## 3. Part C: Chain of Responsibility Pattern (Complaint Escalation)

### Mermaid Diagram
```mermaid
classDiagram
    direction TB
    class SeverityLevel {
        <<enumeration>>
        LOW
        MEDIUM
        HIGH
        EXTREME
        -int level
        -String description
        +getLevel() int
        +getDescription() String
    }
    class ComplaintRequest {
        -String complaintId
        -String citizenName
        -String category
        -String description
        -SeverityLevel severity
        +getComplaintId() String
        +getCitizenName() String
        +getCategory() String
        +getDescription() String
        +getSeverity() SeverityLevel
    }
    class ComplaintResolution {
        -boolean handled
        -String handledByRole
        -String actionTaken
        -List~String~ escalationTrail
        +isHandled() boolean
        +getHandledByRole() String
        +getActionTaken() String
        +getEscalationTrail() List~String~
    }
    class ComplaintHandler {
        <<abstract>>
        #ComplaintHandler nextHandler
        #String handlerName
        #boolean active
        +setNextHandler(ComplaintHandler) ComplaintHandler
        +handle(ComplaintRequest, List~String~) ComplaintResolution
        +getRole()* String
        #canHandle(ComplaintRequest)* boolean
        #process(ComplaintRequest, List~String~)* ComplaintResolution
        +setActive(boolean) void
        +isActive() boolean
    }
    class LocalStaffHandler {
        +getRole() String
        #canHandle(ComplaintRequest) boolean
        #process(...) ComplaintResolution
    }
    class SupervisorHandler {
        +getRole() String
        #canHandle(ComplaintRequest) boolean
        #process(...) ComplaintResolution
    }
    class CityManagerHandler {
        +getRole() String
        #canHandle(ComplaintRequest) boolean
        #process(...) ComplaintResolution
    }

    ComplaintHandler <|-- LocalStaffHandler : extends
    ComplaintHandler <|-- SupervisorHandler : extends
    ComplaintHandler <|-- CityManagerHandler : extends
    ComplaintHandler o--> ComplaintHandler : nextHandler (0..1)
    ComplaintHandler ..> ComplaintRequest : evaluates
    ComplaintHandler ..> ComplaintResolution : produces
    ComplaintRequest --> SeverityLevel : has
```

---

## 4. Part D: Flyweight Pattern (Streetlight Memory Optimization)

### Mermaid Diagram
```mermaid
classDiagram
    direction LR
    class StreetLightFlyweight {
        <<interface>>
        +renderOperation(poleId, lat, lon, isOn, dimming) String
        +getIntrinsicSummary() String
        +getApproximateIntrinsicBytes() long
    }
    class SharedLightModel {
        -String modelCode
        -String manufacturer
        -int wattage
        -int lumens
        -int colorTempK
        -byte[] firmwareLutPayload
        ~SharedLightModel(...)
        +renderOperation(...) String
        +getIntrinsicSummary() String
        +getApproximateIntrinsicBytes() long
    }
    class StreetLightFactory {
        -Map~String, StreetLightFlyweight~ FLYWEIGHT_POOL$
        +getLightModel(code, mfr, watts, lumens, k)$ StreetLightFlyweight
        +getPoolSize()$ int
        +clearPool()$ void
    }
    class StreetLightContext {
        -String poleId
        -String streetName
        -double latitude
        -double longitude
        -boolean poweredOn
        -int brightnessPercent
        -StreetLightFlyweight sharedModel
        +StreetLightContext(...)
        +operate() String
        +getApproximateExtrinsicBytes() long
    }

    StreetLightFlyweight <|.. SharedLightModel : implements
    StreetLightFactory ..> StreetLightFlyweight : caches & supplies
    StreetLightContext o--> StreetLightFlyweight : references (8 bytes)
```

---

## 5. Part E: Interpreter Pattern (Policy DSL Evaluator)

### Mermaid Diagram
```mermaid
classDiagram
    direction TB
    class PolicyContext {
        -Map~String, Object~ attributes
        -List~String~ evaluationTrace
        +setAttribute(String, Object) void
        +getAttribute(String) Object
        +hasAttribute(String) boolean
        +addTrace(String) void
        +getEvaluationTrace() List~String~
        +clearTrace() void
    }
    class Expression {
        <<interface>>
        +interpret(PolicyContext context)* boolean
        +toASTString(int indent)* String
    }
    class ComparisonExpression {
        -String variableName
        -String operator
        -String rawTargetValue
        +ComparisonExpression(String, String, String)
        +interpret(PolicyContext) boolean
        +toASTString(int) String
        -compareNumeric(double, String, double) boolean
        -compareString(String, String, String) boolean
    }
    class AndExpression {
        -Expression left
        -Expression right
        +AndExpression(Expression, Expression)
        +interpret(PolicyContext) boolean
        +toASTString(int) String
    }
    class OrExpression {
        -Expression left
        -Expression right
        +OrExpression(Expression, Expression)
        +interpret(PolicyContext) boolean
        +toASTString(int) String
    }
    class NotExpression {
        -Expression expression
        +NotExpression(Expression)
        +interpret(PolicyContext) boolean
        +toASTString(int) String
    }
    class PolicyDSLParser {
        +parse(String dslRule) Expression
        -tokenize(String) List~Token~
    }

    Expression <|.. ComparisonExpression : implements
    Expression <|.. AndExpression : implements
    Expression <|.. OrExpression : implements
    Expression <|.. NotExpression : implements
    AndExpression o--> Expression : left, right
    OrExpression o--> Expression : left, right
    NotExpression o--> Expression : expression
    PolicyDSLParser ..> Expression : constructs
    Expression ..> PolicyContext : evaluates against
```
