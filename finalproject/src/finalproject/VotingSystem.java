package finalproject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class VotingSystem extends JFrame {
    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);

    JPanel loginPanel = new JPanel();
    JPanel votingPanel = new JPanel();
    JPanel donePanel = new JPanel();
    JPanel resultPanel = new JPanel();

    JTextField usernameField = new JTextField(15);
    JPasswordField passwordField = new JPasswordField(15);

    String[] candidates = {
        "Jelon Bonto", "Coco Melon", "Diego Peras",
        "Maria Santos", "Juan Dela Cruz", "Liza Manalo",
        "Pedro Pascual", "Josefa Rizal", "Andres Bonifacio"
    };

    Map<String, Integer> votes = new HashMap<>();
    Map<String, String[]> accounts = new HashMap<>();

    JRadioButton[] candidateButtons;
    JTextArea resultArea = new JTextArea(15, 28);

    JLabel loginMessage = new JLabel("");

    Font titleFont = new Font("Arial", Font.BOLD, 26);
    Font labelFont = new Font("Arial", Font.BOLD, 18);
    Font textFont = new Font("Arial", Font.PLAIN, 16);
    Font buttonFont = new Font("Arial", Font.BOLD, 14);

    Color mainBg = new Color(240, 248, 255);
    Color votingBg = new Color(250, 250, 210);
    Color doneBg = new Color(224, 255, 224);
    Color resultBg = new Color(255, 239, 213);

    public VotingSystem() {
        for (String c : candidates) votes.put(c, 0);
        loadAccounts();

        loginPanel.setLayout(new BorderLayout());
        loginPanel.setBackground(mainBg);

        JLabel loginTitle = new JLabel("Voting System Login", SwingConstants.CENTER);
        loginTitle.setFont(titleFont);
        loginPanel.add(loginTitle, BorderLayout.NORTH);

        JPanel loginCenter = new JPanel();
        loginCenter.setLayout(new GridBagLayout());
        loginCenter.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        loginCenter.add(userLabel, gbc);

        gbc.gridy++;
        usernameField.setFont(textFont);
        usernameField.setPreferredSize(new Dimension(200, 30));
        loginCenter.add(usernameField, gbc);

        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        loginCenter.add(passLabel, gbc);

        gbc.gridy++;
        passwordField.setFont(textFont);
        passwordField.setPreferredSize(new Dimension(200, 30));
        loginCenter.add(passwordField, gbc);

        gbc.gridy++;
        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn, new Color(70, 130, 180));
        loginBtn.setPreferredSize(new Dimension(140, 35));
        loginCenter.add(loginBtn, gbc);

        gbc.gridy++;
        loginCenter.add(loginMessage, gbc);

        loginPanel.add(loginCenter, BorderLayout.CENTER);

        votingPanel.setLayout(new BorderLayout(10, 10));
        votingPanel.setBackground(votingBg);
        JLabel voteTitle = new JLabel("Vote for Mayor", SwingConstants.CENTER);
        voteTitle.setFont(titleFont);
        JPanel candidatesPanel = new JPanel(new GridLayout(candidates.length, 1, 4, 4));
        candidatesPanel.setBorder(BorderFactory.createEmptyBorder(20, 200, 20, 200));
        candidatesPanel.setOpaque(false);
        candidateButtons = new JRadioButton[candidates.length];
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < candidates.length; i++) {
            candidateButtons[i] = new JRadioButton(candidates[i]);
            candidateButtons[i].setFont(textFont);
            group.add(candidateButtons[i]);
            candidatesPanel.add(candidateButtons[i]);
        }
        JButton voteBtn = new JButton("Submit Vote");
        styleButton(voteBtn, new Color(34, 139, 34));
        voteBtn.setPreferredSize(new Dimension(150, 35));
        votingPanel.add(voteTitle, BorderLayout.NORTH);
        votingPanel.add(candidatesPanel, BorderLayout.CENTER);
        votingPanel.add(voteBtn, BorderLayout.SOUTH);

        donePanel.setLayout(new BorderLayout(10, 10));
        donePanel.setBackground(doneBg);
        JLabel doneLabel = new JLabel("Thank you for voting!", SwingConstants.CENTER);
        doneLabel.setFont(new Font("Arial", Font.BOLD, 22));
        JButton backBtn = new JButton("Return to Login");
        styleButton(backBtn, new Color(70, 130, 180));
        backBtn.setPreferredSize(new Dimension(160, 35));
        donePanel.add(doneLabel, BorderLayout.CENTER);
        donePanel.add(backBtn, BorderLayout.SOUTH);

        resultPanel.setLayout(new BorderLayout(10, 10));
        resultPanel.setBackground(resultBg);
        JLabel resultTitle = new JLabel("Election Results", SwingConstants.CENTER);
        resultTitle.setFont(titleFont);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton backToLoginBtn = new JButton("Return to Login");
        styleButton(backToLoginBtn, new Color(70, 130, 180));
        backToLoginBtn.setPreferredSize(new Dimension(160, 35));
        resultPanel.add(resultTitle, BorderLayout.NORTH);
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        resultPanel.add(backToLoginBtn, BorderLayout.SOUTH);

        mainPanel.add(loginPanel, "Login");
        mainPanel.add(votingPanel, "Voting");
        mainPanel.add(donePanel, "Done");
        mainPanel.add(resultPanel, "Result");

        add(mainPanel);
        setTitle("Voting System");
        setSize(850, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loginBtn.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            if (accounts.containsKey(user) && accounts.get(user)[0].equals(pass)) {
                if (accounts.get(user)[1].equals("admin")) {
                    showResults();
                    cardLayout.show(mainPanel, "Result");
                } else {
                    if (accounts.get(user)[2].equals("1")) {
                        loginMessage.setText("You have already voted!");
                    } else {
                        cardLayout.show(mainPanel, "Voting");
                    }
                }
            } else {
                loginMessage.setText("Invalid credentials!");
            }
        });

        voteBtn.addActionListener(e -> {
            for (int i = 0; i < candidates.length; i++) {
                if (candidateButtons[i].isSelected()) {
                    votes.put(candidates[i], votes.get(candidates[i]) + 1);
                    accounts.get(usernameField.getText())[2] = "1"; 
                    saveAccounts();
                    saveVotes();
                    cardLayout.show(mainPanel, "Done");
                    break;
                }
            }
        });

        backBtn.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            loginMessage.setText("");
            cardLayout.show(mainPanel, "Login");
        });

        backToLoginBtn.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            loginMessage.setText("");
            cardLayout.show(mainPanel, "Login");
        });
    }

    void styleButton(JButton btn, Color bg) {
        btn.setFont(buttonFont);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    void loadAccounts() {
        try (BufferedReader br = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 4) {
                    accounts.put(parts[0], new String[]{parts[1], parts[2], parts[3]});
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void saveAccounts() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("accounts.txt"))) {
            for (Map.Entry<String, String[]> entry : accounts.entrySet()) {
                String[] data = entry.getValue();
                pw.println(entry.getKey() + " " + data[0] + " " + data[1] + " " + data[2]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void showResults() {
        Map<String, Integer> fileVotes = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("votes.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String candidate = parts[0].trim();
                    int count = Integer.parseInt(parts[1].trim());
                    fileVotes.put(candidate, count);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        java.util.List<Map.Entry<String, Integer>> list = new java.util.ArrayList<>(fileVotes.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        StringBuilder sb = new StringBuilder("Results:\n\n");
        int totalVotes = fileVotes.values().stream().mapToInt(i -> i).sum();
        for (Map.Entry<String, Integer> e : list) {
            double percent = totalVotes == 0 ? 0 : (e.getValue() * 100.0 / totalVotes);
            sb.append(String.format("%-20s : %3d votes (%.2f%%)\n", e.getKey(), e.getValue(), percent));
        }
        resultArea.setText(sb.toString());
    }

    void saveVotes() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("votes.txt"))) {
            for (String c : candidates) {
                pw.println(c + ": " + votes.get(c));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VotingSystem().setVisible(true));
    }
}
