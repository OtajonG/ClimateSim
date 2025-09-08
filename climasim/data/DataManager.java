package com.climasim.data;

import com.climasim.data.models.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataManager {

        // FIX: Added Singleton instance variable
        private static DataManager instance;

        private static final String DATA_PATH = "/data/";
        private static final String CLIMATE_DATA_FILE = "climate_data_1980_2050.json";
        private static final String ISSUES_DATA_FILE = "climate_issues.json";
        private static final String SOLUTIONS_DATA_FILE = "climate_solutions.json";

        private ObjectMapper objectMapper;
        private Map<Integer, YearlyClimateData> yearlyDataCache;
        private Map<String, ClimateIssue> climateIssuesCache;
        private Map<String, Solution> solutionsCache;
        private Map<IssueType, List<ClimateIssue>> issuesByType;

        private boolean isDataLoaded = false;
        private boolean isLoading = false;
        private String lastError = null;

        private Random dataGenerator;
        private static final long SEED = 12345L;

        // FIX: Constructor is now private for Singleton pattern
        private DataManager() {
                this.objectMapper = new ObjectMapper();
                this.yearlyDataCache = new ConcurrentHashMap<>();
                this.climateIssuesCache = new ConcurrentHashMap<>();
                this.solutionsCache = new ConcurrentHashMap<>();
                this.issuesByType = new ConcurrentHashMap<>();
                this.dataGenerator = new Random(SEED);
                System.out.println("DataManager instantiated");
        }

        // FIX: Added standard getInstance method for Singleton
        public static DataManager getInstance() {
                if (instance == null) {
                        instance = new DataManager();
                }
                return instance;
        }

        // FIX: This is the method Application.java will call
        public void initialize() {
                System.out.println("DataManager initialization started...");
                // This calls your async method and waits for it to complete.
                // This keeps your async logic while fitting into the app's synchronous startup.
                initializeData().join();
                System.out.println("DataManager initialization finished.");
        }

        private CompletableFuture<Boolean> initializeData() {
                return CompletableFuture.supplyAsync(() -> {
                        try {
                                isLoading = true;
                                lastError = null;
                                loadYearlyClimateData();
                                loadClimateIssues();
                                loadClimateSolutions();
                                buildDataIndices();
                                isDataLoaded = true;
                                isLoading = false;
                                System.out.println("Climate data loaded successfully");
                                System.out.println("- Yearly data points: " + yearlyDataCache.size());
                                System.out.println("- Climate issues: " + climateIssuesCache.size());
                                System.out.println("- Solutions: " + solutionsCache.size());
                                return true;

                        } catch (Exception e) {
                                lastError = e.getMessage();
                                isLoading = false;
                                System.err.println("Failed to load climate data: " + e.getMessage());
                                e.printStackTrace();
                                return false;
                        }
                });
        }

        private void loadYearlyClimateData() {
                try {
                        InputStream is = getClass().getResourceAsStream(DATA_PATH + CLIMATE_DATA_FILE);
                        if (is != null) {
                                TypeReference<Map<Integer, YearlyClimateData>> typeRef = new TypeReference<>() {
                                };
                                yearlyDataCache = objectMapper.readValue(is, typeRef);
                                is.close();
                        } else {
                                generateYearlyClimateData();
                                saveYearlyClimateData();
                        }
                } catch (Exception e) {
                        System.err.println(
                                        "Failed to load yearly climate data, generating new data: " + e.getMessage());
                        generateYearlyClimateData();
                }
        }

        private void generateYearlyClimateData() {
                System.out.println("Generating procedural climate data...");
                for (int year = 1980; year <= 2050; year++) {
                        YearlyClimateData data = new YearlyClimateData();
                        data.setYear(year);
                        float yearsFromBase = year - 1980;
                        float futureProjection = year > 2024 ? (year - 2024) * 0.5f : 0;
                        float baseTemp = 14.0f;
                        float tempIncrease = yearsFromBase * 0.02f + futureProjection * 0.03f;
                        data.setGlobalTemperature(baseTemp + tempIncrease);
                        data.setTemperatureAnomaly(tempIncrease);
                        float baseCO2 = 315.0f;
                        data.setCo2Level(baseCO2 + yearsFromBase * 2.2f + futureProjection * 1.8f);
                        data.setSeaLevelChange(yearsFromBase * 0.32f + futureProjection * 0.45f);
                        float iceBaseline = 100.0f;
                        data.setArcticIceExtent(
                                        Math.max(30.0f, iceBaseline - yearsFromBase * 0.8f - futureProjection * 1.2f));
                        float phBaseline = 8.1f;
                        data.setOceanPH(phBaseline - yearsFromBase * 0.002f - futureProjection * 0.003f);
                        float forestBaseline = 100.0f;
                        data.setGlobalForestCover(Math.max(70.0f,
                                        forestBaseline - yearsFromBase * 0.3f - futureProjection * 0.5f));
                        int baseEvents = 50;
                        data.setExtremeWeatherEvents(
                                        (int) (baseEvents + yearsFromBase * 1.5f + futureProjection * 2.0f));

                        // Generate regional data
                        generateRegionalData(data, year);

                        // NEW: Generate summary for each year based on the data
                        data.setSummary(generateYearSummary(data, year));

                        yearlyDataCache.put(year, data);
                }
        }

        // NEW: Method to generate comprehensive summary for each year
        private String generateYearSummary(YearlyClimateData data, int year) {
                StringBuilder summary = new StringBuilder();

                if (year <= 2024) {
                        // Historical data summary
                        summary.append("Historical data for ").append(year).append(": ");

                        if (data.getTemperatureAnomaly() > 1.0f) {
                                summary.append("Significant warming observed with temperature anomaly of +")
                                                .append(String.format("%.2f", data.getTemperatureAnomaly()))
                                                .append("°C. ");
                        } else if (data.getTemperatureAnomaly() > 0.5f) {
                                summary.append("Moderate warming trend with temperature increase of +")
                                                .append(String.format("%.2f", data.getTemperatureAnomaly()))
                                                .append("°C. ");
                        } else {
                                summary.append("Relatively stable temperatures with anomaly of +")
                                                .append(String.format("%.2f", data.getTemperatureAnomaly()))
                                                .append("°C. ");
                        }

                        if (data.getCo2Level() > 400.0f) {
                                summary.append("CO₂ levels reached ").append(String.format("%.1f", data.getCo2Level()))
                                                .append(" ppm, exceeding critical thresholds. ");
                        } else {
                                summary.append("CO₂ levels at ").append(String.format("%.1f", data.getCo2Level()))
                                                .append(" ppm. ");
                        }

                        if (data.getExtremeWeatherEvents() > 80) {
                                summary.append("High number of extreme weather events (")
                                                .append(data.getExtremeWeatherEvents()).append(") recorded. ");
                        } else if (data.getExtremeWeatherEvents() > 60) {
                                summary.append("Moderate increase in extreme weather events (")
                                                .append(data.getExtremeWeatherEvents()).append(") observed. ");
                        }

                } else {
                        // Future projections summary
                        summary.append("Projected data for ").append(year).append(": ");

                        if (data.getTemperatureAnomaly() > 2.0f) {
                                summary.append("Severe warming projected with temperature anomaly of +")
                                                .append(String.format("%.2f", data.getTemperatureAnomaly()))
                                                .append("°C. ");
                        } else if (data.getTemperatureAnomaly() > 1.5f) {
                                summary.append("Significant warming projected with temperature increase of +")
                                                .append(String.format("%.2f", data.getTemperatureAnomaly()))
                                                .append("°C. ");
                        } else {
                                summary.append("Continued warming trend projected with anomaly of +")
                                                .append(String.format("%.2f", data.getTemperatureAnomaly()))
                                                .append("°C. ");
                        }

                        if (data.getSeaLevelChange() > 200.0f) {
                                summary.append("Critical sea level rise of ")
                                                .append(String.format("%.1f", data.getSeaLevelChange()))
                                                .append("mm threatens coastal regions. ");
                        } else if (data.getSeaLevelChange() > 100.0f) {
                                summary.append("Substantial sea level rise of ")
                                                .append(String.format("%.1f", data.getSeaLevelChange()))
                                                .append("mm projected. ");
                        }

                        if (data.getArcticIceExtent() < 2.0f) {
                                summary.append("Arctic ice extent critically low at ")
                                                .append(String.format("%.1f", data.getArcticIceExtent()))
                                                .append(" million km². ");
                        } else if (data.getArcticIceExtent() < 4.0f) {
                                summary.append("Significant Arctic ice loss with extent at ")
                                                .append(String.format("%.1f", data.getArcticIceExtent()))
                                                .append(" million km². ");
                        }
                }

                // Add forest cover warning if critically low
                if (data.getGlobalForestCover() < 80.0f) {
                        summary.append("Forest cover reduced to ")
                                        .append(String.format("%.1f", data.getGlobalForestCover()))
                                        .append("%, impacting carbon sequestration. ");
                }

                // Add ocean pH warning if critically low
                if (data.getOceanPH() < 7.9f) {
                        summary.append("Ocean pH critically acidic at ")
                                        .append(String.format("%.2f", data.getOceanPH()))
                                        .append(", threatening marine ecosystems.");
                } else if (data.getOceanPH() < 8.0f) {
                        summary.append("Ocean acidification concerns with pH at ")
                                        .append(String.format("%.2f", data.getOceanPH())).append(".");
                }

                return summary.toString().trim();
        }

        private void generateRegionalData(YearlyClimateData data, int year) {
                Map<String, Float> regionalTemps = new HashMap<>();
                Map<String, Float> regionalPrecipitation = new HashMap<>();
                float globalTemp = data.getGlobalTemperature();
                float yearProgress = (year - 1980) / 70.0f;
                String[] regions = { "Arctic", "North America", "Europe", "Asia", "Africa", "South America",
                                "Australia", "Antarctica", "Pacific Islands" };
                float[] warmingMultipliers = { 2.5f, 1.0f, 1.1f, 1.2f, 1.3f, 1.0f, 1.4f, 1.8f, 0.8f };
                float[] precipitationChanges = { 0.1f, -0.05f, 0.02f, -0.1f, -0.15f, -0.08f, -0.2f, 0.05f, 0.03f };
                for (int i = 0; i < regions.length; i++) {
                        float regionTemp = globalTemp * warmingMultipliers[i] + dataGenerator.nextFloat() * 0.5f
                                        - 0.25f;
                        regionalTemps.put(regions[i], regionTemp);
                        float basePrecip = 1000.0f;
                        float precipChange = basePrecip * precipitationChanges[i] * yearProgress;
                        regionalPrecipitation.put(regions[i], basePrecip + precipChange);
                }
                data.setRegionalTemperatures(regionalTemps);
                data.setRegionalPrecipitation(regionalPrecipitation);
        }

        private void loadClimateIssues() {
                try {
                        InputStream is = getClass().getResourceAsStream(DATA_PATH + ISSUES_DATA_FILE);
                        if (is != null) {
                                List<ClimateIssue> issues = objectMapper.readValue(is, new TypeReference<>() {
                                });
                                for (ClimateIssue issue : issues) {
                                        climateIssuesCache.put(issue.getId(), issue);
                                }
                                is.close();
                        } else {
                                generateClimateIssues();
                        }
                } catch (Exception e) {
                        System.err.println("Failed to load climate issues, generating new data: " + e.getMessage());
                        generateClimateIssues();
                }
        }

        private void generateClimateIssues() {
                System.out.println("Generating climate issues data...");
                createGlobalWarmingIssues();
                createDeforestationIssues();
                createOceanIssues();
                createExtremeWeatherIssues();
                createIceMeltingIssues();
                createBiodiversityIssues();
        }

        private void createGlobalWarmingIssues() {
                ClimateIssue globalWarming = new ClimateIssue();
                globalWarming.setId("global_warming");
                globalWarming.setTitle("Global Temperature Rise");
                globalWarming.setDescription("Global average temperatures are rising due to greenhouse gas emissions");
                globalWarming.setDetails(
                                "Comprehensive analysis of global temperature trends and their cascading effects on climate systems worldwide.");

                // Create sub-issues
                List<SubIssue> subIssues = Arrays.asList(
                                new SubIssue("Urban Heat Islands",
                                                "Cities experiencing disproportionate warming due to concrete and reduced vegetation"),
                                new SubIssue("Agricultural Disruption",
                                                "Crop yields declining due to temperature stress and changing growing seasons"),
                                new SubIssue("Heat-Related Health Issues",
                                                "Increasing heat-related illness and mortality, especially in vulnerable populations"));
                globalWarming.setSubIssues(subIssues);
                climateIssuesCache.put(globalWarming.getId(), globalWarming);
        }

        private void createDeforestationIssues() {
                ClimateIssue deforestation = new ClimateIssue();
                deforestation.setId("deforestation");
                deforestation.setTitle("Amazon Deforestation");
                deforestation.setDescription("Rapid loss of forest cover reducing carbon sequestration capacity");
                deforestation.setDetails(
                                "Critical analysis of deforestation patterns and their impact on global carbon cycles.");

                List<SubIssue> subIssues = Arrays.asList(
                                new SubIssue("Carbon Release", "Massive CO2 emissions from cleared forests"),
                                new SubIssue("Biodiversity Loss", "Species extinction due to habitat destruction"),
                                new SubIssue("Water Cycle Disruption",
                                                "Altered precipitation patterns affecting regional climate"));
                deforestation.setSubIssues(subIssues);
                climateIssuesCache.put(deforestation.getId(), deforestation);
        }

        private void createOceanIssues() {
                ClimateIssue oceanAcidification = new ClimateIssue();
                oceanAcidification.setId("ocean_acidification");
                oceanAcidification.setTitle("Ocean Acidification");
                oceanAcidification.setDescription("Ocean pH levels dropping due to increased CO2 absorption");
                oceanAcidification.setDetails(
                                "Detailed examination of ocean chemistry changes and marine ecosystem impacts.");

                List<SubIssue> subIssues = Arrays.asList(
                                new SubIssue("Marine Food Chain Collapse",
                                                "Primary producers affected by acidification"),
                                new SubIssue("Coral Reef Bleaching",
                                                "Coral systems dying due to temperature and pH changes"),
                                new SubIssue("Fisheries Decline", "Commercial fish populations declining"));
                oceanAcidification.setSubIssues(subIssues);
                climateIssuesCache.put(oceanAcidification.getId(), oceanAcidification);
        }

        private void createExtremeWeatherIssues() {
                ClimateIssue extremeWeather = new ClimateIssue();
                extremeWeather.setId("extreme_weather");
                extremeWeather.setTitle("Extreme Weather Events");
                extremeWeather.setDescription("Increasing frequency and intensity of storms, droughts, and floods");
                extremeWeather.setDetails("Analysis of changing weather patterns and their societal impacts.");

                List<SubIssue> subIssues = Arrays.asList(
                                new SubIssue("Hurricane Intensification", "Stronger storms causing greater damage"),
                                new SubIssue("Drought Expansion",
                                                "Water scarcity affecting agriculture and communities"),
                                new SubIssue("Flash Flooding",
                                                "Increased precipitation causing infrastructure damage"));
                extremeWeather.setSubIssues(subIssues);
                climateIssuesCache.put(extremeWeather.getId(), extremeWeather);
        }

        private void createIceMeltingIssues() {
                ClimateIssue iceMelting = new ClimateIssue();
                iceMelting.setId("arctic_ice_melting");
                iceMelting.setTitle("Arctic Ice Melting");
                iceMelting.setDescription("Polar ice caps and glaciers melting at accelerating rates");
                iceMelting.setDetails("Comprehensive study of polar ice loss and global implications.");

                List<SubIssue> subIssues = Arrays.asList(
                                new SubIssue("Sea Level Rise", "Global coastlines threatened by rising waters"),
                                new SubIssue("Albedo Effect Loss", "Reduced ice coverage accelerating warming"),
                                new SubIssue("Polar Habitat Loss", "Arctic wildlife losing essential habitat"));
                iceMelting.setSubIssues(subIssues);
                climateIssuesCache.put(iceMelting.getId(), iceMelting);
        }

        private void createBiodiversityIssues() {
                ClimateIssue biodiversity = new ClimateIssue();
                biodiversity.setId("biodiversity_loss");
                biodiversity.setTitle("Biodiversity Loss");
                biodiversity.setDescription("Species extinction rates accelerating due to climate change");
                biodiversity.setDetails("Assessment of species loss and ecosystem degradation patterns.");

                List<SubIssue> subIssues = Arrays.asList(
                                new SubIssue("Habitat Migration", "Species unable to adapt to changing conditions"),
                                new SubIssue("Pollination Disruption",
                                                "Critical plant-pollinator relationships breaking down"),
                                new SubIssue("Food Web Collapse", "Ecosystem balance being disrupted"));
                biodiversity.setSubIssues(subIssues);
                climateIssuesCache.put(biodiversity.getId(), biodiversity);
        }

        private void loadClimateSolutions() {
                try {
                        InputStream is = getClass().getResourceAsStream(DATA_PATH + SOLUTIONS_DATA_FILE);
                        if (is != null) {
                                List<Solution> solutions = objectMapper.readValue(is, new TypeReference<>() {
                                });
                                for (Solution solution : solutions) {
                                        solutionsCache.put(solution.getName(), solution);
                                }
                                is.close();
                        } else {
                                generateClimateSolutions();
                        }
                } catch (Exception e) {
                        System.err.println("Failed to load climate solutions, generating new data: " + e.getMessage());
                        generateClimateSolutions();
                }
        }

        private void generateClimateSolutions() {
                System.out.println("Generating climate solutions data...");

                // Create comprehensive solutions for each type
                solutionsCache.put("Solar Energy Expansion", new Solution("Solar Energy Expansion",
                                "Massive deployment of solar photovoltaic systems globally",
                                SolutionType.RENEWABLE_ENERGY));

                solutionsCache.put("Amazon Reforestation", new Solution("Amazon Reforestation",
                                "Large-scale tree planting and forest protection programs",
                                SolutionType.REFORESTATION));

                solutionsCache.put("Carbon Capture Technology", new Solution("Carbon Capture Technology",
                                "Direct air capture and storage of atmospheric CO2", SolutionType.CARBON_CAPTURE));

                solutionsCache.put("Climate Policy Reform", new Solution("Climate Policy Reform",
                                "Government regulations and international climate agreements",
                                SolutionType.POLICY_CHANGE));

                solutionsCache.put("Individual Climate Action", new Solution("Individual Climate Action",
                                "Personal lifestyle changes to reduce carbon footprint",
                                SolutionType.INDIVIDUAL_ACTION));

                solutionsCache.put("Green Technology Innovation", new Solution("Green Technology Innovation",
                                "Development of clean technologies and sustainable practices",
                                SolutionType.TECHNOLOGY));

                solutionsCache.put("Wildlife Habitat Protection", new Solution("Wildlife Habitat Protection",
                                "Conservation programs to protect endangered ecosystems", SolutionType.CONSERVATION));

                solutionsCache.put("Climate Education Programs", new Solution("Climate Education Programs",
                                "Public awareness campaigns about climate change impacts", SolutionType.EDUCATION));
        }

        private void buildDataIndices() {
                // Build indices for faster access
                for (ClimateIssue issue : climateIssuesCache.values()) {
                        IssueType type = IssueType.GLOBAL_WARMING; // Default type
                        issuesByType.computeIfAbsent(type, k -> new ArrayList<>()).add(issue);
                }
        }

        private void saveYearlyClimateData() {
                try {
                        File outputDir = new File("src/main/resources" + DATA_PATH);
                        if (outputDir.mkdirs()) {
                                System.out.println("Created data directory");
                        }
                        File outputFile = new File(outputDir, CLIMATE_DATA_FILE);
                        objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, yearlyDataCache);
                        System.out.println("Saved yearly climate data to: " + outputFile.getAbsolutePath());
                } catch (Exception e) {
                        System.err.println("Failed to save yearly climate data: " + e.getMessage());
                }
        }

        // Public API methods
        public YearlyClimateData getClimateDataForYear(int year) {
                return yearlyDataCache.get(year);
        }

        public Set<Integer> getAvailableYears() {
                return yearlyDataCache.keySet();
        }

        public List<ClimateIssue> getClimateIssuesForYear(int year) {
                // Return all issues for any year - in a real app this would be filtered
                return new ArrayList<>(climateIssuesCache.values());
        }

        public List<ClimateIssue> getIssuesByType(IssueType type) {
                return issuesByType.getOrDefault(type, new ArrayList<>());
        }

        public Collection<ClimateIssue> getAllClimateIssues() {
                return climateIssuesCache.values();
        }

        public ClimateIssue getClimateIssue(String id) {
                return climateIssuesCache.get(id);
        }

        public Collection<Solution> getAllSolutions() {
                return solutionsCache.values();
        }

        public List<Solution> getSolutionsByType(SolutionType type) {
                return solutionsCache.values().stream()
                                .filter(s -> s.getType() == type)
                                .sorted((a, b) -> Double.compare(b.getEffectiveness(), a.getEffectiveness()))
                                .collect(Collectors.toList());
        }

        public Solution getSolution(String name) {
                return solutionsCache.get(name);
        }

        public List<Solution> getSolutionsForIssue(String issueTitle) {
                // Simple mapping based on issue title keywords
                List<Solution> relevantSolutions = new ArrayList<>();
                String lowerTitle = issueTitle.toLowerCase();

                if (lowerTitle.contains("temperature") || lowerTitle.contains("warming")) {
                        relevantSolutions.addAll(getSolutionsByType(SolutionType.RENEWABLE_ENERGY));
                        relevantSolutions.addAll(getSolutionsByType(SolutionType.CARBON_CAPTURE));
                } else if (lowerTitle.contains("forest") || lowerTitle.contains("deforestation")) {
                        relevantSolutions.addAll(getSolutionsByType(SolutionType.REFORESTATION));
                        relevantSolutions.addAll(getSolutionsByType(SolutionType.CONSERVATION));
                } else if (lowerTitle.contains("ocean")) {
                        relevantSolutions.addAll(getSolutionsByType(SolutionType.CONSERVATION));
                        relevantSolutions.addAll(getSolutionsByType(SolutionType.POLICY_CHANGE));
                } else {
                        // Default - return a mix of solutions
                        relevantSolutions.add(getSolution("Solar Energy Expansion"));
                        relevantSolutions.add(getSolution("Climate Policy Reform"));
                        relevantSolutions.add(getSolution("Individual Climate Action"));
                }

                return relevantSolutions.stream().filter(s -> s != null).collect(Collectors.toList());
        }

        /**
         * Gets a list of solutions that are marked as requiring donations.
         * 
         * @return A list of Solution objects.
         */
        public List<Solution> getDonationProjects() {
                // Return solutions that require donations
                return getAllSolutions().stream()
                                .filter(s -> s.getType() == SolutionType.REFORESTATION ||
                                                s.getType() == SolutionType.CONSERVATION ||
                                                s.getType() == SolutionType.RENEWABLE_ENERGY ||
                                                s.getType() == SolutionType.CARBON_CAPTURE)
                                .collect(Collectors.toList());
        }

        public List<ClimateIssue> searchIssues(String query) {
                String lowerQuery = query.toLowerCase();
                return climateIssuesCache.values().stream()
                                .filter(issue -> issue.getTitle().toLowerCase().contains(lowerQuery) ||
                                                issue.getDescription().toLowerCase().contains(lowerQuery))
                                .collect(Collectors.toList());
        }

        // Status methods
        public boolean isDataLoaded() {
                return isDataLoaded;
        }

        public boolean isLoading() {
                return isLoading;
        }

        public String getLastError() {
                return lastError;
        }

        public CompletableFuture<Boolean> refreshData() {
                yearlyDataCache.clear();
                climateIssuesCache.clear();
                solutionsCache.clear();
                issuesByType.clear();
                isDataLoaded = false;
                return initializeData();
        }
}