import java.util.*;

class SocialGraph {
    Map<Integer, List<Integer>> adjacencyList;

    public SocialGraph() {
        adjacencyList = new HashMap<>();
    }

    public void addEdge(int userId1, int userId2) {
        adjacencyList.computeIfAbsent(userId1, k -> new ArrayList<>()).add(userId2);
        adjacencyList.computeIfAbsent(userId2, k -> new ArrayList<>()).add(userId1);
    }

    public List<Integer> getFriends(int userId) {
        return adjacencyList.getOrDefault(userId, new ArrayList<>());
    }
}

class User {
    private int id;
    private Set<Integer> likes;
    private Set<Integer> comments;
    private Set<Integer> shares;

    public User(int id) {
        this.id = id;
        this.likes = new HashSet<>();
        this.comments = new HashSet<>();
        this.shares = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void likePost(int postId) {
        likes.add(postId);
    }

    public void commentOnPost(int postId) {
        comments.add(postId);
    }

    public void sharePost(int postId) {
        shares.add(postId);
    }

    public Set<Integer> getLikes() {
        return likes;
    }

    public Set<Integer> getComments() {
        return comments;
    }

    public Set<Integer> getShares() {
        return shares;
    }
}

class Post {
    private int id;
    private Set<Integer> likes;
    private Set<Integer> comments;
    private Set<Integer> shares;

    public Post(int id) {
        this.id = id;
        this.likes = new HashSet<>();
        this.comments = new HashSet<>();
        this.shares = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void addComment(int userId) {
        comments.add(userId);
    }

    public void addShare(int userId) {
        shares.add(userId);
    }

    public Set<Integer> getLikes() {
        return likes;
    }

    public Set<Integer> getComments() {
        return comments;
    }

    public Set<Integer> getShares() {
        return shares;
    }
}

class SocialMediaRecommendationSystem {
    SocialGraph socialGraph;
    Map<Integer, User> users;
    Map<Integer, Post> posts;

    public SocialMediaRecommendationSystem() {
        socialGraph = new SocialGraph();
        users = new HashMap<>();
        posts = new HashMap<>();
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public void addPost(Post post) {
        posts.put(post.getId(), post);
    }

    public void addFriendship(int userId1, int userId2) {
        socialGraph.addEdge(userId1, userId2);
    }

    public List<Integer> recommendPosts(int userId) {
        // Implement personalized PageRank with social regularization
        Map<Integer, Double> scores = new HashMap<>();
        List<Integer> friends = socialGraph.getFriends(userId);
        for (int friend : friends) {
            User friendUser = users.get(friend);
            Set<Integer> friendInteractions = new HashSet<>();
            friendInteractions.addAll(friendUser.getLikes());
            friendInteractions.addAll(friendUser.getComments());
            friendInteractions.addAll(friendUser.getShares());

            for (int interaction : friendInteractions) {
                scores.put(interaction, scores.getOrDefault(interaction, 0.0) + 1.0);
            }
        }

        // Personalized PageRank with social regularization
        Map<Integer, Double> newScores = new HashMap<>();
        for (int postId : scores.keySet()) {
            double score = scores.get(postId);
            Post post = posts.get(postId);
            Set<Integer> postInteractions = new HashSet<>();
            postInteractions.addAll(post.getLikes());
            postInteractions.addAll(post.getComments());
            postInteractions.addAll(post.getShares());

            for (int interaction : postInteractions) {
                double friendScore = scores.getOrDefault(interaction, 0.0);
                newScores.put(postId, newScores.getOrDefault(postId, 0.0) + friendScore);
            }
        }

        // Sort posts by score
        List<Map.Entry<Integer, Double>> sortedEntries = new ArrayList<>(newScores.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Get recommended posts
        List<Integer> recommendedPosts = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : sortedEntries) {
            recommendedPosts.add(entry.getKey());
        }
        return recommendedPosts;
    }
}

public class SocialMediaApp {
    public static void main(String[] args) {
        SocialMediaRecommendationSystem recommendationSystem = new SocialMediaRecommendationSystem();

        // Create users
        User user1 = new User(1);
        User user2 = new User(2);
        User user3 = new User(3);

        // Add users to the recommendation system
        recommendationSystem.addUser(user1);
        recommendationSystem.addUser(user2);
        recommendationSystem.addUser(user3);

        // Create posts
        Post post1 = new Post(101);
        Post post2 = new Post(102);
        Post post3 = new Post(103);
        Post post4 = new Post(104);

        // Add posts to the recommendation system
        recommendationSystem.addPost(post1);
        recommendationSystem.addPost(post2);
        recommendationSystem.addPost(post3);
        recommendationSystem.addPost(post4);

        // Simulate user interactions (likes, comments, shares)
        user1.likePost(post2.getId());
        user1.commentOnPost(post3.getId());
        user2.likePost(post1.getId());
        user2.sharePost(post4.getId());
        user3.likePost(post1.getId());
        user3.commentOnPost(post2.getId());

        // Establish friendships
        recommendationSystem.addFriendship(user1.getId(), user2.getId());
        recommendationSystem.addFriendship(user2.getId(), user3.getId());

        // Recommend posts for user 1
        List<Integer> recommendedPosts = recommendationSystem.recommendPosts(user1.getId());
        System.out.println("Recommended posts for user 1: " + recommendedPosts);
    }
}
