# 🌍 ClimaSim - Globe-Centric Climate Analysis Platform

A sophisticated 3D climate visualization platform that presents climate data through an interactive, realistic Earth globe with year-specific analysis (1980-2050) and micro-donation integration.

## ✨ Features

- **3D Interactive Globe**: Realistic Earth rendering with day/night textures, clouds, and specular highlights
- **Year-Based Analysis**: Climate data visualization for any year from 1980 to 2050
- **Issue Deep Dive**: Major climate issues with sub-issue breakdowns
- **Timeline Simulation**: Watch climate changes evolve over time
- **Solutions Visualization**: See the impact of climate solutions on the globe
- **Micro-Donation Integration**: Support climate action with small, meaningful contributions
- **Real-Time Rendering**: Smooth 60 FPS 3D graphics with orbit controls

## 🛠️ Technology Stack

- **Java 11+**: Core programming language
- **LWJGL 3.3.3**: OpenGL bindings for 3D rendering
- **JOML**: Math library for 3D transformations
- **Dear ImGui**: Modern UI framework
- **Jackson**: JSON data processing
- **Maven**: Dependency management and build system

## 📋 Prerequisites

Before running ClimaSim, make sure you have:

1. **Java 11 or higher** installed

   ```bash
   java -version
   ```

2. **Maven 3.6+** installed

   ```bash
   mvn -version
   ```

3. **Git** (to clone the repository)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/climasim-project.git
cd climasim-project
```

### 2. Build the Project

```bash
mvn clean compile
```

### 3. Run ClimaSim

```bash
mvn exec:java
```

Or alternatively:

```bash
mvn javafx:run
```

### 4. Package for Distribution

```bash
mvn clean package
```

This creates a standalone JAR in `target/climasim-project-1.0.0-shaded.jar`

## 🎮 Controls

- **Left Mouse + Drag**: Orbit around the Earth
- **Mouse Wheel**: Zoom in/out
- **UI Buttons**: Navigate through different views and years

## 📁 Project Structure

```
climasim-project/
├── src/main/java/com/climasim/
│   ├── ClimaSimApp.java              # Main application entry
│   ├── core/                         # Core rendering and application logic
│   │   ├── Application.java          # Main loop and window management
│   │   ├── Renderer.java             # OpenGL rendering
│   │   ├── Camera.java               # 3D camera with orbit controls
│   │   └── input/MouseInput.java     # Mouse interaction handling
│   ├── globe/                        # 3D Earth globe implementation
│   │   ├── Globe.java                # Main Earth object
│   │   ├── GlobeShader.java          # GLSL shaders for realistic rendering
│   │   └── GlobeMaterial.java        # Earth textures and materials
│   ├── data/                         # Climate data management
│   │   ├── DataManager.java          # Loads and manages climate data
│   │   └── models/                   # Data models (YearlyClimateData, etc.)
│   ├── state/                        # Application state management
│   │   ├── StateManager.java         # Controls app flow between views
│   │   └── AppState.java             # Available application states
│   └── ui/                           # User interface components
│       ├── UIManager.java            # Main UI coordinator
│       └── panels/                   # Individual UI panels
├── src/main/resources/
│   ├── shaders/                      # GLSL shader files
│   ├── textures/                     # Earth texture files
│   └── data/                         # Climate data files
└── pom.xml                           # Maven configuration
```

## 🔧 Development Setup

### Adding Real Earth Textures

Replace the placeholder textures in `src/main/resources/textures/` with high-resolution Earth textures:

1. `earth_day.jpg` - Daytime Earth surface (4K+ recommended)
2. `earth_night.jpg` - Nighttime Earth with city lights
3. `earth_clouds.png` - Cloud layer with transparency
4. `earth_specular.jpg` - Ocean specular map (black=land, white=ocean)
5. `earth_normal.jpg` - Surface normal map for terrain detail

### Climate Data

Climate data is currently generated procedurally. To use real data:

1. Update `src/main/resources/data/climate_data_1980_2050.json`
2. Modify `DataManager.loadClimateData()` to parse your JSON format

### Customizing the Globe

- Modify `Globe.java` to adjust sphere quality (SPHERE_SEGMENTS)
- Edit shader files in `resources/shaders/` for visual effects
- Update `GlobeMaterial.java` for different material properties

## 🌍 Application Flow

1. **Welcome Screen**: Introduction to ClimaSim
2. **Main View**: 3D globe with year input (1980-2050)
3. **Issue Selection**: Major climate issues appear around the globe
4. **Deep Dive**: Sub-issues and detailed data for selected problems
5. **Solutions**: Climate solutions with impact visualization
6. **Micro-Donations**: Support climate action with small contributions

## 📊 Sample Data

The application includes procedurally generated climate data covering:

- **Global Warming**: Temperature rise, CO2 emissions, urban heat islands
- **Deforestation**: Forest loss in Amazon, Indonesia, Central Africa
- **Ocean Acidification**: Coral bleaching, marine ecosystem impacts
- **Extreme Weather**: Hurricanes, droughts, floods, heat waves
- **Ice Sheet Melting**: Sea level rise, glacier retreat, Arctic ice loss
- **Biodiversity Loss**: Species extinction, habitat destruction

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙋‍♂️ Support

If you encounter any issues:

1. Check that Java 11+ and Maven are properly installed
2. Ensure your graphics drivers support OpenGL 3.3+
3. Try running with `mvn clean compile exec:java -X` for detailed logs
4. Create an issue on GitHub with system information and error logs

## 🔮 Future Enhancements

- Real-time climate data integration
- VR/AR support for immersive globe interaction
- Machine learning climate predictions
- Blockchain-based micro-donation tracking
- Multi-language support
- Mobile app companion

---

**ClimaSim** - Making climate data accessible, interactive, and actionable through the power of 3D visualization. 🌱
