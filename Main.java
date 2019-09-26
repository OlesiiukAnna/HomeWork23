package home.Task_23;

import home.Task_23.Dao.StudentsDao;
import home.Task_23.models.Group;
import home.Task_23.models.Student;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Group javaGroup = createJavaGroup();
        Group cppGroup = createCppGroup();

        try {
            StudentsDao studentsDao = new StudentsDao();

            studentsDao.addGroup(javaGroup);
            studentsDao.addGroup(cppGroup);

            Group testGroupJava = studentsDao.getGroupByName("Java");
            System.out.println("Java group: " + testGroupJava);
            Group testGroupCpp = studentsDao.getGroupByName("CPP");
            System.out.println("CPP group: " + testGroupCpp);

            System.out.println("");

            studentsDao.addStudent(new Student("Cora", 1));
            System.out.println("All groups and added new student Cora:");
            List<Group> groups = studentsDao.getAllGroups();
            for (Group group : groups) {
                System.out.println(group);
            }

            System.out.println("");

            System.out.println("Get student by id(1) : " + studentsDao.getStudent(1));
            System.out.println("Get student by name(Mary) : " + studentsDao.getStudent("Mary"));
            System.out.println("Get students by group id(1) : " + studentsDao.getStudentsByGroupID(1));

            System.out.println("");

            List<Student> students = studentsDao.getAllStudents();
            System.out.println("All students: " + students);

            System.out.println("");
            System.out.println("Java group: " + studentsDao.getGroupByName("Java"));
            studentsDao.removeStudentById(1);
            studentsDao.removeStudentByName("Cora");

            System.out.println("Java group when removed student by id(1) and by name(Cora) : " +
                    studentsDao.getGroupByName("Java"));

            System.out.println("");

            studentsDao.clean();
            System.out.println("clean(): " + studentsDao.getAllGroups());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Group createJavaGroup() {
        Group group = new Group("Java");
        group.addUser(new Student("Mary"));
        group.addUser(new Student("Sam"));
        group.addUser(new Student("Carl"));
        return group;
    }

    private static Group createCppGroup() {
        Group group = new Group("CPP");
        group.addUser(new Student("Alex"));
        group.addUser(new Student("Tom"));
        group.addUser(new Student("Mark"));
        return group;
    }
}
