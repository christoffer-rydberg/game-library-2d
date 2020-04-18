package com.gamelibrary2d.util.io;

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

    public File browse() throws IOException {
        JFrame frame = new JFrame();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.ICONIFIED);
        frame.setExtendedState(JFrame.NORMAL);

        JFileChooser fc = new JFileChooser(currentDirectory);
        fc.setSelectedFile(selectedFile);
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            setSelectedFile(fc.getSelectedFile());
            setCurrentDirectory(fc.getCurrentDirectory().getAbsolutePath());
            frame.setVisible(false);
            frame.dispose();
            return selectedFile;
        }

        frame.setVisible(false);
        frame.dispose();
        return null;
    }

    private void loadCurrentDirectory(String filePath) throws IOException {
        var file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            try (var reader = new BufferedReader(new FileReader(file))) {
                currentDirectory = reader.readLine();
            }
        }
    }

    private void saveCurrentDirectory(String filePath) throws IOException {
        var file = new File(filePath);
        file.getParentFile().mkdirs();
        try (var writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(currentDirectory);
        }
    }
}