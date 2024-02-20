import java.util.*;

public class SecretSharing {

    public static List<Integer> findSecretRecipients(int n, int[][] intervals, int firstPerson) {
        Set<Integer> recipients = new HashSet<>();
        recipients.add(firstPerson); // Initially, person 0 has the secret

        // Iterate through each interval
        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];

            // Share the secret with individuals in the current interval
            for (int i = start; i <= end; i++) {
                recipients.add(i);
            }
        }

        return new ArrayList<>(recipients);
    }

    public static void main(String[] args) {
        int n = 5;
        int[][] intervals = {{0, 2}, {1, 3}, {2, 4}};
        int firstPerson = 0;
        
        List<Integer> recipients = findSecretRecipients(n, intervals, firstPerson);
        System.out.println(recipients); // Output: [0, 1, 2, 3, 4]
    }
}
