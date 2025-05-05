import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;


public class SwingUI {
    public static void main(String[] args) {
        SeatingLogic logic = new SeatingLogic();

        JFrame frame = new JFrame("Exam Seating System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 650);

        // Output area with a modern look
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(245, 248, 255));
        outputArea.setForeground(Color.DARK_GRAY);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(outputArea);

        // Left panel for buttons with modern look
        JPanel panel = new JPanel();
        panel.setBackground(new Color(20, 40, 80)); // dark navy
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Button style
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Color buttonColor = new Color(70, 130, 180); // steel blue
        Color textColor = Color.WHITE;

        JButton uploadBtn = createStyledButton("Upload students.txt files", buttonFont, buttonColor, textColor);
        JButton allocateBtn = createStyledButton("Allocate Rooms", buttonFont, buttonColor, textColor);
        JButton showAllBtn = createStyledButton("Show All Allocations", buttonFont, buttonColor, textColor);
        JButton scheduleBtn = createStyledButton("Timetable", buttonFont, buttonColor, textColor);
        JButton visualizeBtn = createStyledButton("Visualize Room Layout", buttonFont, buttonColor, textColor);
        JButton showRoomBtn = createStyledButton("Show Room Arrangement", buttonFont, buttonColor, textColor);

        panel.add(uploadBtn);
        panel.add(allocateBtn);
        panel.add(showAllBtn);
        panel.add(scheduleBtn);
        panel.add(visualizeBtn);
        panel.add(showRoomBtn);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.WEST);
        frame.add(scroll, BorderLayout.CENTER);
        frame.setVisible(true);

        // Button Actions
        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] files = chooser.getSelectedFiles();
                logic.loadMultipleStudentFiles(files, outputArea);
            }
        });

        allocateBtn.addActionListener(e -> {
            LinkedList<String> branchList = logic.getAvailableBranches();
            LinkedList<String> sectionList = logic.getAvailableSections();

            String[] branches = branchList.toArray(new String[0]);
            String[] sections = sectionList.toArray(new String[0]);

            LinkedList<String> selectedBranches = showMultiSelectDialog(frame, "Select Branches", branches);
            LinkedList<String> selectedSections = showMultiSelectDialog(frame, "Select Sections", sections);

            int seats = Integer.parseInt(JOptionPane.showInputDialog("Seats per room:"));
            int mix = Integer.parseInt(JOptionPane.showInputDialog("Max branches per room:"));

            logic.assignRooms(seats, mix, selectedBranches, selectedSections, outputArea);
        });

        showAllBtn.addActionListener(e -> {
            LinkedList<StudentInfo> students = logic.getAllStudents();
        
            JPanel allocPanel = new JPanel(new BorderLayout());
            allocPanel.setBorder(BorderFactory.createTitledBorder("All Student Allocations"));
        
            String[] headers = {"ID", "Name", "Year", "Branch", "Section", "Room"};
            Object[][] data = new Object[students.size()][headers.length];
        
            for (int i = 0; i < students.size(); i++) {
                StudentInfo s = students.get(i);
                if (s.getSeatNumber() > 0 && s.getRoom() != null && !s.getRoom().isEmpty()) {
                    data[i][0] = s.getId();
                    data[i][1] = s.getName();
                    data[i][2] = s.getYear();
                    data[i][3] = s.getBranch();
                    data[i][4] = s.getSection();
                    data[i][5] = s.getRoom();
                    
                }
            }
        
            JTable table = new JTable(data, headers);
            JScrollPane scrollPane = new JScrollPane(table);
            allocPanel.add(scrollPane, BorderLayout.CENTER);
        
            JButton printBtn = new JButton("Print Allocations");
            printBtn.setBackground(new Color(60, 120, 180));
            printBtn.setForeground(Color.WHITE);
            printBtn.setFocusPainted(false);
            printBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            allocPanel.add(printBtn, BorderLayout.SOUTH);
        
            printBtn.addActionListener(p -> {
                try {
                    boolean printed = table.print();
                    if (!printed) {
                        JOptionPane.showMessageDialog(null, "Printing cancelled by user.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Print failed: " + ex.getMessage());
                }
            });
        
            JFrame allocFrame = new JFrame("Allocated Students");
            allocFrame.setSize(800, 500);
            allocFrame.setLocationRelativeTo(null);
            allocFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            allocFrame.add(allocPanel);
            allocFrame.setVisible(true);
        });
        

        scheduleBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String date = JOptionPane.showInputDialog("Enter Start Date (dd-MM-yyyy):");

                LinkedList<String> holidays = new LinkedList<>();
                int holCount = Integer.parseInt(JOptionPane.showInputDialog("Number of holidays:"));
                for (int i = 0; i < holCount; i++) {
                    String h = JOptionPane.showInputDialog("Enter holiday date (dd-MM-yyyy):");
                    holidays.add(h);
                }

                LinkedList<DaySchedule> timetable = ExamScheduler.generateSchedule(file, date, holidays);
                LinkedList<String> branches = new LinkedList<>();

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] split = line.split(":");
                        branches.add(split[0].trim());
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error reading file for branch headers.");
                    return;
                }

                String[] columns = new String[branches.size() + 1];
                columns[0] = "Date/Day";
                for (int i = 0; i < branches.size(); i++) columns[i + 1] = branches.get(i);

                Object[][] tableData = new Object[timetable.size()][columns.length];
                for (int i = 0; i < timetable.size(); i++) {
                    DaySchedule d = timetable.get(i);
                    tableData[i][0] = d.date + " " + d.dayName;
                    for (int j = 0; j < branches.size(); j++) tableData[i][j + 1] = "";
                    for (int j = 0; j < d.branches.size(); j++) {
                        int index = branches.indexOf(d.branches.get(j));
                        if (index != -1) tableData[i][index + 1] = d.subjects.get(j);
                    }
                }

                JTable table = new JTable(tableData, columns) {
                    public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                        Component c = super.prepareRenderer(renderer, row, column);
                        if (column > 0 && getValueAt(row, column) != null && !getValueAt(row, column).toString().isEmpty()) {
                            c.setBackground(new Color(170, 220, 255)); // soft blue
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                        return c;
                    }
                };

                table.setRowHeight(30);
                table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                JScrollPane pane = new JScrollPane(table);
                pane.setPreferredSize(new Dimension(900, 400));
                JFrame ttFrame = new JFrame("Exam Timetable");
                ttFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ttFrame.add(pane);
                ttFrame.pack();
                ttFrame.setVisible(true);
            }
        });
        showRoomBtn.addActionListener(e -> {
            String roomNo = JOptionPane.showInputDialog("Enter Room Number:");
            LinkedList<Room> rooms = logic.getRooms();
            for (Room room : rooms) {
                if (room.getRoomNumber().equalsIgnoreCase("Room " + roomNo)) {
                    LinkedList<StudentInfo> list = room.getStudentsInRoom();
                    Object[][] data = new Object[list.size()][6];
                    for (int i = 0; i < list.size(); i++) {
                        StudentInfo s = list.get(i);
                        data[i][0] = s.getSeatNumber();
                        data[i][1] = s.getId();
                        data[i][2] = s.getName();
                        data[i][3] = s.getYear();
                        data[i][4] = s.getBranch();
                        data[i][5] = s.getSection();
                    }
        
                    JTable table = new JTable(data, new String[]{"Seat", "ID", "Name", "Year", "Branch", "Section"});
                    table.setRowHeight(25);
        
                    JScrollPane pane = new JScrollPane(table);
                    pane.setPreferredSize(new Dimension(700, 300));
        
                    JButton printBtn = new JButton("Print Room Arrangement");
                    printBtn.setBackground(new Color(60, 120, 180));
                    printBtn.setForeground(Color.WHITE);
                    printBtn.setFocusPainted(false);
                    printBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        
                    printBtn.addActionListener(p -> {
                        try {
                            boolean printed = table.print();
                            if (!printed) {
                                JOptionPane.showMessageDialog(null, "Printing cancelled by user.");
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Print failed: " + ex.getMessage());
                        }
                    });
        
                    JPanel roomPanel = new JPanel(new BorderLayout());
                    roomPanel.add(pane, BorderLayout.CENTER);
                    roomPanel.add(printBtn, BorderLayout.SOUTH);
        
                    JFrame roomFrame = new JFrame("Room " + roomNo + " Arrangement");
                    roomFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    roomFrame.add(roomPanel);
                    roomFrame.pack();
                    roomFrame.setVisible(true);
                    return;
                }
            }
            outputArea.append("Room not found.\n");
        });
        
        
        visualizeBtn.addActionListener(e -> {
            String roomNo = JOptionPane.showInputDialog("Enter Room Number:");
            int rows = Integer.parseInt(JOptionPane.showInputDialog("Enter number of rows:"));
            int cols = Integer.parseInt(JOptionPane.showInputDialog("Enter number of columns:"));

            Room selectedRoom = null;
            for (Room room : logic.getRooms()) {
                if (room.getRoomNumber().equalsIgnoreCase("Room " + roomNo)) {
                    selectedRoom = room;
                    break;
                }
            }

            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(null, "Room not found.");
                return;
            }

            LinkedList<StudentInfo> list = selectedRoom.getStudentsInRoom();
            JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 10, 10));
            gridPanel.setBackground(new Color(240, 240, 255));

            int index = 0;
            for (int i = 0; i < rows * cols; i++) {
                JLabel label;
                if (index < list.size()) {
                    label = new JLabel(list.get(index).getId(), SwingConstants.CENTER);
                    label.setOpaque(true);
                    label.setBackground(new Color(70, 130, 180));
                    label.setForeground(Color.WHITE);
                    index++;
                } else {
                    label = new JLabel("", SwingConstants.CENTER);
                    label.setOpaque(true);
                    label.setBackground(Color.LIGHT_GRAY);
                }
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridPanel.add(label);
            }

            JFrame layoutFrame = new JFrame("Room " + roomNo + " Layout");
            layoutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            layoutFrame.add(new JScrollPane(gridPanel));
            layoutFrame.setSize(600, 400);
            layoutFrame.setVisible(true);
        });
    }

    // Helper: Create consistent button styling
    private static JButton createStyledButton(String text, Font font, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return button;
    }

    public static LinkedList<String> showMultiSelectDialog(JFrame parent, String title, String[] options) {
        JList<String> list = new JList<>(options);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(200, 150));

        int result = JOptionPane.showConfirmDialog(parent, scrollPane, title, JOptionPane.OK_CANCEL_OPTION);
        LinkedList<String> selected = new LinkedList<>();
        if (result == JOptionPane.OK_OPTION) {
            for (String value : list.getSelectedValuesList()) {
                selected.add(value);
            }
        }
        return selected;
    }
}