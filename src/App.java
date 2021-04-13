import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import git.tools.client.GitSubprocessClient;


public class App {

	private String repoPath;
	private GitSubprocessClient gitSubprocessClient;

	private JTextArea statusText;
	private JLabel loadFailLabel;
	private JScrollPane statusPane;

	public App() {
		JFrame mainWindow = new JFrame("Git Helper");

		JPanel mainPanel = new JPanel(new BorderLayout());

		// setting up the repo selection panel
		JPanel repoSelectPanel = new JPanel();

		JLabel selectRepoLabel = new JLabel("Filepath for repo:");
		JTextField repoInputBox = new JTextField(50);
		JButton submitRepoButton = new JButton("Open Repo");
		loadFailLabel = new JLabel("Failed to open repo");

		loadFailLabel.setForeground(Color.red);

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

		repoSelectPanel.add(selectRepoLabel);
		repoSelectPanel.add(repoInputBox);
		repoSelectPanel.add(submitRepoButton);
		repoSelectPanel.add(loadFailLabel);

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
		statusPane = new JScrollPane(statusText);
		statusTextPanel.add(statusPane);

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
		

		
		
		// setting up individual file actions panel to center
		JPanel allFilePanel = new JPanel();
		allFilePanel.setLayout(new BoxLayout(allFilePanel, BoxLayout.Y_AXIS));

		JLabel allFileLabel = new JLabel("All File Actions:");
		//JLabel selectFileLabel = new JLabel("Select File:");
		
		JButton addAllFileButton = new JButton("Add all files");
		JButton restoreAllFileButton = new JButton("Restore all files");
		JButton unstageAllFileButton = new JButton("Unstage all files");
		
		allFileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		//selectFileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		addAllFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		restoreAllFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		unstageAllFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		addAllFileButton.setMaximumSize(new Dimension(200, 25));
		restoreAllFileButton.setMaximumSize(new Dimension(200, 25));
		unstageAllFileButton.setMaximumSize(new Dimension(200, 25));
		
		
		addAllFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
					
					addAllFile();
					updateGitStatus();
				}
			
		});

		restoreAllFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
					
					restoreAllFile();
					updateGitStatus();
				}
			
		});

		unstageAllFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
					unstageAllFile();
					updateGitStatus();
				}
			
		});
		
		allFilePanel.add(allFileLabel);
		allFilePanel.add(Box.createRigidArea(new Dimension(0, 12))); // spacing
		//allFilePanel.add(selectFileLabel);
		//individualFilePanel.add(fileDropdown);
		allFilePanel.add(Box.createRigidArea(new Dimension(0, 12))); // spacing
		allFilePanel.add(addAllFileButton);
		allFilePanel.add(Box.createRigidArea(new Dimension(0, 12))); // spacing
		allFilePanel.add(restoreAllFileButton);
		allFilePanel.add(Box.createRigidArea(new Dimension(0, 12))); // spacing
		allFilePanel.add(unstageAllFileButton);
		centerPanel.add(allFilePanel);

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
	
	public void addAllFile() {
		if (gitSubprocessClient == null) {
			showLoadFail();
		}
		else {
			System.out.println(gitSubprocessClient.gitAddAll());
		}
	}
	
	public void restoreAllFile() {
		if (gitSubprocessClient == null) {
			showLoadFail();
		}
		else {
			System.out.println(gitSubprocessClient.runGitCommand("restore ." ));
		}
	}
	
	public void unstageAllFile() {
		if (gitSubprocessClient == null) {
			showLoadFail();
		}
		else {
			System.out.println(gitSubprocessClient.runGitCommand("reset"));
		}
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
