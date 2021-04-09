import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import git.tools.client.GitSubprocessClient;

public class App {

	private String repoPath;
	private String commitMessage;
	private String branchName = "master";
	private GitSubprocessClient gitSubprocessClient;
	private ArrayList<JComponent> items;
	private boolean dark;
	private JTextArea statusText;
	private JLabel loadFailLabel;
	private JScrollPane statusPane;

	public App() {
		items = new ArrayList<JComponent>();
		dark = true; // will be toggled back to false at end of startup
		JFrame mainWindow = new JFrame("Git Helper");
		JPanel mainPanel = new JPanel(new BorderLayout());
		//mainPanel.setBackground(_panelBackgroundColor);

		// setting up the repo selection panel
		JPanel repoSelectPanel = new JPanel();
		JLabel selectRepoLabel = new JLabel("Filepath for repo:");
		JTextField repoInputBox = new JTextField(50);
		repoInputBox.setBorder(BorderFactory.createLineBorder(Color.black));
		JButton submitRepoButton = new JButton("Open Repo");
		loadFailLabel = new JLabel("Failed to open repo");

		loadFailLabel.setForeground(Color.red);
		JButton _selectThemeButton = new JButton("Dark Mode");
		items.add(submitRepoButton);
		items.add(repoSelectPanel);
		items.add(selectRepoLabel);

		// adding button listener
		submitRepoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repoPath = repoInputBox.getText();
				try {
					gitSubprocessClient = new GitSubprocessClient(repoPath);
					gitSubprocessClient.gitStatus(); // unused command to test if the loaded directory can be used
					hideLoadFail();
				} catch (RuntimeException exception) {
					showLoadFail();
				}

				updateGitStatus();
			}
		});
		//Action Listener for the change theme button
		_selectThemeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dark = !dark;
				if (dark) {
					for (JComponent item : items) {
						if (item instanceof JPanel) {
							item.setBackground(Color.black);
							_selectThemeButton.setText("Light Mode");
						} else {
							_selectThemeButton.setText("Light Mode");
							item.setBackground(Color.black);
							item.setForeground(Color.white);
						}
					}
				} else {
					for (JComponent item : items) {
						if (item instanceof JPanel) {
							item.setBackground(Color.white);
							_selectThemeButton.setText("Dark Mode");
						} else {
							_selectThemeButton.setText("Dark Mode");
							item.setBackground(Color.white);
							item.setForeground(Color.black);
						}
					}
				}

			}
		});

		repoSelectPanel.add(selectRepoLabel);
		repoSelectPanel.add(repoInputBox);
		repoSelectPanel.add(submitRepoButton);
		repoSelectPanel.add(loadFailLabel);
		repoSelectPanel.add(_selectThemeButton);
		items.add(_selectThemeButton);

		hideLoadFail();

		mainPanel.add(repoSelectPanel, BorderLayout.NORTH);
		// end repo selection panel setup

		// setting up center panel
		JPanel centerPanel = new JPanel(new GridLayout(1, 3));


		// setting up status panel of center
		JPanel statusPanel = new JPanel(new BorderLayout());


		JPanel refreshPanel = new JPanel();
		JButton refreshButton = new JButton("Refresh Status");
		refreshPanel.add(refreshButton);

		JPanel statusTextPanel = new JPanel();
		statusText = new JTextArea(20, 25);
		statusText.setEditable(false);
		statusText.setMargin(new Insets(10, 10, 10, 10));
		statusText.setBorder(BorderFactory.createLineBorder(Color.black));
		statusPane = new JScrollPane(statusText);
		JLabel commitInputLabel = new JLabel("Commit Message:");
		JTextArea commitInputBox = new JTextArea(15, 27);
		commitInputBox.setBorder(BorderFactory.createLineBorder(Color.black));
		JTextArea commitOutputBox = new JTextArea(15, 27);
		commitOutputBox.setBorder(BorderFactory.createLineBorder(Color.black));
		commitOutputBox.setEditable(false);
		commitInputBox.setEditable(true);
		statusTextPanel.add(statusPane);
		statusTextPanel.add(commitInputLabel);
		statusTextPanel.add(commitInputBox);
		statusTextPanel.add(commitOutputBox);
		items.add(refreshButton);
		items.add(refreshPanel);
		items.add(centerPanel);
		items.add(statusPanel);
		items.add(statusTextPanel);
		items.add(commitInputLabel);
		// adding listener to update status text box
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateGitStatus();
			}
		});

		statusPanel.add(refreshPanel, BorderLayout.NORTH);
		statusPanel.add(statusTextPanel, BorderLayout.CENTER);

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

		//South Panel
		JPanel _buttonPanel = new JPanel();
		_buttonPanel.setPreferredSize(new Dimension(200, 30));
		_buttonPanel.setBackground(java.awt.Color.white);
		// Creating buttons
		JButton _commitButton = new JButton("Commit");
		JButton _pushButton = new JButton("Push");
		JButton _pullButton = new JButton("Pull");
		// Adds the buttons to the panel
		_buttonPanel.add(_commitButton);
		_buttonPanel.add(_pushButton);
		_buttonPanel.add(_pullButton);
		items.add(_commitButton);
		items.add(_pushButton);
		items.add(_pullButton);
		items.add(_buttonPanel);
		//Action Listener for the commit button
		_commitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gitSubprocessClient == null) {
					showLoadFail();
				}
				else {
					commitMessage = commitInputBox.getText();
					gitSubprocessClient.gitCommit(commitMessage);
					commitOutputBox.setText("Successfully committed: " + commitMessage);
					updateGitStatus();
				}

			}
		});
		//Action Listener for the push button
		_pushButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gitSubprocessClient == null) {
					showLoadFail();
				}
				else {
					gitSubprocessClient.gitPush(branchName);
					commitOutputBox.setText("Successfully pushed to " + branchName);
					updateGitStatus();
				}

			}
		});
		//Action Listener for the pull button
		/*	_pullButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (gitSubprocessClient == null) {
					    showLoadFail();
					}
					else {
						gitSubprocessClient.gitPull(branchName);
						statusText.setText("Successfully pulled to " + branchName);
						updateGitStatus();
					}

				}
			}); */
		// Adds the JPanels to the JFrame
		mainPanel.add(_buttonPanel, BorderLayout.SOUTH);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setSize(1000, 1000);
		mainWindow.add(mainPanel);
		mainWindow.setVisible(true);
	}

	public static void main(String[] args) {
		new App();
	}

	public void updateGitStatus() {
		if (gitSubprocessClient == null) {
			showLoadFail();
		}
		else {
			statusText.setText(gitSubprocessClient.gitStatus());

			if (statusText.getText().indexOf("fatal") == 0) {
				showLoadFail();
			}
		}

		statusText.setText(statusText.getText() + "\n"); // moves the status pane to show the left of the panel, not right
	}

	public void showLoadFail() {
		loadFailLabel.setText("Failed to open repo");
		gitSubprocessClient = null; // clear out a bad directory so that it cannot be used
		statusText.setText("fatal: not a git repository (or any of the parent directories): .git");
		loadFailLabel.setVisible(true);
	}

	public void hideLoadFail() {
		loadFailLabel.setText("");
	}

}
