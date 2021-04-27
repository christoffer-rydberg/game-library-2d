package com.gamelibrary2d.tools.particlegenerator.panels;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class FileChooser {

    private final String tmpPath;

    private String currentDirectory;
    private File selectedFile;

    public FileChooser() {
        tmpPath = null;
    }

    public FileChooser(String tmpPath) {
        try {
            loadCurrentDirectory(tmpPath);
        } catch (IOException e) {
            e.printStackTrace();
            tmpPath = null;
        }

        this.tmpPath = tmpPath;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) throws IOException {
        this.currentDirectory = currentDirectory;
        if (tmpPath != null) {
            saveCurrentDirectory(tmpPath);
        }
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    private void applyBrowseMode(JFileChooser fileChooser, FileSelectionMode mode) {
        switch (mode) {
            case FILES_ONLY:
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                break;
            case DIRECTORIES_ONLY:
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                break;
            case FILES_AND_DIRECTORIES:
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                break;
        }
    }

    public File browse(FileSelectionMode fileSelectionMode) throws IOException {
        JFrame frame = new JFrame();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.ICONIFIED);
        frame.setExtendedState(JFrame.NORMAL);

        JFileChooser fileChooser = new JFileChooser(currentDirectory);
        applyBrowseMode(fileChooser, fileSelectionMode);
        fileChooser.setSelectedFile(selectedFile);
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            setSelectedFile(fileChooser.getSelectedFile());
            setCurrentDirectory(fileChooser.getCurrentDirectory().getAbsolutePath());
            frame.setVisible(false);
            frame.dispose();
            return selectedFile;
        }

        frame.setVisible(false);
        frame.dispose();
        return null;
    }

    private void loadCurrentDirectory(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                currentDirectory = reader.readLine();
            }
        }
    }

    private void saveCurrentDirectory(String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(currentDirectory);
        }
    }
}