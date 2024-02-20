import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ImageDownloaderApp extends JFrame {

    private ExecutorService executorService;
    private static final String DOWNLOAD_DIRECTORY = "./downloaded_file/";
    private List<Future<?>> downloadTasks;
    private Map<Future<?>, DownloadInfo> downloadInfoMap;

    private JPanel panel;
    private JTextField inputField;
    private JProgressBar progressBar;
    private JButton downloadButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private JButton cancelButton;

    public ImageDownloaderApp() {
        initComponents();
        executorService = Executors.newFixedThreadPool(5);
        downloadTasks = new CopyOnWriteArrayList<>();
        downloadInfoMap = new ConcurrentHashMap<>();
    }

    private void initComponents() {
        setTitle("Image Downloader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 200);
        setLocationRelativeTo(null); // Center the window
    
        panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
    
        inputField = new JTextField();
        inputField.setFont(inputField.getFont().deriveFont(Font.PLAIN, 16));
    
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
    
        downloadButton = new JButton("Download");
        downloadButton.setFont(downloadButton.getFont().deriveFont(Font.PLAIN, 16));
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                download_btnActionPerformed(e);
            }
        });
    
        pauseButton = new JButton("Pause");
        pauseButton.setFont(pauseButton.getFont().deriveFont(Font.PLAIN, 16));
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause_btnActionPerformed(e);
            }
        });
    
        resumeButton = new JButton("Resume");
        resumeButton.setFont(resumeButton.getFont().deriveFont(Font.PLAIN ,16));
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resume_btnActionPerformed(e);
            }
        });
    
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(cancelButton.getFont().deriveFont(Font.PLAIN, 16));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancel_btnActionPerformed(e);
            }
        });
    
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(inputField)
                .addComponent(progressBar)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(downloadButton)
                        .addComponent(pauseButton)
                        .addComponent(resumeButton)
                        .addComponent(cancelButton)));
    
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(inputField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(downloadButton)
                        .addComponent(pauseButton)
                        .addComponent(resumeButton)
                        .addComponent(cancelButton)));
    
        layout.linkSize(SwingConstants.HORIZONTAL, downloadButton, pauseButton, resumeButton, cancelButton);
    
        getContentPane().add(panel);
    }
    

    private void download_btnActionPerformed(ActionEvent evt) {
        // This method is called when the download button is clicked.
        // It reads URLs from the input field and starts downloading the images.
        String urlsText = inputField.getText();
        String[] urls = urlsText.split("[,\\s]+");
        for (String url : urls) {
            if (!url.isEmpty()) {
                downloadImage(url);
            }
        }
    }

    private void pause_btnActionPerformed(ActionEvent evt) {
        // This method is called when the pause button is clicked.
        // It pauses the ongoing downloads.
        pauseDownloads();
    }

    private void resume_btnActionPerformed(ActionEvent evt) {
        // This method is called when the resume button is clicked.
        // It resumes the paused downloads.
        resumeDownloads();
    }

    private void cancel_btnActionPerformed(ActionEvent evt) {
        // This method is called when the cancel button is clicked.
        // It cancels all the ongoing downloads.
        cancelDownloads();
    }

    private void downloadImage(String urlString) {
        // This method downloads the image from the given URL.
        Runnable downloadTask = new Runnable() {
            @Override
            public void run() {
                DownloadInfo downloadInfo = downloadInfoMap.get(Thread.currentThread());
                int progress = downloadInfo != null ? downloadInfo.getProgress() : 0;
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                    if (progress > 0) {
                        connection.setRequestProperty("Range", "bytes=" + progress + "-");
                    }

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
                        int contentLength = connection.getContentLength();
                        InputStream inputStream = connection.getInputStream();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            progress += bytesRead;
                            int currentProgress = (int) ((progress / (double) contentLength) * 100);
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setValue(currentProgress);
                                }
                            });

                            if (Thread.currentThread().isInterrupted()) {
                                throw new InterruptedException("Download interrupted");
                            }

                            Thread.sleep(50);
                        }

                        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
                        saveImage(outputStream.toByteArray(), fileName);

                        inputStream.close();
                        outputStream.close();
                    } else {
                        throw new IOException("Failed to download image. Response code: " + responseCode);
                    }
                } catch (IOException | InterruptedException e) {
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    if (!(e instanceof InterruptedException)) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Future<?> task = executorService.submit(downloadTask);
        downloadTasks.add(task);
        downloadInfoMap.put(task, new DownloadInfo(urlString, 0));
    }

    private void saveImage(byte[] imageData, String fileName) {
        // This method saves the downloaded image data to a file.
        File directory = new File("./downloaded_file/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fullPath = "./downloaded_file/" + fileName;

        try {
            FileOutputStream outputStream = new FileOutputStream(fullPath);
            outputStream.write(imageData);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void resumeDownloads() {
        // This method resumes the paused downloads.
        for (Future<?> task : downloadTasks) {
            if (task.isCancelled()) {
                DownloadInfo downloadInfo = downloadInfoMap.get(task);
                if (downloadInfo != null) {
                    downloadImage(downloadInfo.getUrl());
                }
            }
        }
    }

    private void pauseDownloads() {
        // This method pauses the ongoing downloads.
        for (Future<?> task : downloadTasks) {
            if (!task.isDone() && !task.isCancelled()) {
                task.cancel(true);
            }
        }
    }

    private void cancelDownloads() {
        // This method cancels all the ongoing downloads.
        for (Future<?> task : downloadTasks) {
            task.cancel(true);
        }
        progressBar.setValue(0);
    }

    private class DownloadInfo {
        // This class holds information about a download task.
        private String url;
        private int progress;

        public DownloadInfo(String url, int progress) {
            this.url = url;
            this.progress = progress;
        }

        public String getUrl() {
            return url;
        }

        public int getProgress() {
            return progress;
        }
    }

    public static void main(String args[]) {
        // Set the look and feel to the system's look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ImageDownloaderApp().setVisible(true);
            }
        });
    }
}
