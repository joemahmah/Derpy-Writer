/*
 * The MIT License
 *
 * Copyright 2015 Michael Hrcek <hrcekmj@clarkson.edu>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hrcek.core;

import static hrcek.core.Boot.isFilenameValid;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class DerpyGUI extends JFrame {

    JTabbedPane mainPane;
    JPanel controlPanel;
    JPanel outputPanel;
    JPanel settingsPanel;
    JPanel aboutPanel;

    public DerpyGUI() {

        final JTextArea outputText = new JTextArea();

        //Control Area
        controlPanel = new JPanel(new GridBagLayout());

        JButton addSourceButton = new JButton("Add Source");
        addSourceButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int selection = fileChooser.showOpenDialog(null);

                if (selection == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                    if (isFilenameValid(filePath)) {
                        if (new File(filePath).exists()) {
                            DerpyManager.getSources().add(new File(filePath).getAbsolutePath());
                            DerpyManager.getWeights().add(1);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, filePath + " is an invalid file path!");
                    }
                }
            }
        });

        JButton readButton = new JButton("Read");
        readButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (DerpyManager.checkIfHasWritingSource()) {
                    try {
                        if (DerpyManager.getDictionary() == null && DerpyManager.getInputDictionary() == null) {
                            DerpyManager.setDictionary(new Dictionary());
                        } else if (DerpyManager.getDictionary() != null && DerpyManager.getInputDictionary() == null) {
                            //Prompt to use same dictionary
                        } else if (DerpyManager.getInputDictionary() != null) {
                            DerpyManager.loadDictionary();
                        }
                        DerpyManager.setWordAccuracy();

                        DerpyManager.readSources();
                        DerpyManager.checkIfRequestedAccuracyIsWithinAcceptableBounds();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DerpyGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You must use at least one source file!");
                }

            }
        });

        JButton writeButton = new JButton("Write");
        writeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (DerpyManager.getDictionary() != null) {
                    outputText.setText(DerpyManager.write());
                } else {
                    JOptionPane.showMessageDialog(null, "Dictionary is empty! Read material or load a dictionary to write!");
                }
            }
        });

        controlPanel.add(addSourceButton);
        controlPanel.add(readButton);
        controlPanel.add(writeButton);

        //Control Area
        outputPanel = new JPanel(new GridLayout(1, 1));

        outputText.setEditable(false);
        outputText.setLineWrap(true);
        outputText.setWrapStyleWord(true);
        JScrollPane outputPane = new JScrollPane(outputText);

        outputPanel.add(outputPane);

        //Settings Area
        settingsPanel = new JPanel(new GridLayout(15, 1));

        JCheckBox ignorePunctuation = new JCheckBox("Ignore Punctuation", DerpyManager.isIgnorePunctuation());
        ignorePunctuation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox box = (JCheckBox) e.getSource();
                DerpyManager.setIgnorePunctuation(box.isSelected());
            }
        });

        JCheckBox formatText = new JCheckBox("Format Text", DerpyManager.isFormatText());
        formatText.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox box = (JCheckBox) e.getSource();
                DerpyManager.setFormatText(box.isSelected());
            }
        });

        final JFormattedTextField accuracy = new JFormattedTextField(NumberFormat.getIntegerInstance());
        accuracy.setText("" + DerpyManager.getAccuracy());
        accuracy.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() < KeyEvent.VK_0 || e.getKeyChar() > KeyEvent.VK_9) {
                    System.out.println(e.getKeyChar());
                    e.consume();
                }
            }
        });

        final JFormattedTextField wordCount = new JFormattedTextField(NumberFormat.getIntegerInstance());
        wordCount.setText("" + DerpyManager.getOutput());
        wordCount.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() < KeyEvent.VK_0 || e.getKeyChar() > KeyEvent.VK_9) {
                    System.out.println(e.getKeyChar());
                    e.consume();
                }
            }
        });

        JButton updateButton = new JButton("Update Settings");
        updateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                //Update 
                //JFormattedTextField text = (JFormattedTextField) e.getSource();
                DerpyManager.setOutput(Integer.parseInt(wordCount.getText().replace(",", "")));
                DerpyManager.setAccuracy(Integer.parseInt(accuracy.getText().replace(",", "")));
            }
        });

        settingsPanel.add(ignorePunctuation);
        settingsPanel.add(formatText);
        settingsPanel.add(accuracy);
        settingsPanel.add(wordCount);
        settingsPanel.add(updateButton);

        //About and Help Area
        aboutPanel = new JPanel(new GridLayout(2, 1));
        JTextArea aboutPanelHelpText = new JTextArea(Boot.showUsageAsString());
        JTextArea aboutPaneAboutText = new JTextArea("About");
        aboutPaneAboutText.setEditable(false);
        aboutPanelHelpText.setEditable(false);
        aboutPanel.add(aboutPaneAboutText);
        aboutPanel.add(aboutPanelHelpText);

        //Main pane
        mainPane = new JTabbedPane();
        mainPane.addTab("Control", controlPanel);
        mainPane.addTab("Output", outputPanel);
        mainPane.addTab("Settings", settingsPanel);
        mainPane.addTab("About/Help", aboutPanel);

        setSize(800, 800);
        setResizable(true);
        setTitle("Derpy Writer");
        setContentPane(mainPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

    }

}
