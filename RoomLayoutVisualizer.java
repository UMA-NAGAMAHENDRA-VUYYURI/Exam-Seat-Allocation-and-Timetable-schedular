import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoomLayoutVisualizer extends JFrame {
    private List<Room> rooms;

    public RoomLayoutVisualizer(List<Room> rooms) {
        this.rooms = rooms;
        setTitle("Room Layout Visualizer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new GridLayout(rooms.size(), 1, 10, 10));
        roomPanel.setBackground(new Color(240, 240, 240));

        for (Room room : rooms) {
            JPanel roomDetail = new JPanel();
            roomDetail.setBackground(new Color(255, 255, 255));
            roomDetail.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel roomLabel = new JLabel("Room " + room.getRoomNumber(), JLabel.CENTER);
            roomLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            roomLabel.setPreferredSize(new Dimension(200, 50));

            JTextArea studentsArea = new JTextArea();
            studentsArea.setEditable(false);
            studentsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            for (StudentInfo student : room.getStudentsInRoom()) {
                studentsArea.append(student.getName() + " (" + student.getSeatNumber() + ")\n");
            }
            JScrollPane scrollPane = new JScrollPane(studentsArea);
            scrollPane.setPreferredSize(new Dimension(400, 200));

            roomDetail.setLayout(new BorderLayout());
            roomDetail.add(roomLabel, BorderLayout.NORTH);
            roomDetail.add(scrollPane, BorderLayout.CENTER);

            roomPanel.add(roomDetail);
        }

        add(roomPanel, BorderLayout.CENTER);
    }
}
