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
        this.tmpPath = tmpPath;
        loadCurrentDirectoryFromTempPath();
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
        if (tmpPath != null) {
            saveCurrentDirectoryToTempPath();
        }
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    public File browse() {
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

    private void loadCurrentDirectoryFromTempPath() {
        BufferedReader reader = null;
        try {
            File f = new File(tmpPath);
            if (f.exists() && !f.isDirectory()) {
                reader = new BufferedReader(new FileReader(f));
                currentDirectory = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }
    }

    private void saveCurrentDirectoryToTempPath() {
        BufferedWriter writer = null;
        try {
            File file = new File(tmpPath);
            file.getParentFile().mkdirs();
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(currentDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }
}