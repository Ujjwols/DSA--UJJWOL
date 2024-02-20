import java.util.PriorityQueue;

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
        left = right = null;
    }
}

public class ClosestValuesInBST {
    public static int[] closestValues(TreeNode root, double target, int x) {
        // Priority queue to store x closest values
        PriorityQueue<double[]> pq = new PriorityQueue<>((a, b) -> Double.compare(b[1], a[1]));

        // In-order traversal of the binary search tree
        inorder(root, target, x, pq);

        // Retrieve the x closest values from the priority queue
        int[] result = new int[x];
        for (int i = 0; i < x; i++) {
            result[i] = (int) pq.poll()[0];
        }
        return result;
    }

    // In-order traversal function
    private static void inorder(TreeNode root, double target, int x, PriorityQueue<double[]> pq) {
        if (root == null) return;

        inorder(root.left, target, x, pq);

        // Calculate absolute difference between node's value and target
        double diff = Math.abs(root.val - target);

        if (pq.size() < x) {
            pq.offer(new double[]{root.val, diff});
        } else {
            if (diff < pq.peek()[1]) {
                pq.poll();
                pq.offer(new double[]{root.val, diff});
            }
        }

        inorder(root.right, target, x, pq);
    }

    public static void main(String[] args) {
         /*
//          * Provided Tree:
//          *       4
//          *      / \
//          *     2   5
//          *    / \
//          *   1   3
//          */

        // Example usage
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);

        double target = 3.8;
        int x = 2;

        int[] closest = closestValues(root, target, x);
        System.out.println("Closest values to " + target + " are: ");
        for (int value : closest) {
            System.out.print(value + " ");
        }
    }
}