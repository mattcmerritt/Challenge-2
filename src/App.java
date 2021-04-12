import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

import git.tools.client.GitSubprocessClient;

public class App {

	private String repoPath;
	private String commitMessage;
	private String branchName;
	private GitSubprocessClient gitSubprocessClient;
	private ArrayList<JComponent> items;
	private boolean dark;
	private JTextArea statusText, outputText, commitInputBox;
	private JLabel loadFailLabel;
	private JScrollPane statusPane, outputPane;
	private JComboBox<String> fileDropdown, branchDropdown;

	private String[] changedFiles, branches;

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
					changeColor();
					if (dark) {
						_selectThemeButton.setText("Light Mode");
					} else {
						_selectThemeButton.setText("Dark Mode");
					}
				
			}
		});

		repoSelectPanel.add(selectRepoLabel);
		repoSelectPanel.add(repoInputBox);
		repoSelectPanel.add(submitRepoButton);
		repoSelectPanel.add(loadFailLabel);
		repoSelectPanel.add(_selectThemeButton);
		items.add(_selectThemeButton);
		items.add(repoInputBox);
		

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
		commitInputBox = new JTextArea(1, 25);
		commitInputBox.setMargin(new Insets(10, 10, 10, 10));
		commitInputBox.setBorder(BorderFactory.createLineBorder(Color.black));
		JLabel logLabel = new JLabel("Output Log:");

		outputText = new JTextArea(15, 25);
		outputText.setBorder(BorderFactory.createLineBorder(Color.black));
		outputText.setMargin(new Insets(10, 10, 10, 10));
		outputText.setEditable(false);

		outputPane = new JScrollPane(outputText);

		commitInputBox.setEditable(true);
		statusTextPanel.add(statusPane);
		statusTextPanel.add(commitInputLabel);
		statusTextPanel.add(commitInputBox);
		statusTextPanel.add(logLabel);
		statusTextPanel.add(outputPane);

		items.add(refreshButton);
		items.add(refreshPanel);
		items.add(centerPanel);
		items.add(statusPanel);
		items.add(statusTextPanel);
		items.add(commitInputLabel);
		items.add(logLabel);
		items.add(outputText);
		items.add(commitInputBox);
		items.add(statusText);
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

		centerPanel.add(panel1);

		// setting up individual file actions panel to center
		JPanel individualFilePanel = new JPanel();
		individualFilePanel.setLayout(new BoxLayout(individualFilePanel, BoxLayout.Y_AXIS));

		JLabel individualFileLabel = new JLabel("Individual File Actions:");
		JLabel selectFileLabel = new JLabel("Select File:");

		changedFiles = new String[0];

		fileDropdown = new JComboBox<String>(changedFiles);
		JButton addFileButton = new JButton("Add single file");
		JButton restoreFileButton = new JButton("Restore single file");
		JButton unstageFileButton = new JButton("Unstage single file");

		individualFileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		selectFileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		fileDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
		addFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		restoreFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		unstageFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		fileDropdown.setMaximumSize(new Dimension(300, 25));
		addFileButton.setMaximumSize(new Dimension(150, 25));
		restoreFileButton.setMaximumSize(new Dimension(150, 25));
		unstageFileButton.setMaximumSize(new Dimension(150, 25));

		addFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selectedOption = fileDropdown.getSelectedItem();
				if (selectedOption != null) {
					String selectedFile = selectedOption.toString();
					addIndividualFile(selectedFile);
					updateGitStatus();
				}
			}
		});

		restoreFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selectedOption = fileDropdown.getSelectedItem();
				if (selectedOption != null) {
					String selectedFile = selectedOption.toString();
					restoreIndividualFile(selectedFile);
					updateGitStatus();
				}
			}
		});

		unstageFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selectedOption = fileDropdown.getSelectedItem();
				if (selectedOption != null) {
					String selectedFile = selectedOption.toString();
					unstageIndividualFile(selectedFile);
					updateGitStatus();
				}
			}
		});

		items.add(individualFileLabel);
		items.add(selectFileLabel);
		items.add(fileDropdown);
		items.add(addFileButton);
		items.add(restoreFileButton);
		items.add(unstageFileButton);
		items.add(individualFilePanel);

		individualFilePanel.add(individualFileLabel);
		individualFilePanel.add(Box.createRigidArea(new Dimension(0, 12))); // spacing
		individualFilePanel.add(selectFileLabel);
		individualFilePanel.add(fileDropdown);
		individualFilePanel.add(Box.createRigidArea(new Dimension(0, 12))); // spacing
		individualFilePanel.add(addFileButton);
		individualFilePanel.add(Box.createRigidArea(new Dimension(0, 12))); // spacing
		individualFilePanel.add(restoreFileButton);
		individualFilePanel.add(Box.createRigidArea(new Dimension(0, 12))); // spacing
		individualFilePanel.add(unstageFileButton);

		centerPanel.add(individualFilePanel);
		// end individual file actions panel

		mainPanel.add(centerPanel, BorderLayout.CENTER);
		// end center panel setup

		//South Panel
		JPanel _buttonPanel = new JPanel();
		//_buttonPanel.setPreferredSize(new Dimension(200, 30)); // panel autosizes to fit screen
		_buttonPanel.setBackground(java.awt.Color.white);
		// Creating buttons
		JButton _commitButton = new JButton("Commit");
		JButton _pushButton = new JButton("Push");
		JButton _pullButton = new JButton("Pull");
		// Creating branch selection for pull
		branches = new String[0];
		branchDropdown = new JComboBox<String>(branches);
		branchDropdown.setPreferredSize(new Dimension(100, 25));
		// Adds the buttons to the panel
		_buttonPanel.add(_commitButton);
		_buttonPanel.add(_pushButton);
		_buttonPanel.add(_pullButton);
		items.add(_commitButton);
		items.add(_pushButton);
		items.add(_pullButton);
		items.add(_buttonPanel);
		// Add dropdown to panel and list of components
		_buttonPanel.add(branchDropdown);
		items.add(branchDropdown);
		//Action Listener for the commit button
		_commitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gitSubprocessClient == null) {
					showLoadFail();
				}
				else {
					commitMessage = commitInputBox.getText();
					outputText.setText(gitSubprocessClient.gitCommit(commitMessage));
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
					outputText.setText(gitSubprocessClient.gitPush(branchName));
					updateGitStatus();
				}

			}
		});
		//Action Listener for the pull button
		_pullButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gitSubprocessClient == null) {
					showLoadFail();
				}
				else {
					Object selectedOption = branchDropdown.getSelectedItem();
					if (selectedOption != null) {
						String selectedBranch = (String) selectedOption;
						outputText.setText(gitSubprocessClient.gitPull(selectedBranch));
						updateGitStatus();
					}
				}

			}
		});
		// Adds the JPanels to the JFrame
		mainPanel.add(_buttonPanel, BorderLayout.SOUTH);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setSize(1000, 850);
		mainWindow.add(mainPanel);
		mainWindow.setVisible(true);
		changeColor();
		
	}

	public static void main(String[] args) {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, fall back to cross-platform
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception ex) {
				// Not worth my time
			}
		}
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

			// these should only run if the second check passed
			if (gitSubprocessClient != null) {
				// file dropdown setup code
				// saving the previously selected item, if an item was selected
				String prevSelected = fileDropdown.getSelectedItem() == null ? null : fileDropdown.getSelectedItem().toString();

				// fetching list of changed files
				Object[] fileObjects = listFilesInStatus();
				changedFiles = new String[fileObjects.length];

				// casting files to be useful
				for (int i = 0; i < fileObjects.length; i++) {
					changedFiles[i] = (String) fileObjects[i];
				}

				// clears out dropdown and adds in new list of files
				fileDropdown.removeAllItems();
				for (String filename : changedFiles) {
					fileDropdown.addItem(filename);
				}

				// re-selecting the previously selected item if possible
				if (prevSelected != null) {
					fileDropdown.setSelectedItem(prevSelected);
				}
				// end file dropdown

				// pull button setup code
				branchName = gitSubprocessClient.runGitCommand("branch --show-current");

				String unparsedBranches = gitSubprocessClient.runGitCommand("ls-remote --heads");
				// remove all carriage returns to make string consistent on all operating systems
				unparsedBranches = unparsedBranches.replaceAll("\\r", "");

				// separating all branches from string, need a structure that can grow
				ArrayList<String> branchesList = new ArrayList<String>();
				while (unparsedBranches.indexOf("\n") != 0) {
					branchesList.add(unparsedBranches.substring(unparsedBranches.indexOf("refs/heads/") + "refs/heads/".length(), unparsedBranches.indexOf("\n")));
					unparsedBranches = unparsedBranches.substring(unparsedBranches.indexOf("\n") + 1);
				}

				// converting arraylist to array
				branches = new String[branchesList.size()];
				for (int i = 0; i < branches.length; i++) {
					branches[i] = branchesList.get(i);
				}

				// clears out dropdown and adds in new list of branches
				branchDropdown.removeAllItems();
				for (String branch : branches) {
					branchDropdown.addItem(branch);
				}

				branchDropdown.setSelectedItem(branchName);
				// end branch dropdown
			}
		}
		statusText.setText(statusText.getText() + "\n"); // moves the status pane to show the left of the panel, not right
	}

	public void addIndividualFile(String filename) {
		if (gitSubprocessClient == null) {
			showLoadFail();
		}
		else {
			outputText.setText(gitSubprocessClient.gitAddFile(filename));
		}
	}

	public void restoreIndividualFile(String filename) {
		if (gitSubprocessClient == null) {
			showLoadFail();
		}
		else {
			outputText.setText(gitSubprocessClient.runGitCommand("restore " + filename));
		}
	}

	public void unstageIndividualFile(String filename) {
		if (gitSubprocessClient == null) {
			showLoadFail();
		}
		else {
			outputText.setText(gitSubprocessClient.runGitCommand("restore --staged " + filename));
		}
	}

	public void showLoadFail() {
		loadFailLabel.setText("Failed to open repo");
		gitSubprocessClient = null; // clear out a bad directory so that it cannot be used
		statusText.setText("fatal: not a git repository (or any of the parent directories): .git\n");
		outputText.setText("fatal: not a git repository (or any of the parent directories): .git");
		commitInputBox.setText(""); // clear message
		commitMessage = ""; // clear message
		// clearing out dropdowns
		branches = new String[0];
		changedFiles = new String[0];
		branchDropdown.removeAllItems();
		fileDropdown.removeAllItems();
		loadFailLabel.setVisible(true);
	}

	public void hideLoadFail() {
		loadFailLabel.setText("");
	}
	
	public void changeColor() {
		dark = !dark;
		if (dark) {
			for (JComponent item : items) {
				if (item instanceof JPanel) {
					item.setBackground(Color.black);
				} 
				else if (item instanceof JTextComponent || item instanceof JComboBox) {
                    item.setBackground(Color.darkGray);
                    item.setForeground(Color.white);
                }
				else {
					item.setBackground(Color.black);
					item.setForeground(Color.white);
				}
			}
		} else {
			for (JComponent item : items) {
				if (item instanceof JPanel) {
					item.setBackground(Color.white);
				} 
				else if (item instanceof JTextComponent) {
                    item.setBackground(Color.white);
                    item.setForeground(Color.black);
                }
				else {
					item.setBackground(Color.white);
					item.setForeground(Color.black);
				}
			}
		}
	}

	public Object[] listFilesInStatus() {
		if (gitSubprocessClient == null) {
			showLoadFail();
			return new String[0];
		}
		else {
			ArrayList<String> files = new ArrayList<String>();
			StringTokenizer changed = new StringTokenizer(gitSubprocessClient.runGitCommand("ls-files -m"));
			while (changed.hasMoreElements()) {
				files.add(changed.nextToken());
			}
			StringTokenizer untracked = new StringTokenizer(gitSubprocessClient.runGitCommand("ls-files --others --exclude-standard"));
			while (untracked.hasMoreElements()) {
				files.add(untracked.nextToken());
			}
			StringTokenizer staged = new StringTokenizer(gitSubprocessClient.runGitCommand("diff --name-only --cached"));
			while (staged.hasMoreElements()) {
				files.add(staged.nextToken());
			}
			return files.toArray();
		}
	}

}
