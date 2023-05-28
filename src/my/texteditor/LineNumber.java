/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.Element;

public class LineNumber extends JTextArea {

    private JTextArea textArea;

    public LineNumber(JTextArea textArea) {
        this.textArea = textArea;
        setBackground(Color.LIGHT_GRAY);
        setEditable(false);
        
    }

    public void updateLineNumbers() {
        String lineNumbersText = getLineNumbersText();
        setText(lineNumbersText);
    }

    private String getLineNumbersText() {
        int caretPosition = textArea.getDocument().getLength();
        Element root = textArea.getDocument().getDefaultRootElement();
        StringBuilder lineNumbersTextBuilder = new StringBuilder();
        lineNumbersTextBuilder.append("     1").append(System.lineSeparator());

        for (int elementIndex = 2; elementIndex < root.getElementIndex(caretPosition) + 2; elementIndex++) {
            lineNumbersTextBuilder.append("     " + elementIndex).append(System.lineSeparator());
        }

        return lineNumbersTextBuilder.toString();
    }
}
