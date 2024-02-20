import java.util.*;

// Class to represent an edge in the graph
class Edge implements Comparable<Edge> {
    int source, destination, weight;

    public Edge(int source, int destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    // Compare edges based on their weights
    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }
}

// Class to represent a disjoint set for union-find operations
class DisjointSet {
    int[] parent, rank;

    public DisjointSet(int n) {
        parent = new int[n];
        rank = new int[n];
        // Initialize each node as a separate set with rank 0
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    // Find the set to which a node belongs (with path compression)
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    // Union two sets (with union by rank)
    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) return;

        // Attach smaller rank tree under root of high rank tree
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            // If ranks are the same, make one as root and increment its rank
            parent[rootY] = rootX;
            rank[rootX]++;
        }
    }
}

public class KruskalMST {
    private List<Edge> edges;
    private int numVertices;

    public KruskalMST(int numVertices) {
        this.numVertices = numVertices;
        edges = new ArrayList<>();
    }

    public void addEdge(int source, int destination, int weight) {
        edges.add(new Edge(source, destination, weight));
    }

    public List<Edge> findMST() {
        // Sort edges based on their weights (using priority queue based on minimum heap)
        PriorityQueue<Edge> minHeap = new PriorityQueue<>(edges);

        DisjointSet ds = new DisjointSet(numVertices);

        List<Edge> mst = new ArrayList<>();

        while (!minHeap.isEmpty() && mst.size() < numVertices - 1) {
            Edge edge = minHeap.poll();

            int sourceRoot = ds.find(edge.source);
            int destRoot = ds.find(edge.destination);

            // If including this edge does not cause a cycle, add it to the MST
            if (sourceRoot != destRoot) {
                mst.add(edge);
                ds.union(sourceRoot, destRoot);
            }
        }

        return mst;
    }

    public static void main(String[] args) {
        KruskalMST graph = new KruskalMST(4);
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 2, 6);
        graph.addEdge(0, 3, 5);
        graph.addEdge(1, 3, 15);
        graph.addEdge(2, 3, 4);

        List<Edge> mst = graph.findMST();

        System.out.println("Edges in MST:");
        for (Edge edge : mst) {
            System.out.println(edge.source + " - " + edge.destination + ": " + edge.weight);
        }
    }
}
