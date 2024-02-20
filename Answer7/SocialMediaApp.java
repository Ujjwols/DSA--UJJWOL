import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialMediaApp extends JFrame {
    private Map<String, List<String>> socialNetwork;
    private Map<String, UserProfile> userProfiles;
    private String currentUser;

    private List<JPanel> posts;
    private JPanel postPanel;
    private JButton addPostButton;
    private JButton connectUsersButton;
    private JLabel currentUserLabel;
    private Font postFont = new Font("Arial", Font.PLAIN, 14);
    private Color postBackgroundColor = new Color(240, 240, 240);
    private Color buttonBackgroundColor = new Color(150, 200, 255);

    public SocialMediaApp() {
        setTitle("Social Media App");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        socialNetwork = new HashMap<>();
        userProfiles = new HashMap<>();

        // Initialize posts panel
        posts = new ArrayList<>();
        postPanel = new JPanel();
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
        postPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(postPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Label to display current user
        currentUserLabel = new JLabel("Current User: ");
        add(currentUserLabel, BorderLayout.NORTH);

        // Add Post button (initially disabled)
        addPostButton = new JButton("Add Post");
        addPostButton.setBackground(buttonBackgroundColor);
        addPostButton.setFocusPainted(false);
        addPostButton.setEnabled(false); // Disable initially
        addPostButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String postContent = JOptionPane.showInputDialog(SocialMediaApp.this, "Enter your post:");
                if (postContent != null && !postContent.isEmpty()) {
                    addNewPost(postContent);
                }
            }
        });

        // Connect Users button
        connectUsersButton = new JButton("Connect Users");
        connectUsersButton.setBackground(buttonBackgroundColor);
        connectUsersButton.setFocusPainted(false);
        connectUsersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectUsers();
            }
        });

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addPostButton);
        buttonPanel.add(connectUsersButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);

        createUser(); // Call create user function to start the process
    }

    private void createUser() {
        String userName = JOptionPane.showInputDialog(SocialMediaApp.this, "Enter your username:");
        if (userName != null && !userName.isEmpty()) {
            currentUser = userName;
            currentUserLabel.setText("Current User: " + currentUser);
            socialNetwork.put(currentUser, new ArrayList<>());
            userProfiles.put(currentUser, new UserProfile(currentUser));
            addPostButton.setEnabled(true); // Enable "Add Post" button after creating user
            recommendContent(currentUser); // Initially recommend content
        } else {
            // If user cancels or enters an empty username, close the application
            dispose();
        }
    }

    private void addNewPost(String postContent) {
        JPanel post = new JPanel();
        post.setLayout(new BorderLayout());
        post.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        post.setBackground(postBackgroundColor);

        JLabel postLabel = new JLabel("<html><body style='width: 300px;'>" + postContent + "</body></html>");
        postLabel.setFont(postFont);

        JButton likeButton = new JButton("Like");
        likeButton.setBackground(buttonBackgroundColor);
        likeButton.setFocusPainted(false);
        JButton dislikeButton = new JButton("Dislike");
        dislikeButton.setBackground(buttonBackgroundColor);
        dislikeButton.setFocusPainted(false);
        JButton commentButton = new JButton("Comment");
        commentButton.setBackground(buttonBackgroundColor);
        commentButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(postBackgroundColor);
        buttonPanel.add(likeButton);
        buttonPanel.add(dislikeButton);
        buttonPanel.add(commentButton);

        post.add(postLabel, BorderLayout.NORTH);
        post.add(buttonPanel, BorderLayout.SOUTH);

        posts.add(post);
        postPanel.add(post);
        postPanel.revalidate();
        postPanel.repaint();

        likeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(SocialMediaApp.this, currentUser + " liked the post!");
                trackUserInteractions(currentUser, "like", postContent);
                recommendContent(currentUser);
            }
        });

        dislikeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(SocialMediaApp.this, currentUser + " disliked the post!");
                trackUserInteractions(currentUser, "dislike", postContent);
                recommendContent(currentUser);
            }
        });

        commentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String comment = JOptionPane.showInputDialog(SocialMediaApp.this, "Enter your comment:");
                if (comment != null && !comment.isEmpty()) {
                    JOptionPane.showMessageDialog(SocialMediaApp.this, currentUser + " commented: " + comment);
                    trackUserInteractions(currentUser, "comment", postContent);
                    recommendContent(currentUser);
                }
            }
        });

        // Share the post with connected users
        for (String connectedUser : socialNetwork.getOrDefault(currentUser, new ArrayList<>())) {
            JOptionPane.showMessageDialog(this, "Shared post with " + connectedUser);
            addSharedPost(connectedUser, postContent);
        }
    }

    private void connectUsers() {
        List<String> users = new ArrayList<>(userProfiles.keySet());
        users.remove(currentUser); // Remove current user from the list
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No other users to connect with.");
            return;
        }
        String selectedUser = (String) JOptionPane.showInputDialog(
                this,
                "Select a user to connect with:",
                "Connect Users",
                JOptionPane.QUESTION_MESSAGE,
                null,
                users.toArray(),
                users.get(0)
        );
        if (selectedUser != null) {
            socialNetwork.get(currentUser).add(selectedUser);
            JOptionPane.showMessageDialog(this, "Connected with user: " + selectedUser);
        }
    }

    private void addSharedPost(String userName, String postContent) {
        JPanel post = new JPanel();
        post.setLayout(new BorderLayout());
        post.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        post.setBackground(postBackgroundColor);

        JLabel postLabel = new JLabel("<html><body style='width: 300px;'>" + postContent + "</body></html>");
        postLabel.setFont(postFont);

        post.add(postLabel, BorderLayout.NORTH);

        postPanel.add(post);
        postPanel.revalidate();
        postPanel.repaint();
    }

    private void trackUserInteractions(String userName, String interactionType, String targetContent) {
        String targetUserName = "Post Creator"; // Assuming target is the post creator
        UserProfile userProfile = userProfiles.getOrDefault(userName, new UserProfile(userName));
        userProfile.addInteraction(targetUserName, interactionType);
        userProfiles.put(userName, userProfile);

        if (interactionType.equals("like")) {
            UserProfile targetProfile = userProfiles.getOrDefault(targetUserName, new UserProfile(targetUserName));
            targetProfile.addInterest("Liked posts");
            userProfiles.put(targetUserName, targetProfile);
        }
    }

    private void recommendContent(String userName) {
        List<String> recommendations = new ArrayList<>();
        for (String followedUser : socialNetwork.getOrDefault(userName, new ArrayList<>())) {
            UserProfile userProfile = userProfiles.get(followedUser);
            if (userProfile != null) {
                recommendations.addAll(userProfile.getInterests());
            }
        }
        displayRecommendedContentUI(recommendations);
    }

    private void displayRecommendedContentUI(List<String> recommendations) {
        // Create a dialog box to display recommended content
        StringBuilder contentMessage = new StringBuilder("<html><body>");
        contentMessage.append("<h3>Recommended Content</h3>");
        if (recommendations.isEmpty()) {
            contentMessage.append("<p>No recommendations available.</p>");
        } else {
            for (String recommendation : recommendations) {
                contentMessage.append("<p>").append(recommendation).append("</p>");
            }
        }
        contentMessage.append("</body></html>");

        JLabel contentLabel = new JLabel(contentMessage.toString());
        JScrollPane scrollPane = new JScrollPane(contentLabel);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Recommended Content", JOptionPane.INFORMATION_MESSAGE);
    }

    private class UserProfile {
        private String userName;
        private List<String> interests;
        private Map<String, List<String>> interactions;

        public UserProfile(String userName) {
            this.userName = userName;
            interests = new ArrayList<>();
            interactions = new HashMap<>();
        }

        public void addInterest(String interest) {
            interests.add(interest);
        }

        public void addInteraction(String target, String interaction) {
            interactions.computeIfAbsent(target, k -> new ArrayList<>()).add(interaction);
        }

        public List<String> getInterests() {
            List<String> allInterests = new ArrayList<>(interests);
            for (List<String> userInteractions : interactions.values()) {
                allInterests.addAll(userInteractions);
            }
            return allInterests;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SocialMediaApp();
            }
        });
    }
}
