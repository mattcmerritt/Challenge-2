import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import git.tools.client.GitSubprocessClient;

public class App {

	private String repoPath;
	private GitSubprocessClient gitSubprocessClient;

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
			}
		});

		repoSelectPanel.add(selectRepoLabel);
		repoSelectPanel.add(repoInputBox);
		repoSelectPanel.add(submitRepoButton);

		mainPanel.add(repoSelectPanel, BorderLayout.NORTH);
		// end repo selection panel setup

		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setSize(1000, 1000);
		mainWindow.add(mainPanel);
		mainWindow.setVisible(true);
	}

	public static void main(String[] args) {
		new App();
	}

}
