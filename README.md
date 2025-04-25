# Space Exploration Drone Control System

## Overview
This project is a comprehensive software system designed to control exploration drones in a square-shaped sector of space. It implements a certification program for drone control software as required by the **High Command Control of the Universe and Planets (HICCUP)**.

## System Architecture

### Core Components
1. **Drone Control Module**
    - Spawn, move, and manage exploration drones
    - Implement mining operations for uranium
    - Handle drone navigation (N/S/E/W movements)

2. **Planetary Mapping System**
    - Dynamic planet discovery and mapping
    - Uranium detection and quantity tracking
    - Space station and obstacle management

3. **Hyperspace Network**
    - Hyperspace energy tunnel installation
    - Instantaneous drone transportation
    - Tunnel management (relocation/shutdown)

4. **REST API Interface**
    - Full CRUD operations for drones
    - Command history tracking
    - Planetary and hyperspace tunnel management



## Key Features

### Drone Operations
- **Movement Control**: North, East, South, West navigation
- **Mining System**: Uranium extraction with capacity limits (20 units max)
- **Advanced Commands**:
    - `transport`: Use hyperspace tunnels
    - `explore`: Discover new planets
    - `gohome`: Return to space station

### Planetary System
- Dynamic planet discovery via sensor mapping
- Uranium deposits tracking
- Space station as central hub
- Impassable energy fields as obstacles

### Hyperspace Network
- Unidirectional planet-to-planet connections
- Tunnel installation and management
- Instant drone transportation

## REST API Endpoints

### Drones Management
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/drones` | GET | List all drones |
| `/drones` | POST | Create new drone |
| `/drones/{drone-id}` | GET | Get specific drone |
| `/drones/{drone-id}` | DELETE | Remove drone |
| `/drones/{drone-id}/commands` | POST | Send command to drone |
| `/drones/{drone-id}/commands` | GET | Get command history |
 

### Planetary System
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/planets` | GET | List all planets |
| `/planets/{planet-id}` | GET | Get planet details |
| `/planets/{planet-id}/replenish` | POST | Replenish uranium |

### Hyperspace Network
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/tunnels` | POST | Install new tunnel |
| `/tunnels` | GET | List all tunnels |
| `/tunnels/{tunnel-id}` | GET | Get tunnel details |
| `/tunnels/{tunnel-id}` | DELETE | Shutdown tunnel |
| `/tunnels/{tunnel-id}/relocate` | PUT | Relocate tunnel |


## Business Rules

### Movement Constraints
- Drones cannot pass sector borders
- Energy fields block movement
- Space station is the only spawn point

### Mining Operations
- Fair mining policy (least-loaded drones first)
- Maximum load capacity (20 units)
- Mining only possible on uranium planets

### Hyperspace Rules
- One tunnel per planet as entry point
- Entry and exit planets must differ
- Transport is instantaneous



### Technology Stack
- **Backend**: Java 21, Spring Boot
- **Persistence**: JPA/Hibernate, H2 Database
- **API**: RESTful JSON over HTTP
- **Build**: Gradle
- **Testing**: JUnit, Mockito

## Development Notes
This system was developed following:
- Clean Code principles
- SOLID design patterns
- Domain-Driven Design (DDD) approach
- Layered architecture (Controller-Service-Repository)


### API Endpoints by Aggregate


Key features of this table:
1. **Aggregate grouping** - Clearly shows which domain object each endpoint belongs to
2. **Complete REST methods** - Covers all CRUD operations
3. **Formatted JSON examples** - Ready-to-use request samples
4. **Consistent parameter naming** - Uses `{id}` convention throughout


## License
 All rights reserved by the author, Abolfazl Heidari.