
package com.cet.finder.ui;

import com.cet.finder.util.CsvImportTask;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;

public class CsvToolsPanel extends JPanel {

 private final JTextField name = new JTextField(12);
 private final JTextField code = new JTextField(10);
 private final JTextField city = new JTextField(10);
 private final JTextField course = new JTextField(12);
 private final JTextField category = new JTextField(8);
 private final JTextField round = new JTextField("0", 3);
 private final JTextField year = new JTextField("0", 5);

 public CsvToolsPanel() {
  setLayout(new BorderLayout(12, 12));
  setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

  JLabel title = new JLabel("CSV Import & Export");
  title.setFont(new Font("SansSerif", Font.BOLD, 26));
  title.setForeground(new Color(103, 58, 183));

  JLabel note = new JLabel(
          "Cutoff CSV header: choice_code,year,cap_round,category,merit_rank,merit_score,percentile"
  );

  JPanel north = new JPanel(new GridLayout(2, 1, 0, 8));
  north.add(title);
  north.add(note);
  add(north, BorderLayout.NORTH);

  JPanel center = new JPanel();
  center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

  JButton imp = new JButton("Import cutoff CSV...");
  JLabel example = new JLabel(
          "Imports cutoff records. Invalid files are rolled back."
  );

  center.add(imp);
  center.add(Box.createVerticalStrut(8));
  center.add(example);
  center.add(Box.createVerticalStrut(28));

  JLabel exportTitle = new JLabel("Export filtered search results");
  exportTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
  center.add(exportTitle);

  JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
  addFilter(filters, "College name", name);
  addFilter(filters, "Institute code", code);
  addFilter(filters, "City", city);
  addFilter(filters, "Course", course);
  addFilter(filters, "Category", category);
  addFilter(filters, "CAP round (0=all)", round);
  addFilter(filters, "Year (0=all)", year);

  center.add(filters);

  JButton export = new JButton("Export filtered results to CSV...");
  center.add(Box.createVerticalStrut(8));
  center.add(export);

  add(center, BorderLayout.CENTER);

  imp.addActionListener(e -> {
   JFileChooser chooser = new JFileChooser();
   chooser.setDialogTitle("Select cutoff CSV file");

   if (chooser.showOpenDialog(this)
           != JFileChooser.APPROVE_OPTION) {
    return;
   }

   Path path = chooser.getSelectedFile().toPath();

   try {
    int count = CsvImportTask.importCutoffs(path);

    JOptionPane.showMessageDialog(
            this,
            count + " cutoff rows imported successfully.",
            "Import Complete",
            JOptionPane.INFORMATION_MESSAGE
    );

   } catch (Exception ex) {
    showError("CSV import failed", ex);
   }
  });

  export.addActionListener(e -> {
   JFileChooser chooser = new JFileChooser();
   chooser.setDialogTitle("Export college results");
   chooser.setSelectedFile(
           new File("filtered_college_results.csv")
   );

   if (chooser.showSaveDialog(this)
           != JFileChooser.APPROVE_OPTION) {
    return;
   }

   try {
    int capRound = Integer.parseInt(round.getText().trim());
    int selectedYear = Integer.parseInt(year.getText().trim());

    int count = CsvImportTask.exportSearch(
            chooser.getSelectedFile().toPath(),
            name.getText().trim(),
            code.getText().trim(),
            city.getText().trim(),
            course.getText().trim(),
            category.getText().trim(),
            capRound,
            selectedYear
    );

    JOptionPane.showMessageDialog(
            this,
            count + " rows exported successfully.",
            "Export Complete",
            JOptionPane.INFORMATION_MESSAGE
    );

   } catch (NumberFormatException ex) {
    JOptionPane.showMessageDialog(
            this,
            "CAP round and year must be valid integers. Use 0 for all.",
            "Invalid Filter",
            JOptionPane.WARNING_MESSAGE
    );

   } catch (Exception ex) {
    showError("CSV export failed", ex);
   }
  });
 }

 private void addFilter(
         JPanel panel, String label, JTextField field) {

  JPanel wrapper = new JPanel(new BorderLayout(3, 3));
  wrapper.add(new JLabel(label), BorderLayout.NORTH);
  wrapper.add(field, BorderLayout.CENTER);
  panel.add(wrapper);
 }

 private void showError(String title, Exception ex) {
  String message = ex.getMessage();

  if (message == null || message.isBlank()) {
   message = ex.getClass().getSimpleName();
  }

  JOptionPane.showMessageDialog(
          this,
          message,
          title,
          JOptionPane.ERROR_MESSAGE
  );
 }
}
