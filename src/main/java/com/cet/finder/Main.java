
package com.cet.finder;

import com.cet.finder.ui.SearchPanel;
import com.cet.finder.ui.CutoffPredictorPanel;
import com.cet.finder.ui.ShortlistPanel;
import com.cet.finder.ui.AdminPanel;
import com.cet.finder.ui.CsvToolsPanel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MHT-CET College Finder");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            frame.setMinimumSize(new Dimension(1000, 650));
            frame.setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();

            tabs.addTab("College Search", new SearchPanel());
            tabs.addTab("Cutoff Predictor", new CutoffPredictorPanel());
            tabs.addTab("My Shortlist", new ShortlistPanel());
            tabs.addTab("Admin Cutoff Management", new AdminPanel());
            tabs.addTab("CSV Import / Export", new CsvToolsPanel());

            frame.setContentPane(tabs);
            frame.setVisible(true);
        });
    }
}
