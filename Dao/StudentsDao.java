package home.Task_23.Dao;

import home.Task_23.Const;
import home.Task_23.models.Group;
import home.Task_23.models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentsDao {

    private Connection connection;

    private final String SQL_INSERT_STUDENT = "INSERT INTO students(name, group_id) VALUES(?, ?)";
    private final String SQL_SELECT_ALL_STUDENTS = "SELECT id, name, group_id FROM students";
    private final String SQL_SELECT_STUDENT_BY_NAME = "SELECT id, name, group_id FROM students WHERE name = ?";
    private final String SQL_SELECT_STUDENT_BY_ID = "SELECT id, name, group_id FROM students WHERE id = ?";
    private final String SQL_DELETE_ALL_STUDENTS = "DELETE FROM students";
    private final String SQL_DELETE_STUDENT_BY_ID = "DELETE FROM students WHERE id=?";
    private final String SQL_DELETE_STUDENT_BY_NAME = "DELETE FROM students WHERE name=?";
    private final String SQL_SELECT_STUDENT_BY_GROUP_ID = "SELECT id, name, group_id FROM students WHERE group_id = ?";

    private final String SQL_INSERT_GROUP = "INSERT INTO groups(name) VALUES(?)";
    private final String SQL_DELETE_ALL_GROUPS = "DELETE FROM groups";
    private final String SQL_SELECT_ALL_GROUPS = "SELECT id, name FROM groups";
    private final String SQL_SELECT_GROUP_BY_NAME = "SELECT id, name FROM groups WHERE name = ?";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public StudentsDao() throws SQLException {
        connection = DriverManager.getConnection(Const.JDBC_URL, Const.USER, Const.PASSWORD);
        maybeCreateGroupsTable();
        maybeCreateStudentsTable();
    }

    private void maybeCreateGroupsTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String request = "CREATE TABLE IF NOT EXISTS groups \n" +
                    "(id SERIAL, \n" +
                    "name varchar(100),\n" +
                    "PRIMARY KEY (id));";
            statement.execute(request);
        }
    }

    private void maybeCreateStudentsTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String request = "CREATE TABLE IF NOT EXISTS students \n" +
                    "(id SERIAL, \n" +
                    "name varchar(100),\n" +
                    "group_id int, PRIMARY KEY (id), \n" +
                    "FOREIGN KEY (group_id) REFERENCES groups(id));";
            statement.execute(request);
        }
    }

    public void clean() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL_DELETE_ALL_STUDENTS);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL_DELETE_ALL_GROUPS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeStudentById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_DELETE_STUDENT_BY_ID);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeStudentByName(String name) {
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_DELETE_STUDENT_BY_NAME);
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addGroup(Group group) {
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_GROUP);
            statement.setString(1, group.getName());
            statement.execute();
            for (Student student : group.getStudents()) {
                student.setGroup_id(getGroupByName(group.getName()).getId());
                addStudent(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStudent(Student student) {
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_STUDENT);
            statement.setString(1, student.getName());
            statement.setInt(2, student.getGroup_id());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Group getGroupByName(String group_name) {
        List<Student> students = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_GROUP_BY_NAME);
            statement.setString(1, group_name);
            ResultSet resultSet = statement.executeQuery();
            String name = "";
            int id = 0;
            while (resultSet.next()) {
                id = resultSet.getInt(1);
                name = resultSet.getString(2);
                students = getStudentsByGroupID(id);
            }
            return new Group(id, name, students);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> getStudentsByGroupID(int group_id) {
        List<Student> students = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_STUDENT_BY_GROUP_ID);
            statement.setInt(1, group_id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int group = resultSet.getInt(3);
                students.add(new Student(id, name, group));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public Student getStudent(int id) {
        Student student = null;
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_STUDENT_BY_ID);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            int userID = 0;
            while (resultSet.next() && id != userID) {
                userID = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int group = resultSet.getInt(3);
                student = new Student(userID, name, group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public Student getStudent(String name) {
        Student student = null;
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_STUDENT_BY_NAME);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            String studentName = "";
            while (resultSet.next() && !name.equals(studentName)) {
                int id = resultSet.getInt(1);
                studentName = resultSet.getString(2);
                int group = resultSet.getInt(3);
                student = new Student(id, name, group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_GROUPS);
            while (resultSet.next()) {
                String name = resultSet.getString(2);
                groups.add(getGroupByName(name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_STUDENTS);
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int group = resultSet.getInt(3);
                students.add(new Student(id, name, group));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}
