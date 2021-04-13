# Git Helper

## What is this?

This is a GUI-based application deisgned to help developers who are not familiar with using Git or working with a terminal in general. It offers features like adding and restoring files, creating commits, pushing to a remote repository, and pulling from a branch in the remote repository.

## Requirements

* Java 8 or higher
* For Windows, Git Subprocess Client v0.0.9 or higher, [available on GitHub](https://github.com/CSC109/GitSubprocessClient/releases/tag/v0.0.9)
* For Mac, Git Subprocess Client v0.0.12 or higher, [available on GitHub](https://github.com/CSC109/GitSubprocessClient/releases/tag/v0.0.12)

## Setup

To begin, clone the repository onto your machine.

In order to use the application, it is recommended that you use an IDE to manage the dependencies. Instructions are provided for how to configure your project in both Eclipse and IntelliJ.

If you choose to use another IDE, you will need to add the GitSubprocessClient JAR file to the list of dependencies, otherwise the app will not work.

* If you are using Eclipse, follow these [instructions](./EclipseSetup).
* If you are using IntelliJ, follow these [instructions](./IntelliJSetup).

Once you have finished configuring your project, you will be ready to use the application. To run the application, select the ``App.java`` class and press run.

## Usage

Once the application starts up, the user will be shown a window that can be divided into five sections. The user will be able to select a local repository from their computer and then use a variety of different Git commands in order to make changes to the repository. 

A more thorough description of how to use the application can be found [here](./usage).

The application assumes that the user has already made changes to the files stored in the repository before launching the application, but it is possible to refresh the Git status to update the application. 

Additionally, the user should not delete the repository that they are currently working with, as this will cause problems for the application.