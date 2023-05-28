package my.texteditor;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import java.awt.Component;

import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import say.swing.JFontChooser;
import javax.swing.undo.UndoManager;

public class TextEditorUI extends javax.swing.JFrame {

    String filename;

    Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();

    Icon logo = new ImageIcon(getClass().getResource("/my/resources/logo.png"));

    private Highlighter highlighter;
    private HighlightPainter painter;

    JTextArea textArea = new JTextArea();
    JScrollPane scroll = new JScrollPane();

    UndoManager um = new UndoManager();

    public TextEditorUI() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Notepad--");
        undoable();
        Clock clock = new Clock(lblTime);
        clock.start();
        showDateTime();
        updateWordsChars(txtEditor);
        lineNumbers(txtEditor, edtScrollPane);
    }

    public void undoable() {
        this.txtEditor.getDocument().addUndoableEditListener(um);
    }

    public void spellChecker(JTextArea txt) {

    }

    public void lineNumbers(JTextArea txt, JScrollPane scrollPane) {
        JTextLiner n = new JTextLiner(txt);
        scrollPane.setRowHeaderView(n);
    }

    public void updateWordsChars(JTextArea txtArea) {
        Tools tool = new Tools();
        String txt = txtArea.getText();
        txtWords.setText(String.valueOf(tool.wordCount(txt)));
        txtChars.setText(String.valueOf(tool.characterCount(txt)));
    }

    public void showDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy/MM/dd");
        LocalDateTime.now();
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        lblDate.setText(dtf.format(java.time.LocalDate.now()));
        lblTime.setText((java.time.LocalTime.now().toString()));
        lblDay.setText(String.valueOf(dayOfWeek).toLowerCase());
    }

    public void openFile() {
        FileDialog fileDialog = new FileDialog(this, "Open File", FileDialog.LOAD);
        fileDialog.setVisible(true);
        if (fileDialog.getFile() != null) {
            filename = fileDialog.getDirectory() + fileDialog.getFile();
        }
        try {

            BufferedReader reader = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                txtEditor.setText(sb.toString());
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("File not found!");
        }

    }

    public void saveFile() {
        FileDialog fileDialog = new FileDialog(this, "Save File", FileDialog.SAVE);
        fileDialog.setVisible(true);

        if (fileDialog.getFile() != null) {
            filename = fileDialog.getDirectory() + fileDialog.getFile();

            JLabel lblTitle = new JLabel(filename);

            try {
                FileWriter writer = new FileWriter(filename + ".txt");
                writer.write(txtEditor.getText());

                tabbedPane.setTabComponentAt(tabbedPane.getSelectedIndex(), lblTitle);
                writer.close();
            } catch (IOException ex) {
                System.out.println("File not Found!");
            }
        }
        // updateWordsChars(textArea);
    }

    public void printText() {
        try {
            boolean complete = txtEditor.print();

            if (complete) {
                JOptionPane.showMessageDialog(null, "Done Printing", "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Printing", "Printer",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void exitApp() {
        int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?",
                "Exit", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null);
        if (answer == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void copyText() {
        String copyText = txtEditor.getSelectedText();
        StringSelection copySelection = new StringSelection(copyText);
        cb.setContents(copySelection, copySelection);
    }

    public void cutText() {
        String cutText = txtEditor.getSelectedText();
        StringSelection cutSelection = new StringSelection(cutText);
        cb.setContents(cutSelection, cutSelection);
        txtEditor.replaceRange("", txtEditor.getSelectionStart(), txtEditor.getSelectionEnd());
        updateWordsChars(txtEditor);
    }

    public void pasteText() {
        try {
            Transferable pasteText = cb.getContents(this);
            String sel = (String) pasteText.getTransferData(DataFlavor.stringFlavor);
            txtEditor.replaceRange(sel, txtEditor.getSelectionStart(), txtEditor.getSelectionEnd());

        } catch (Exception e) {
            System.out.println("Error!");
        }
        updateWordsChars(txtEditor);
    }

    public void undoText() {
        um.undo();
        /* int caretPosition = txtEditor.getCaretPosition();
        String text = txtEditor.getText();

        String lastWord = text.substring(caretPosition - text.indexOf(" "), caretPosition);

        txtEditor.setText(text.replace(lastWord, ""));

        // Set the caret position to the end of the text.
        txtEditor.setCaretPosition(txtEditor.getText().length()); */
    }

    public void redoText() {
        um.redo();
    }

    public void findText() {
        String searchQuery = JOptionPane.showInputDialog(TextEditorUI.this, "Enter word to find:");
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String text = txtEditor.getText();

            try {
                int startIndex = 0;
                while ((startIndex = text.indexOf(searchQuery, startIndex)) != -1) {
                    int endIndex = startIndex + searchQuery.length();
                    highlighter.addHighlight(startIndex, endIndex, painter);
                    startIndex = endIndex;
                }
                if (highlighter.getHighlights().length == 0) {
                    JOptionPane.showMessageDialog(TextEditorUI.this, "Word not found.");
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void findAndReplaceText() {
        String searchText = JOptionPane.showInputDialog(this, "Text to replace:");
        String replaceText = JOptionPane.showInputDialog(this, "Enter replacement text:");
        if (searchText != null && replaceText != null) {
            String text = txtEditor.getText();
            text = text.replaceAll(searchText, replaceText);
            txtEditor.setText(text);
            updateWordsChars(txtEditor);
        }
    }

    public void selectAll() {
        txtEditor.selectAll();
    }

    public void wordWrapText() {
        if (menuWordWrap.isSelected() || btnWordWrap.isSelected()) {
            txtEditor.setLineWrap(true);
            txtEditor.setWrapStyleWord(true);
        } else {
            txtEditor.setLineWrap(false);
            txtEditor.setWrapStyleWord(false);
        }
    }

    public void changeFont() {
        JFontChooser chooser = new JFontChooser();
        chooser.setSelectedFont(txtEditor.getFont());
        int fontChosen = chooser.showDialog(this);
        if (fontChosen == JFontChooser.OK_OPTION) {
            txtEditor.setFont(chooser.getSelectedFont());
        }

    }

    public void aboutNotepad() {
        JOptionPane.showMessageDialog(null,
                "Notepad-- (64 bit) \n Version 2.0.5 \n Developed by: Kaye Anne Mirador",
                "Notepad Negative",
                JOptionPane.YES_NO_CANCEL_OPTION, logo);
    }

    public void analogClockLoad() {
        JFrame frame = new JFrame("Analog Clock");
        GraphicsConfiguration config = frame.getGraphicsConfiguration();
        Rectangle rect = config.getBounds();

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new AnalogClock());
        frame.pack();
        int x = (int) rect.getMaxX() - frame.getWidth();
        int y = (int) rect.getMaxY() - frame.getHeight();
        frame.setLocation(x, y - 45);
        frame.setVisible(true);
    }

    public void newFile(JTextArea txtArea) {
        if (txtArea.getText() != null) {
            int ans = JOptionPane.showConfirmDialog(null,
                    "Do you want to save changes to " + this.getTitle() + "?",
                    "Notepad Negative",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (ans == JOptionPane.NO_OPTION) {

            } else if (ans == JOptionPane.YES_OPTION) {
                FileDialog fileDialog = new FileDialog(this, "Save File", FileDialog.SAVE);
                fileDialog.setVisible(true);

                String filePath = fileDialog.getDirectory() + fileDialog.getFile();
                try {
                    FileWriter writer = new FileWriter(filePath);
                    writer.write(txtArea.getText());
                    writer.close();
                } catch (IOException ex) {
                    System.out.println("An error occured");
                }
            }
        }
    }

    public void newTab(JTextArea txt) {
        txt = new JTextArea();
        txt.setColumns(1);
        txt.setFont(new java.awt.Font("Courier New", 0, 12));
        txt.setRows(5);

        scroll = new JScrollPane(txt);
        lineNumbers(txt, scroll);
        keyPressedOfNewTabArea(txt);

        JLabel lblTitle = new JLabel("New tab");

        tabbedPane.addTab("new", scroll);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, lblTitle);

    }

    public void keyPressedOfNewTabArea(JTextArea txt) {
        txt.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                updateWordsChars(txt);

            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateWordsChars(txt);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                updateWordsChars(txt);
            }
        });

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblDate = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        lblDay = new javax.swing.JLabel();
        lblDay2 = new javax.swing.JLabel();
        lblDay1 = new javax.swing.JLabel();
        txtWords = new javax.swing.JTextField();
        txtChars = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        checkboxDarkMode = new javax.swing.JCheckBox();
        tabbedPane = new javax.swing.JTabbedPane();
        edtScrollPane = new javax.swing.JScrollPane();
        txtEditor = new javax.swing.JTextArea();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnOpen = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnCut = new javax.swing.JButton();
        btnCopy = new javax.swing.JButton();
        btnPaste = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnWordWrap = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        panelNotepad = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuNew = new javax.swing.JMenuItem();
        menuOpen = new javax.swing.JMenuItem();
        menuSave = new javax.swing.JMenuItem();
        menuSaveAs = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuPrint = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuUndo = new javax.swing.JMenuItem();
        menuRedo = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuCut = new javax.swing.JMenuItem();
        menuCopy = new javax.swing.JMenuItem();
        menuPaste = new javax.swing.JMenuItem();
        menuDelete = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        menuSearch = new javax.swing.JMenuItem();
        menuFind = new javax.swing.JMenuItem();
        menuReplace = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        menuSelect = new javax.swing.JMenuItem();
        menuFormat = new javax.swing.JMenu();
        menuWordWrap = new javax.swing.JCheckBoxMenuItem();
        menuFont = new javax.swing.JMenuItem();
        View = new javax.swing.JMenu();
        menuClock = new javax.swing.JMenuItem();
        menuPomodoro = new javax.swing.JMenuItem();
        menuAbout = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        lblDate.setBackground(new java.awt.Color(255, 204, 204));
        lblDate.setFont(new java.awt.Font("DS-Digital", 0, 18)); // NOI18N
        lblDate.setForeground(new java.awt.Color(51, 51, 51));
        lblDate.setText("Date ");
        lblDate.setOpaque(true);

        lblTime.setBackground(new java.awt.Color(0, 0, 0));
        lblTime.setFont(new java.awt.Font("DS-Digital", 0, 18)); // NOI18N
        lblTime.setForeground(new java.awt.Color(51, 51, 51));
        lblTime.setText("Time");

        lblDay.setFont(new java.awt.Font("Muthiara -Demo Version-", 0, 36)); // NOI18N
        lblDay.setForeground(new java.awt.Color(51, 51, 51));
        lblDay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDay.setText("Day");

        lblDay2.setFont(new java.awt.Font("Muthiara -Demo Version-", 0, 18)); // NOI18N
        lblDay2.setForeground(new java.awt.Color(51, 51, 51));
        lblDay2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDay2.setText("words");

        lblDay1.setFont(new java.awt.Font("Muthiara -Demo Version-", 0, 18)); // NOI18N
        lblDay1.setForeground(new java.awt.Color(51, 51, 51));
        lblDay1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDay1.setText("characters");

        txtWords.setEditable(false);
        txtWords.setFont(new java.awt.Font("DS-Digital", 0, 18)); // NOI18N
        txtWords.setForeground(new java.awt.Color(51, 51, 51));
        txtWords.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtWords.setAutoscrolls(false);
        txtWords.setFocusable(false);

        txtChars.setEditable(false);
        txtChars.setFont(new java.awt.Font("DS-Digital", 0, 18)); // NOI18N
        txtChars.setForeground(new java.awt.Color(51, 51, 51));
        txtChars.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtChars.setAutoscrolls(false);
        txtChars.setFocusable(false);

        jCheckBox1.setFont(new java.awt.Font("Nirmala UI", 0, 12)); // NOI18N
        jCheckBox1.setForeground(new java.awt.Color(51, 51, 51));
        jCheckBox1.setText("Autosave");

        checkboxDarkMode.setFont(new java.awt.Font("Nirmala UI", 0, 12)); // NOI18N
        checkboxDarkMode.setForeground(new java.awt.Color(51, 51, 51));
        checkboxDarkMode.setText("Dark Mode");
        checkboxDarkMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxDarkModeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(lblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblDay, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 251, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblDay2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblDay1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtWords, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(77, 77, 77))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(checkboxDarkMode)
                                .addGap(53, 53, 53)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addComponent(txtChars, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBox1)
                            .addComponent(checkboxDarkMode))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtWords, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtChars, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDay1)
                            .addComponent(lblDay2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblDay, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14))))
        );

        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });

        edtScrollPane.setAutoscrolls(true);
        edtScrollPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        edtScrollPane.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        txtEditor.setColumns(1);
        txtEditor.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        txtEditor.setRows(5);
        txtEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEditorKeyPressed(evt);
            }
        });
        edtScrollPane.setViewportView(txtEditor);

        tabbedPane.addTab("Untitled", edtScrollPane);

        jToolBar1.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar1.setRollover(true);
        jToolBar1.setOpaque(false);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/newfile.png"))); // NOI18N
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/openfolder.png"))); // NOI18N
        btnOpen.setToolTipText("");
        btnOpen.setFocusable(false);
        btnOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        jToolBar1.add(btnOpen);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/save-file.png"))); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/close-file.png"))); // NOI18N
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnClose);

        btnCut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/cut.png"))); // NOI18N
        btnCut.setFocusable(false);
        btnCut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCutActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCut);

        btnCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/copy.png"))); // NOI18N
        btnCopy.setFocusable(false);
        btnCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCopy);

        btnPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/paste.png"))); // NOI18N
        btnPaste.setFocusable(false);
        btnPaste.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPaste.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPaste);

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/undo.png"))); // NOI18N
        jButton8.setToolTipText("");
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton8);

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/redo.png"))); // NOI18N
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton9);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/find.png"))); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/replace.png"))); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/selection.png"))); // NOI18N
        jButton11.setFocusable(false);
        jButton11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton11);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/printer.png"))); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        btnWordWrap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/justify-text.png"))); // NOI18N
        btnWordWrap.setToolTipText("");
        btnWordWrap.setFocusable(false);
        btnWordWrap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnWordWrap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnWordWrap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWordWrapActionPerformed(evt);
            }
        });
        jToolBar1.add(btnWordWrap);

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/font.png"))); // NOI18N
        jButton7.setToolTipText("Font");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton7);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/clock.png"))); // NOI18N
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton5);

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/information.png"))); // NOI18N
        jButton10.setFocusable(false);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton10);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/resources/icons/exit.png"))); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        panelNotepad.setBackground(new java.awt.Color(204, 255, 204));
        panelNotepad.setBorder(null);
        panelNotepad.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        menuFile.setBackground(new java.awt.Color(255, 204, 204));
        menuFile.setText("File");

        menuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuNew.setText("New");
        menuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewActionPerformed(evt);
            }
        });
        menuFile.add(menuNew);

        menuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuOpen.setText("Open");
        menuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenActionPerformed(evt);
            }
        });
        menuFile.add(menuOpen);

        menuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuSave.setText("Save");
        menuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveActionPerformed(evt);
            }
        });
        menuFile.add(menuSave);

        menuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuSaveAs.setText("Save As");
        menuFile.add(menuSaveAs);
        menuFile.add(jSeparator4);

        menuPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuPrint.setText("Print");
        menuPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPrintActionPerformed(evt);
            }
        });
        menuFile.add(menuPrint);
        menuFile.add(jSeparator2);

        menuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        menuFile.add(menuExit);

        panelNotepad.add(menuFile);

        menuEdit.setText("Edit");

        menuUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuUndo.setText("Undo");
        menuUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuUndoActionPerformed(evt);
            }
        });
        menuEdit.add(menuUndo);

        menuRedo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuRedo.setText("Redo");
        menuRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRedoActionPerformed(evt);
            }
        });
        menuEdit.add(menuRedo);
        menuEdit.add(jSeparator1);

        menuCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuCut.setText("Cut");
        menuCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCutActionPerformed(evt);
            }
        });
        menuEdit.add(menuCut);

        menuCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuCopy.setText("Copy");
        menuCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCopyActionPerformed(evt);
            }
        });
        menuEdit.add(menuCopy);

        menuPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuPaste.setText("Paste");
        menuPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPasteActionPerformed(evt);
            }
        });
        menuEdit.add(menuPaste);

        menuDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuDelete.setText("Delete");
        menuEdit.add(menuDelete);
        menuEdit.add(jSeparator3);

        menuSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuSearch.setText("Search with Bing");
        menuEdit.add(menuSearch);

        menuFind.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuFind.setText("Find");
        menuFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFindActionPerformed(evt);
            }
        });
        menuEdit.add(menuFind);

        menuReplace.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuReplace.setText("Replace");
        menuReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuReplaceActionPerformed(evt);
            }
        });
        menuEdit.add(menuReplace);
        menuEdit.add(jSeparator5);

        menuSelect.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuSelect.setText("Select All");
        menuSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSelectActionPerformed(evt);
            }
        });
        menuEdit.add(menuSelect);

        panelNotepad.add(menuEdit);

        menuFormat.setText("Format");

        menuWordWrap.setText("Word Wrap");
        menuWordWrap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuWordWrapActionPerformed(evt);
            }
        });
        menuFormat.add(menuWordWrap);

        menuFont.setText("Font");
        menuFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFontActionPerformed(evt);
            }
        });
        menuFormat.add(menuFont);

        panelNotepad.add(menuFormat);

        View.setText("View");

        menuClock.setText("Analog Clock");
        menuClock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuClockActionPerformed(evt);
            }
        });
        View.add(menuClock);

        menuPomodoro.setText("Pomodoro");
        View.add(menuPomodoro);

        panelNotepad.add(View);

        menuAbout.setText("About");
        menuAbout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuAboutMouseClicked(evt);
            }
        });
        panelNotepad.add(menuAbout);

        setJMenuBar(panelNotepad);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabbedPane)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void menuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewActionPerformed
        newTab(textArea);

    }//GEN-LAST:event_menuNewActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        newTab(textArea);

    }//GEN-LAST:event_btnNewActionPerformed

    private void menuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenActionPerformed
        openFile();
    }//GEN-LAST:event_menuOpenActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        openFile();
    }//GEN-LAST:event_btnOpenActionPerformed

    private void menuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveActionPerformed
        saveFile();
    }//GEN-LAST:event_menuSaveActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveFile();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void menuPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPrintActionPerformed
        printText();
    }//GEN-LAST:event_menuPrintActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        printText();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        exitApp();
    }//GEN-LAST:event_menuExitActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        exitApp();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void menuUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuUndoActionPerformed
        undoText();
    }//GEN-LAST:event_menuUndoActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        undoText();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void menuRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRedoActionPerformed
        redoText();
    }//GEN-LAST:event_menuRedoActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        redoText();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void menuCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCutActionPerformed
        cutText();
    }//GEN-LAST:event_menuCutActionPerformed

    private void btnCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCutActionPerformed
        cutText();
    }//GEN-LAST:event_btnCutActionPerformed

    private void menuCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCopyActionPerformed
        copyText();
    }//GEN-LAST:event_menuCopyActionPerformed

    private void btnCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyActionPerformed
        copyText();
    }//GEN-LAST:event_btnCopyActionPerformed

    private void menuPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPasteActionPerformed
        pasteText();
    }//GEN-LAST:event_menuPasteActionPerformed

    private void btnPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasteActionPerformed
        pasteText();
    }//GEN-LAST:event_btnPasteActionPerformed

    private void menuFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFindActionPerformed
        findText();
    }//GEN-LAST:event_menuFindActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        findText();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void menuReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuReplaceActionPerformed
        findAndReplaceText();
    }//GEN-LAST:event_menuReplaceActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        findAndReplaceText();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void menuSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSelectActionPerformed
        selectAll();
    }//GEN-LAST:event_menuSelectActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        selectAll();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void menuWordWrapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuWordWrapActionPerformed
        wordWrapText();
    }//GEN-LAST:event_menuWordWrapActionPerformed

    private void btnWordWrapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWordWrapActionPerformed
        txtEditor.setLineWrap(true);
        txtEditor.setWrapStyleWord(true);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }//GEN-LAST:event_btnWordWrapActionPerformed

    private void menuFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFontActionPerformed
        changeFont();
    }//GEN-LAST:event_menuFontActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        changeFont();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void menuClockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuClockActionPerformed
        analogClockLoad();
    }//GEN-LAST:event_menuClockActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        analogClockLoad();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void menuAboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuAboutMouseClicked
        aboutNotepad();
    }//GEN-LAST:event_menuAboutMouseClicked

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        aboutNotepad();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged

        int selectedIndex = tabbedPane.getSelectedIndex();
        System.out.println(selectedIndex);
        textArea.getText();
        updateWordsChars(textArea);

        if (selectedIndex == 0) {
            updateWordsChars(txtEditor);
        } else {
            // gets the component of the jscrollpane and cast the view to the jTextArea
            Component jcomp = tabbedPane.getComponentAt(selectedIndex);

            JScrollPane scrollPane = (JScrollPane) jcomp;

            JViewport viewport = scrollPane.getViewport();

            Component view = viewport.getView();

            textArea = (JTextArea) view;
            
            String text = textArea.getText();
            System.out.println("did");
            System.out.println(text);
            updateWordsChars(textArea);

        }
    }//GEN-LAST:event_tabbedPaneStateChanged

    private void checkboxDarkModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxDarkModeActionPerformed
        if (checkboxDarkMode.isSelected()) {

            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FlatAnimatedLafChange.showSnapshot();
                    FlatDraculaIJTheme.setup();
                    FlatLaf.updateUI();
                    FlatAnimatedLafChange.hideSnapshotWithAnimation();

                }
            });
        } else {

            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FlatAnimatedLafChange.showSnapshot();
                    FlatSolarizedLightIJTheme.setup();
                    FlatLaf.updateUI();
                    FlatAnimatedLafChange.hideSnapshotWithAnimation();

                }
            });

        }
    }//GEN-LAST:event_checkboxDarkModeActionPerformed

    private void txtEditorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEditorKeyPressed
        updateWordsChars(txtEditor);
    }//GEN-LAST:event_txtEditorKeyPressed

    public static void main(String args[]) {
        FlatSolarizedLightIJTheme.setup();

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Login().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu View;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnCopy;
    private javax.swing.JButton btnCut;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnPaste;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnWordWrap;
    private javax.swing.JCheckBox checkboxDarkMode;
    private javax.swing.JScrollPane edtScrollPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDay;
    private javax.swing.JLabel lblDay1;
    private javax.swing.JLabel lblDay2;
    private javax.swing.JLabel lblTime;
    private javax.swing.JMenu menuAbout;
    private javax.swing.JMenuItem menuClock;
    private javax.swing.JMenuItem menuCopy;
    private javax.swing.JMenuItem menuCut;
    private javax.swing.JMenuItem menuDelete;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuFind;
    private javax.swing.JMenuItem menuFont;
    private javax.swing.JMenu menuFormat;
    private javax.swing.JMenuItem menuNew;
    private javax.swing.JMenuItem menuOpen;
    private javax.swing.JMenuItem menuPaste;
    private javax.swing.JMenuItem menuPomodoro;
    private javax.swing.JMenuItem menuPrint;
    private javax.swing.JMenuItem menuRedo;
    private javax.swing.JMenuItem menuReplace;
    private javax.swing.JMenuItem menuSave;
    private javax.swing.JMenuItem menuSaveAs;
    private javax.swing.JMenuItem menuSearch;
    private javax.swing.JMenuItem menuSelect;
    private javax.swing.JMenuItem menuUndo;
    private javax.swing.JCheckBoxMenuItem menuWordWrap;
    private javax.swing.JMenuBar panelNotepad;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField txtChars;
    private javax.swing.JTextArea txtEditor;
    private javax.swing.JTextField txtWords;
    // End of variables declaration//GEN-END:variables

}
