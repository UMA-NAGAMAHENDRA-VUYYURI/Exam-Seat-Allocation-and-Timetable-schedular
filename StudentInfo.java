public class StudentInfo {
    private String id;
    private String name;
    private String year;
    private String branch;
    private String section;
    private String room;
    private int seatNumber;

    public StudentInfo(String id, String name, String year, String branch, String section) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.branch = branch;
        this.section = section;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getYear() { return year; }
    public String getBranch() { return branch; }
    public String getSection() { return section; }
    public String getRoom() { return room; }
    public int getSeatNumber() { return seatNumber; }

    public void setRoom(String room) { this.room = room; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    @Override
    public String toString() {
        return id + "," + name + "," + year + "," + branch + "," + section + "," + room + "," + seatNumber;
    }
}