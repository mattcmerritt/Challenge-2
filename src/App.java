import javax.swing.*;
import java.awt.*;

public class App {

	public App() {
		JFrame mainWindow = new JFrame("Git Helper");

		JPanel mainPanel = new JPanel(new BorderLayout());

		// setting up the repo selection panel
		JPanel repoSelectPanel = new JPanel();

		JLabel selectRepoLabel = new JLabel("Filepath for repo:");
		JTextField repoInputBox = new JTextField(50);
		JButton submitRepoButton = new JButton("Open Repo");

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
