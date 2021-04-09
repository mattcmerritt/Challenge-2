import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

import git.tools.client.GitSubprocessClient;

public class App {

	private String repoPath;
	private GitSubprocessClient gitSubprocessClient;

	private JTextArea statusText;
	private JLabel loadFailLabel;
	private JScrollPane statusPane;
	private JComboBox<String> fileDropdown;

	private String[] changedFiles;

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
		JPanel panel1 = new JPanel();
		panel1.setBackground(Color.blue);

		centerPanel.add(panel1);

		// setting up individual file actions panel to center
		JPanel individualFilePanel = new JPanel();
		individualFilePanel.setLayout(new BoxLayout(individualFilePanel, BoxLayout.Y_AXIS));

		JLabel individualFileLabel = new JLabel("Individual File Actions:");
		JLabel selectFileLabel = new JLabel("Select File:");

		changedFiles = new String[0];

		fileDropdown = new JComboBox(changedFiles);
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
		addFileButton.setMaximumSize(new Dimension(200, 25));
		restoreFileButton.setMaximumSize(new Dimension(200, 25));
		unstageFileButton.setMaximumSize(new Dimension(200, 25));

		addFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedFile = fileDropdown.getSelectedItem().toString();
				addIndividualFile(selectedFile);
				updateGitStatus();
			}
		});

		restoreFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedFile = fileDropdown.getSelectedItem().toString();
				restoreIndividualFile(selectedFile);
				updateGitStatus();
			}
		});

		unstageFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedFile = fileDropdown.getSelectedItem().toString();
				unstageIndividualFile(selectedFile);
				updateGitStatus();
			}
		});

		individualFilePanel.add(individualFileLabel);
		individualFilePanel.add(Box.createRigidArea(new Dimension(0, 25))); // spacing
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

			if (statusText.getText().indexOf("fatal") == 0) {
				showLoadFail();
			}
		}

		statusText.setText(statusText.getText() + "\n"); // moves the status pane to show the left of the panel, not right
	}

	public void addIndividualFile(String filename) {
		if (gitSubprocessClient == null) {
			showLoadFail();
		}
		else {
			System.out.println(gitSubprocessClient.gitAddFile(filename));
		}
	}

	public void restoreIndividualFile(String filename) {
		if (gitSubprocessClient == null) {
			showLoadFail();
		}
		else {
			System.out.println(gitSubprocessClient.runGitCommand("restore " + filename));
		}
	}

	public void unstageIndividualFile(String filename) {
		if (gitSubprocessClient == null) {
			showLoadFail();
		}
		else {
			System.out.println(gitSubprocessClient.runGitCommand("restore --staged " + filename));
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
