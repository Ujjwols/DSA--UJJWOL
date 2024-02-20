import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;

public class ImageDownloaderApp extends JFrame {
    private JTextField urlField;
    private JButton downloadButton;
    private JTextArea logArea;
    private ExecutorService executorService;
    private List<Future<?>> downloadTasks;

    public ImageDownloaderApp() {
        setTitle("Image Downloader");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        addListeners();

        executorService = Executors.newFixedThreadPool(5); // Using a fixed-size thread pool
        downloadTasks = new ArrayList<>();

        setVisible(true);
    }

    private void initComponents() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel urlLabel = new JLabel("URL:");
        urlField = new JTextField(20);
        downloadButton = new JButton("Download");

        inputPanel.add(urlLabel);
        inputPanel.add(urlField);
        inputPanel.add(downloadButton);

        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(logArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addListeners() {
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = urlField.getText().trim();
                if (!url.isEmpty()) {
                    downloadImage(url);
                }
            }
        });
    }

    private void downloadImage(String url) {
        Future<?> task = executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    URL imageUrl = new URL(url);
                    String fileName = imageUrl.getFile();
                    int lastSlashIndex = fileName.lastIndexOf('/');
                    if (lastSlashIndex != -1 && lastSlashIndex < fileName.length() - 1) {
                        fileName = fileName.substring(lastSlashIndex + 1);
                    } else {
                        fileName = "image" + System.currentTimeMillis() + ".jpg";
                    }
                    File outputFile = new File(fileName);

                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try (InputStream inputStream = connection.getInputStream();
                             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }
                        logMessage("Downloaded image: " + fileName);
                    } else {
                        logMessage("Failed to download image: " + fileName + ", HTTP response code: " + responseCode);
                    }
                } catch (IOException ex) {
                    logMessage("Error downloading image: " + ex.getMessage());
                }
            }
        });
        downloadTasks.add(task);
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                logArea.append(message + "\n");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ImageDownloaderApp();
            }
        });
    }
}



// Tested Urls
// https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/PNG_transparency_demonstration_1.png/240px-PNG_transparency_demonstration_1.png
// https://via.placeholder.com/150