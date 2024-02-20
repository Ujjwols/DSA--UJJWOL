public class MinimumCostDecoration {

    public static int minCostToDecorate(int[][] costs) {
        if (costs == null || costs.length == 0 || costs[0].length == 0) {
            return 0;
        }

        int n = costs.length;
        int k = costs[0].length;

        // Initialize dp table to store the minimum cost to decorate each venue with each theme
        int[][] dp = new int[n][k];

        // Initialize the first row with the costs of decorating the first venue
        for (int j = 0; j < k; j++) {
            dp[0][j] = costs[0][j];
        }

        // Iterate through venues and themes
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < k; j++) {
                // For each theme j, find the minimum cost of decorating venue i with theme j
                dp[i][j] = Integer.MAX_VALUE;
                for (int prevTheme = 0; prevTheme < k; prevTheme++) {
                    if (prevTheme != j) {
                        dp[i][j] = Math.min(dp[i][j], dp[i - 1][prevTheme] + costs[i][j]);
                    }
                }
            }
        }

        // Find the minimum cost of decorating the last venue
        int minCost = Integer.MAX_VALUE;
        for (int j = 0; j < k; j++) {
            minCost = Math.min(minCost, dp[n - 1][j]);
        }
        return minCost;
    }

    public static void main(String[] args) {
        int[][] costMatrix = {{1, 3, 2}, {4, 6, 8}, {3, 1, 5}};
        int result = minCostToDecorate(costMatrix);
        System.out.println(result); // Output: 7
    }
}