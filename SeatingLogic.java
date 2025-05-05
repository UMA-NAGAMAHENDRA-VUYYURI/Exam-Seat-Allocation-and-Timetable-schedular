
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.Dimension;

public class SeatingLogic {
    private LinkedList<StudentInfo> students = new LinkedList<>();
    private LinkedList<Room> rooms = new LinkedList<>();
    private LinkedList<String> selectedBranches;
    private LinkedList<String> selectedSections;

    public void loadMultipleStudentFiles(File[] files, JTextArea outputArea) {
        students.clear();
        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        students.add(new StudentInfo(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim()));
                    }
                }
            } catch (IOException e) {
                outputArea.append("Error reading file: " + file.getName() + "\n");
            }
        }
        outputArea.append("All student files loaded.\n");
    }

    public void assignRooms(int seatsPerRoom, int mixCount, LinkedList<String> branches, LinkedList<String> sections, JTextArea outputArea) {
        rooms.clear();
        selectedBranches = branches;
        selectedSections = sections;
    
        // Step 1: Group students by branch
        LinkedList<LinkedList<StudentInfo>> groupedByBranch = new LinkedList<>();
    
        for (String branch : branches) {
            LinkedList<StudentInfo> branchGroup = new LinkedList<>();
            for (StudentInfo s : students) {
                if (s.getBranch().equals(branch) && sections.contains(s.getSection())) {
                    branchGroup.add(s);
                }
            }
            if (!branchGroup.isEmpty()) {
                Collections.shuffle(branchGroup); // Shuffle within each branch group
                groupedByBranch.add(branchGroup);
            }
        }
    
        if (groupedByBranch.isEmpty()) {
            outputArea.append("No matching students found.\n");
            return;
        }
    
        int roomCount = 1;
    
        while (!groupedByBranch.isEmpty()) {
            Room room = new Room("Room " + roomCount, seatsPerRoom);
            LinkedList<LinkedList<StudentInfo>> currentMix = new LinkedList<>();
            int used = 0;
    
            // Select up to mixCount branch groups
            Iterator<LinkedList<StudentInfo>> it = groupedByBranch.iterator();
            while (it.hasNext() && used < mixCount) {
                LinkedList<StudentInfo> group = it.next();
                currentMix.add(group);
                used++;
            }
    
            // Round-robin fill the room
            boolean added = true;
            while (room.getStudentsInRoom().size() < seatsPerRoom && added) {
                added = false;
                for (LinkedList<StudentInfo> branchList : currentMix) {
                    if (!branchList.isEmpty() && room.getStudentsInRoom().size() < seatsPerRoom) {
                        StudentInfo student = branchList.removeFirst();
                        room.addStudent(student);
                        added = true;
                    }
                }
            }
    
            // Remove empty branch groups from master list
            groupedByBranch.removeIf(LinkedList::isEmpty);
    
            rooms.add(room);
            roomCount++;
        }
    
        outputArea.append("Rooms allocated using controlled branch mixing.\n");
    }
    
    public LinkedList<Room> getRooms() {
        return rooms;
    }

    public LinkedList<StudentInfo> getAllStudents() {
        LinkedList<StudentInfo> allocated = new LinkedList<>();
        for (StudentInfo s : students) {
            if (s.getSeatNumber() > 0 && s.getRoom() != null) {
                allocated.add(s);
            }
        }
        return allocated;
    }

    public LinkedList<String> getAvailableBranches() {
        LinkedList<String> branches = new LinkedList<>();
        for (StudentInfo s : students) {
            if (!branches.contains(s.getBranch())) {
                branches.add(s.getBranch());
            }
        }
        return branches;
    }

    public LinkedList<String> getAvailableSections() {
        LinkedList<String> sections = new LinkedList<>();
        for (StudentInfo s : students) {
            if (!sections.contains(s.getSection())) {
                sections.add(s.getSection());
            }
        }
        return sections;
    }
}


