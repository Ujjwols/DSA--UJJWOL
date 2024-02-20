import java.util.Random;
import java.util.Arrays;

public class AntColonyOptimization {

    // TSP problem parameters
    private int numCities;
    private double[][] distances;

    // ACO parameters
    private int numAnts;
    private double[][] pheromones;
    private double[][] heuristicValues;
    private double alpha; // Pheromone weight
    private double beta;  // Heuristic weight
    private double evaporationRate;
    private double depositRate;
    private int maxIterations;

    public AntColonyOptimization(int numCities, double[][] distances, int numAnts, double alpha, double beta,
                                 double evaporationRate, double depositRate, int maxIterations) {
        this.numCities = numCities;
        this.distances = distances;
        this.numAnts = numAnts;
        this.alpha = alpha;
        this.beta = beta;
        this.evaporationRate = evaporationRate;
        this.depositRate = depositRate;
        this.maxIterations = maxIterations;

        // Initialize pheromones and heuristic values
        this.pheromones = new double[numCities][numCities];
        this.heuristicValues = new double[numCities][numCities];
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromones[i][j] = 0.1; // Initial pheromone level
                heuristicValues[i][j] = 1.0 / distances[i][j]; // Inverse of distance
            }
        }
    }

    public int[] solveTSP() {
        int[] bestTour = new int[numCities];
        double bestTourLength = Double.MAX_VALUE;

        Random random = new Random();

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            // Initialize ant solutions
            int[][] antSolutions = new int[numAnts][numCities];
            double[] antTourLengths = new double[numAnts];

            // Construct solutions for each ant
            for (int ant = 0; ant < numAnts; ant++) {
                int[] tour = new int[numCities];
                boolean[] visited = new boolean[numCities];
                tour[0] = random.nextInt(numCities); // Start from a random city
                visited[tour[0]] = true;

                // Construct the tour
                for (int step = 1; step < numCities; step++) {
                    int currentCity = tour[step - 1];
                    int nextCity = selectNextCity(currentCity, visited);
                    tour[step] = nextCity;
                    visited[nextCity] = true;
                }

                antSolutions[ant] = tour;
                antTourLengths[ant] = calculateTourLength(tour);
            }

            // Update pheromones
            updatePheromones(antSolutions, antTourLengths);

            // Update best solution found so far
            for (int ant = 0; ant < numAnts; ant++) {
                if (antTourLengths[ant] < bestTourLength) {
                    bestTourLength = antTourLengths[ant];
                    System.arraycopy(antSolutions[ant], 0, bestTour, 0, numCities);
                }
            }

            // Evaporate pheromones
            evaporatePheromones();
        }

        return bestTour;
    }

    private int selectNextCity(int currentCity, boolean[] visited) {
        double totalProbability = 0.0;
        double[] probabilities = new double[numCities];

        // Calculate probabilities for each city
        for (int city = 0; city < numCities; city++) {
            if (!visited[city]) {
                double pheromone = pheromones[currentCity][city];
                double heuristic = heuristicValues[currentCity][city];
                probabilities[city] = Math.pow(pheromone, alpha) * Math.pow(heuristic, beta);
                totalProbability += probabilities[city];
            }
        }

        // Select next city based on probabilities
        double randomValue = Math.random() * totalProbability;
        double cumulativeProbability = 0.0;
        for (int city = 0; city < numCities; city++) {
            if (!visited[city]) {
                cumulativeProbability += probabilities[city];
                if (cumulativeProbability >= randomValue) {
                    return city;
                }
            }
        }

        // Should not reach here
        return -1;
    }

    private double calculateTourLength(int[] tour) {
        double tourLength = 0;
        for (int i = 0; i < numCities - 1; i++) {
            tourLength += distances[tour[i]][tour[i + 1]];
        }
        tourLength += distances[tour[numCities - 1]][tour[0]]; // Return to the starting city
        return tourLength;
    }

    private void updatePheromones(int[][] antSolutions, double[] antTourLengths) {
        // Deposit pheromones based on ant solutions
        for (int ant = 0; ant < numAnts; ant++) {
            double tourLength = antTourLengths[ant];
            for (int i = 0; i < numCities - 1; i++) {
                int city1 = antSolutions[ant][i];
                int city2 = antSolutions[ant][i + 1];
                pheromones[city1][city2] += depositRate / tourLength;
                pheromones[city2][city1] += depositRate / tourLength;
            }
            int lastCity = antSolutions[ant][numCities - 1];
            int firstCity = antSolutions[ant][0];
            pheromones[lastCity][firstCity] += depositRate / tourLength;
            pheromones[firstCity][lastCity] += depositRate / tourLength;
        }
    }

    private void evaporatePheromones() {
        // Evaporate pheromones on all edges
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromones[i][j] *= (1 - evaporationRate);
            }
        }
    }

    public static void main(String[] args) {
        // Example TSP problem
        int numCities = 5;
        double[][] distances = {
            {0, 10, 15, 20, 25},
            {10, 0, 35, 25, 30},
            {15, 35, 0, 30, 10},
            {20, 25, 30, 0, 35},
            {25, 30, 10, 35, 0}
        };

        // ACO parameters
        int numAnts = 10;
        double alpha = 1.0;
        double beta = 2.0;
        double evaporationRate = 0.1;
        double depositRate = 1.0;
        int maxIterations = 100;

        AntColonyOptimization aco = new AntColonyOptimization(numCities, distances, numAnts, alpha, beta,
                                                              evaporationRate, depositRate, maxIterations);

        int[] bestTour = aco.solveTSP();
        System.out.println("Best tour found: " + Arrays.toString(bestTour));
    }
}
