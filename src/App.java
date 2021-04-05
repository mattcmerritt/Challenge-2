import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import git.tools.client.GitSubprocessClient;

public class App {

	private String repoPath;
	private GitSubprocessClient gitSubprocessClient;

	private JTextField statusField;

	public App() {
		JFrame mainWindow = new JFrame("Git Helper");

		JPanel mainPanel = new JPanel(new BorderLayout());

		// setting up the repo selection panel
		JPanel repoSelectPanel = new JPanel();

		JLabel selectRepoLabel = new JLabel("Filepath for repo:");
		JTextField repoInputBox = new JTextField(50);
		JButton submitRepoButton = new JButton("Open Repo");

		// adding button listener
		submitRepoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repoPath = repoInputBox.getText();
				gitSubprocessClient = new GitSubprocessClient(repoPath);

				updateGitStatus();
			}
		});

		repoSelectPanel.add(selectRepoLabel);
		repoSelectPanel.add(repoInputBox);
		repoSelectPanel.add(submitRepoButton);

		mainPanel.add(repoSelectPanel, BorderLayout.NORTH);
		// end repo selection panel setup

		// setting up center panel
		JPanel centerPanel = new JPanel(new GridLayout(1, 3));

		// setting up status panel of center
		JPanel statusPanel = new JPanel(new GridLayout(2, 1));

		JButton refreshButton = new JButton("Refresh Status");
		statusField = new JTextField(30);
		statusField.setEditable(false);
		updateGitStatus();

		// adding listener to update status text box
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateGitStatus();
			}
		});

		statusPanel.add(refreshButton);
		statusPanel.add(statusField);

		centerPanel.add(statusPanel);
		// end status panel setup

		// placeholder panels for center panel
		JPanel panel1 = new JPanel();
		panel1.setBackground(Color.blue);
		JPanel panel2 = new JPanel();
		panel2.setBackground(Color.red);

		centerPanel.add(panel1);
		centerPanel.add(panel2);

		mainPanel.add(centerPanel, BorderLayout.CENTER);
		// end center panel setup

		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setSize(1000, 1000);
		mainWindow.add(mainPanel);
		mainWindow.setVisible(true);
	}

	public static void main(String[] args) {
		new App();
	}

	public void updateGitStatus() {
		// implement behavior
	}

}
