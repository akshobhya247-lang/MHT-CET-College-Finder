
package com.cet.finder.ui;

import com.cet.finder.dao.PreferenceDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ShortlistPanel extends JPanel {

    private final PreferenceDAO preferenceDAO = new PreferenceDAO();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                    "Preference ID",
                    "Choice Code",
                    "Order",
                    "College Name",
                    "Course",
                    "City"
            }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final JLabel countLabel = new JLabel("0 saved preferences");

    public ShortlistPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 243, 250));
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JLabel heading = new JLabel("My College Shortlist");
        heading.setFont(new Font("SansSerif", Font.BOLD, 27));
        heading.setForeground(new Color(103, 58, 183));

        JLabel subtitle = new JLabel(
                "Arrange your college-course preferences in priority order.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 110));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 7));
        titlePanel.setOpaque(false);
        titlePanel.add(heading);
        titlePanel.add(subtitle);

        JButton refreshButton = new JButton("Refresh");
        JButton upButton = new JButton("Move Up ↑");
        JButton downButton = new JButton("Move Down ↓");
        JButton removeButton = new JButton("Remove Selected");

        styleButton(refreshButton, new Color(103, 58, 183));
        styleButton(upButton, new Color(70, 130, 180));
        styleButton(downButton, new Color(70, 130, 180));
        styleButton(removeButton, new Color(190, 55, 75));

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        buttonPanel.add(upButton);
        buttonPanel.add(downButton);
        buttonPanel.add(removeButton);

        countLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        countLabel.setForeground(new Color(103, 58, 183));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(countLabel, BorderLayout.WEST);
        toolbar.add(buttonPanel, BorderLayout.EAST);

        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(false);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(235, 232, 240));
        table.setSelectionBackground(new Color(225, 212, 245));
        table.setSelectionForeground(Color.BLACK);

        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(103, 58, 183));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        // Hide internal database ID and choice code.
        table.removeColumn(table.getColumnModel().getColumn(1));
        table.removeColumn(table.getColumnModel().getColumn(0));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel top = new JPanel(new BorderLayout(0, 15));
        top.setOpaque(false);
        top.add(titlePanel, BorderLayout.NORTH);
        top.add(toolbar, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> loadPreferences());
        upButton.addActionListener(e -> moveSelected(-1));
        downButton.addActionListener(e -> moveSelected(1));
        removeButton.addActionListener(e -> removeSelected());

        loadPreferences();
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void loadPreferences() {
        try {
            model.setRowCount(0);

            List<String[]> preferences =
                    preferenceDAO.getSavedColleges();

            for (String[] preference : preferences) {
                model.addRow(preference);
            }

            countLabel.setText(
                    preferences.size() + " saved preferences");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load shortlist:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void moveSelected(int direction) {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this, "Select a college first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        long preferenceId = Long.parseLong(
                model.getValueAt(modelRow, 0).toString());

        try {
            preferenceDAO.moveCollege(preferenceId, direction);
            loadPreferences();

            int newRow = modelRow + direction;

            if (newRow >= 0 && newRow < table.getRowCount()) {
                table.setRowSelectionInterval(newRow, newRow);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not reorder preference:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelected() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a college from the shortlist first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        long preferenceId = Long.parseLong(
                model.getValueAt(modelRow, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Remove this saved preference?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            preferenceDAO.removeCollege(preferenceId);
            loadPreferences();

            JOptionPane.showMessageDialog(
                    this, "Preference removed successfully.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not remove preference:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
