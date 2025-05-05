import java.util.LinkedList;

public class Room {
    private String roomNumber;
    private LinkedList<StudentInfo> studentsInRoom = new LinkedList<>();
    private int capacity;

    public Room(String roomNumber, int capacity) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
    }

    public String getRoomNumber() { return roomNumber; }

    public boolean addStudent(StudentInfo student) {
        if (studentsInRoom.size() < capacity) {
            student.setRoom(roomNumber);
            student.setSeatNumber(studentsInRoom.size() + 1);
            studentsInRoom.add(student);
            return true;
        }
        return false;
    }

    public LinkedList<StudentInfo> getStudentsInRoom() {
        return studentsInRoom;
    }
}