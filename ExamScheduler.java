import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

class DaySchedule {
    String date;
    String dayName;
    LinkedList<String> branches = new LinkedList<>();
    LinkedList<String> subjects = new LinkedList<>();

    public DaySchedule(String date, String dayName) {
        this.date = date;
        this.dayName = dayName;
    }
}

public class ExamScheduler {
    public static LinkedList<DaySchedule> generateSchedule(File subjectFile, String startDate, LinkedList<String> holidays) {
        LinkedList<DaySchedule> schedule = new LinkedList<>();
        LinkedList<String> branchList = new LinkedList<>();
        LinkedList<LinkedList<String>> allSubjects = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(subjectFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(":");
                String branch = split[0].trim();
                branchList.add(branch);

                LinkedList<String> subjectList = new LinkedList<>();
                StringTokenizer tokenizer = new StringTokenizer(split[1], ",");
                while (tokenizer.hasMoreTokens()) {
                    subjectList.add(tokenizer.nextToken().trim());
                }
                allSubjects.add(subjectList);
            }
        } catch (IOException e) {
            System.out.println("Error reading subjects file");
            return schedule;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(startDate));

            while (true) {
                boolean hasPending = false;
                for (LinkedList<String> subjects : allSubjects) {
                    if (!subjects.isEmpty()) {
                        hasPending = true;
                        break;
                    }
                }
                if (!hasPending) break;

                String currentDate = sdf.format(cal.getTime());
                String day = new SimpleDateFormat("EEEE").format(cal.getTime());

                if (!day.equals("Saturday") && !day.equals("Sunday") && !holidays.contains(currentDate)) {
                    DaySchedule ds = new DaySchedule(currentDate, day);
                    LinkedList<String> usedSubjects = new LinkedList<>();

                    for (int i = 0; i < allSubjects.size(); i++) {
                        LinkedList<String> subjects = allSubjects.get(i);
                        String selected = null;
                        for (String subj : subjects) {
                            if (!usedSubjects.contains(subj)) {
                                selected = subj;
                                break;
                            }
                        }
                        if (selected != null) {
                            subjects.remove(selected);
                            usedSubjects.add(selected);
                            ds.branches.add(branchList.get(i));
                            ds.subjects.add(selected);
                        }
                    }
                    schedule.add(ds);
                }
                cal.add(Calendar.DATE, 1);
            }
        } catch (Exception e) {
            System.out.println("Error scheduling exams");
        }
        return schedule;
    }
}
