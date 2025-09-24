package finalproject;

import javax.swing.*;
import java.awt.*;
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
    Map<String, String[]> accounts = new HashMap<>(); // username -> [password, roleOrStatus]

    JRadioButton[] candidateButtons;
    JTextArea resultArea = new JTextArea(10, 25);

    JLabel loginMessage = new JLabel("");

    public VotingSystem() {
        loadAccounts();
        loadVotes();

        loginPanel.setLayout(new BorderLayout(10, 10));
        JPanel loginFields = new JPanel();
        loginFields.setLayout(new BoxLayout(loginFields, BoxLayout.Y_AXIS));

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));

        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(200, 30));

        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(200, 30));

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));

        loginFields.add(Box.createVerticalStrut(20));
        loginFields.add(userLabel);
        loginFields.add(usernameField);
        loginFields.add(Box.createVerticalStrut(15));
        loginFields.add(passLabel);
        loginFields.add(passwordField);
        loginFields.add(Box.createVerticalStrut(20));
        loginFields.add(loginBtn);
        loginFields.add(Box.createVerticalStrut(10));
        loginFields.add(loginMessage);

        JPanel loginWrapper = new JPanel(new GridBagLayout());
        loginWrapper.add(loginFields);

        JLabel loginTitle = new JLabel("Voting System Login", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 20));

        loginPanel.add(loginTitle, BorderLayout.NORTH);
        loginPanel.add(loginWrapper, BorderLayout.CENTER);

        votingPanel.setLayout(new BoxLayout(votingPanel, BoxLayout.Y_AXIS));
        JLabel voteTitle = new JLabel("Vote for Mayor", SwingConstants.CENTER);
        voteTitle.setFont(new Font("Arial", Font.BOLD, 18));
        voteTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        votingPanel.add(voteTitle);
        votingPanel.add(Box.createVerticalStrut(10));

        candidateButtons = new JRadioButton[candidates.length];
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < candidates.length; i++) {
            candidateButtons[i] = new JRadioButton(candidates[i]);
            candidateButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            candidateButtons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            group.add(candidateButtons[i]);
            votingPanel.add(candidateButtons[i]);
        }

        JButton voteBtn = new JButton("Submit Vote");
        voteBtn.setFont(new Font("Arial", Font.BOLD, 13));
        voteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        votingPanel.add(Box.createVerticalStrut(15));
        votingPanel.add(voteBtn);

        donePanel.setLayout(new BorderLayout());
        donePanel.add(new JLabel("Voting Done!", SwingConstants.CENTER), BorderLayout.CENTER);
        JButton backBtn = new JButton("Return to Login");
        donePanel.add(backBtn, BorderLayout.SOUTH);

        resultPanel.setLayout(new BorderLayout());
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        JButton backToLoginBtn = new JButton("Return to Login");
        resultPanel.add(backToLoginBtn, BorderLayout.SOUTH);

        mainPanel.add(loginPanel, "Login");
        mainPanel.add(votingPanel, "Voting");
        mainPanel.add(donePanel, "Done");
        mainPanel.add(resultPanel, "Result");

        add(mainPanel);
        setTitle("Voting System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loginBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();
            if (accounts.containsKey(user) && accounts.get(user)[0].equals(pass)) {
                String roleOrStatus = accounts.get(user)[1];
                if (roleOrStatus.equals("admin")) {
                    showResults();
                    cardLayout.show(mainPanel, "Result");
                } else if (roleOrStatus.equals("0")) {
                    cardLayout.show(mainPanel, "Voting");
                } else if (roleOrStatus.equals("1")) {
                    loginMessage.setText("Already voted!");
                }
            } else {
                loginMessage.setText("Invalid credentials!");
            }
        });

        voteBtn.addActionListener(e -> {
            for (int i = 0; i < candidates.length; i++) {
                if (candidateButtons[i].isSelected()) {
                    votes.put(candidates[i], votes.get(candidates[i]) + 1);
                    String user = usernameField.getText().trim();
                    accounts.get(user)[1] = "1"; // mark as voted
                    saveVotes();
                    saveAccounts();
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

    void loadAccounts() {
        accounts.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 3) {
                    accounts.put(parts[0], new String[]{parts[1], parts[2]});
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void saveAccounts() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("accounts.txt"))) {
            for (Map.Entry<String, String[]> entry : accounts.entrySet()) {
                pw.println(entry.getKey() + " " + entry.getValue()[0] + " " + entry.getValue()[1]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void loadVotes() {
        try (BufferedReader br = new BufferedReader(new FileReader("votes.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    votes.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                }
            }
        } catch (IOException ex) {
            for (String c : candidates) {
                votes.put(c, 0);
            }
        }
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

    void showResults() {
        java.util.List<Map.Entry<String, Integer>> list = new ArrayList<>(votes.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());

        StringBuilder sb = new StringBuilder("Results:\n");
        int totalVotes = votes.values().stream().mapToInt(i -> i).sum();

        for (Map.Entry<String, Integer> e : list) {
            double percent = totalVotes == 0 ? 0 : (e.getValue() * 100.0 / totalVotes);
            sb.append(e.getKey()).append(": ").append(e.getValue())
              .append(" votes (").append(String.format("%.2f", percent)).append("%)\n");
        }
        resultArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VotingSystem().setVisible(true));
    }
}
