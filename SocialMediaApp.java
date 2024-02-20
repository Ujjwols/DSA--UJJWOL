import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

class User {
    private String username;
    private Set<User> friends;
    private Set<String> interests;

    public User(String username) {
        this.username = username;
        this.friends = new HashSet<>();
        this.interests = new HashSet<>();
    }

    public String getUsername() {
        return username;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void addFriend(User friend) {
        friends.add(friend);
    }

    public Set<String> getInterests() {
        return interests;
    }

    public void addInterest(String interest) {
        interests.add(interest);
    }
}

class SocialNetwork {
    private Set<User> users;

    public SocialNetwork() {
        this.users = new HashSet<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public void followUser(String followerUsername, String followeeUsername) {
        User follower = getUser(followerUsername);
        User followee = getUser(followeeUsername);
        if (follower != null && followee != null) {
            follower.addFriend(followee);
        }
    }

    public Set<User> recommendFriends(String username) {
        User user = getUser(username);
        if (user == null) {
            return new HashSet<>();
        }

        Set<User> recommendations = new HashSet<>();
        for (User friend : user.getFriends()) {
            for (User friendOfFriend : friend.getFriends()) {
                if (!user.getFriends().contains(friendOfFriend) && !friendOfFriend.equals(user)) {
                    recommendations.add(friendOfFriend);
                }
            }
        }

        return recommendations;
    }
}

public class SocialMediaApp extends JFrame {
    private SocialNetwork socialNetwork;
    private JTextField usernameField;
    private JTextArea recommendationTextArea;

    public SocialMediaApp() {
        socialNetwork = new SocialNetwork();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Social Media Recommendation System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel usernameLabel = new JLabel("Enter Username:");
        usernameField = new JTextField(20);
        JButton recommendButton = new JButton("Get Recommendations");

        recommendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                if (username != null && !username.isEmpty()) {
                    Set<User> recommendations = socialNetwork.recommendFriends(username);
                    recommendationTextArea.setText("Recommendations for " + username + ":\n");
                    for (User user : recommendations) {
                        recommendationTextArea.append(user.getUsername() + "\n");
                    }
                } else {
                    JOptionPane.showMessageDialog(SocialMediaApp.this, "Please enter a valid username.");
                }
            }
        });

        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(recommendButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        recommendationTextArea = new JTextArea(10, 30);
        recommendationTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(recommendationTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SocialMediaApp app = new SocialMediaApp();
                app.setVisible(true);
            }
        });
    }
}
