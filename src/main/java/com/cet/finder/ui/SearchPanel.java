
package com.cet.finder.ui;

import com.cet.finder.dao.CollegeDAO;
import com.cet.finder.dao.PreferenceDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SearchPanel extends JPanel {

    private final Color purple = new Color(111, 55, 168);
    private final Color lightPurple = new Color(243, 238, 249);
    private final Color background = new Color(245, 246, 248);
    private final Color textColor = new Color(45, 43, 52);
    private final Color muted = new Color(112, 112, 124);
    private final Color borderColor = new Color(222, 224, 230);

    private final JTextField collegeField = new JTextField(13);
    private final JTextField instituteCodeField = new JTextField(8);
    private final JTextField cityField = new JTextField(10);
    private final JTextField courseField = new JTextField(13);

    private final JComboBox<String> categoryBox = new JComboBox<>(
            new String[]{
                    "All Categories", "AI", "DEFOBCO", "GNT1H", "GNT1O",
                    "GNT2H", "GNT2O", "GNT3H", "GNT3O", "GOBCH", "GOBCO",
                    "GOPENH", "GOPENO", "GSCH", "GSCO", "GSEBCH", "GSEBCO",
                    "GSTH", "GSTO", "GVJH", "LNT1H", "LNT2H", "LNT3H",
                    "LNT3O", "LOBCH", "LOBCO", "LOPENH", "LOPENO", "LSCH",
                    "LSEBCH", "LSEBCO", "LSTH", "LSTO", "LVJH", "LVJO",
                    "MI", "ORPHAN", "PHCH", "PHCOBCH"
            });

    private final JComboBox<String> roundBox = new JComboBox<>(
            new String[]{"All Rounds", "1", "2", "3", "4"});

    private final JButton searchButton = new JButton("Search Colleges");
    private final JButton saveButton = new JButton("Add Selected to Shortlist");
    private final JLabel statusLabel = new JLabel("Search by city or course");

    // Columns 0 and 1 are internal identifiers.
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                    "Choice Code", "Institute Code", "College Name", "City", "Course",
                    "Year", "CAP Round", "Category", "Merit Rank", "Percentile"
            }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public SearchPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(background);
        setBorder(new EmptyBorder(24, 26, 20, 26));

        JLabel heading = new JLabel("MHT-CET College Finder");
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));
        heading.setForeground(purple);

        JLabel subtitle = new JLabel(
                "Find engineering colleges and build your CAP preference shortlist.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(muted);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(heading);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        JPanel searchCard = new JPanel(new GridBagLayout());
        searchCard.setBackground(Color.WHITE);
        searchCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(10, 10, 10, 10)));

        styleField(cityField);
        styleField(courseField);
        styleCombo(categoryBox);
        styleCombo(roundBox);
        styleButton(searchButton, purple);
        styleButton(saveButton, new Color(49, 133, 94));

        addSearchItem(searchCard, 0, "COLLEGE NAME", collegeField);
        addSearchItem(searchCard, 2, "INSTITUTE CODE", instituteCodeField);
        addSearchItem(searchCard, 4, "CITY", cityField);
        addSearchItem(searchCard, 6, "COURSE", courseField);
        addSearchItem(searchCard, 8, "CATEGORY", categoryBox);
        addSearchItem(searchCard, 10, "CAP ROUND", roundBox);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 12;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        searchCard.add(searchButton, gbc);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(header);
        top.add(Box.createVerticalStrut(18));
        top.add(searchCard);

        // Table styling
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setForeground(textColor);
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(239, 234, 245));
        table.setSelectionBackground(new Color(235, 224, 248));
        table.setSelectionForeground(purple);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        table.getTableHeader().setBackground(lightPurple);
        table.getTableHeader().setForeground(purple);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setReorderingAllowed(false);

        // Hide internal identifiers while keeping them in the model.
        table.removeColumn(table.getColumnModel().getColumn(1));
        table.removeColumn(table.getColumnModel().getColumn(0));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        statusLabel.setForeground(muted);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setOpaque(false);
        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(saveButton, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchColleges());
        saveButton.addActionListener(e -> saveSelectedColleges());

        collegeField.addActionListener(e -> searchColleges());
        instituteCodeField.addActionListener(e -> searchColleges());
        cityField.addActionListener(e -> searchColleges());
        courseField.addActionListener(e -> searchColleges());
        categoryBox.addActionListener(e -> searchColleges());
        roundBox.addActionListener(e -> searchColleges());
    }

    private void addSearchItem(
            JPanel panel, int x, String labelText, JComponent field) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel label = new JLabel(labelText);
        label.setForeground(muted);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));

        gbc.gridx = x;
        gbc.gridy = 0;
        panel.add(label, gbc);

        gbc.gridx = x + 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void styleField(JTextField field) {
        field.setBackground(Color.WHITE);
        field.setForeground(textColor);
        field.setCaretColor(purple);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(8, 8, 8, 8)));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(Color.WHITE);
        combo.setForeground(textColor);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 13, 11, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void searchColleges() {
        String collegeName = collegeField.getText().trim();
        String instituteCode = instituteCodeField.getText().trim();
        String city = cityField.getText().trim();
        String course = courseField.getText().trim();

        String selectedCategory = (String) categoryBox.getSelectedItem();
        String category = "All Categories".equals(selectedCategory)
                ? "" : selectedCategory;

        String selectedRound = (String) roundBox.getSelectedItem();
        int capRound = "All Rounds".equals(selectedRound)
                ? 0 : Integer.parseInt(selectedRound);

        searchButton.setEnabled(false);
        statusLabel.setText("Searching...");

        try {
            List<String[]> results = new CollegeDAO().searchColleges(
                    collegeName, instituteCode, city, course, category, capRound, 0);

            model.setRowCount(0);

            for (String[] result : results) {
                model.addRow(result);
            }

            statusLabel.setText(
                    results.size() + " college-course/cutoff rows found. "
                            + "Use Ctrl-click or Shift-click to select multiple rows.");

        } catch (SQLException ex) {
            statusLabel.setText("Search failed");
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to retrieve results:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            searchButton.setEnabled(true);
        }
    }

    private void saveSelectedColleges() {
        int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select one or more college-course rows first.",
                    "No Selection",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        PreferenceDAO dao = new PreferenceDAO();
        int saved = 0;
        int skipped = 0;

        try {
            List<String[]> existing = dao.getSavedColleges();
            java.util.Set<String> existingCodes = new java.util.HashSet<>();

            int maxOrder = 0;
            for (String[] item : existing) {
                existingCodes.add(item[1]); // choice_code
                try {
                    maxOrder = Math.max(maxOrder, Integer.parseInt(item[2]));
                } catch (NumberFormatException ignored) {
                    // Keep the highest valid order.
                }
            }

            for (int viewRow : selectedRows) {
                int row = table.convertRowIndexToModel(viewRow);
                String choiceCode = model.getValueAt(row, 0).toString();

                if (existingCodes.contains(choiceCode)) {
                    skipped++;
                    continue;
                }

                maxOrder++;
                dao.saveCollege(choiceCode, maxOrder);
                existingCodes.add(choiceCode);
                saved++;
            }

            JOptionPane.showMessageDialog(
                    this,
                    saved + " option(s) added to your shortlist."
                            + (skipped > 0
                            ? "\n" + skipped + " option(s) were already saved."
                            : "")
                            + "\nOpen the My Shortlist tab to view them.",
                    "Shortlist Updated",
                    JOptionPane.INFORMATION_MESSAGE);

            statusLabel.setText(
                    saved + " saved; " + skipped + " already existed.");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not save selected options:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
