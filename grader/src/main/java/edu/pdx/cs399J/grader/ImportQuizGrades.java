package edu.pdx.cs399J.grader;

import au.com.bytecode.opencsv.CSVReader;
import edu.pdx.cs399J.ParserException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Imports quiz grades into the grade book from a CSV file exported from BlackBoard
 *
 * @author David Whitlock
 * @since Summer 2010
 */
public class ImportQuizGrades
{
    private static final int LAST_NAME_INDEX = 0;
    private static final int FIRST_NAME_INDEX = 1;
    private static final int USER_ID_INDEX = 2;
    private static final int ROLE_INDEX = 3;

    private static void usage(String message) {
        PrintStream err = System.err;
        err.println("*** " + message);
        err.println("\n");
        err.println("usage: java ImportQuizGrades gradebook.xml blackboardexport.csv");
        err.println("\n");

        System.exit(1);
    }

    public static void main(String[] args) throws IOException, ParserException
    {
        String gradeBookFileName = null;
        String exportFileName = null;

        for (String arg : args) {
            if (gradeBookFileName == null) {
                gradeBookFileName = arg;

            } else if (exportFileName == null) {
                exportFileName = arg;

            } else {
                usage( "Extraneous command line argument: " + arg );
            }
        }

        if (gradeBookFileName == null) {
            usage( "Missing grade book file name" );

        } else if (exportFileName == null) {
            usage("Missing export file name");
        }

        File gradeBookFile = new File(gradeBookFileName);
        if (!gradeBookFile.exists()) {
            usage("Non-existent grade book file " + gradeBookFile);
        }

        File exportFile = new File(exportFileName);
        if (!exportFile.exists()) {
            usage("Non-existent BlackBoard export file: " + exportFile);
        }

        BlackBoardClass bb = parseBlackBoardExport(exportFile);
        // dumpClass( bb );
        GradeBook book = new XmlGradeBookParser( gradeBookFile).parse();

        Map<String, Assignment> quizzes = findKnownQuizzes(bb, book);
        // dumpKnownQuizzes( quizzes );

        for (BlackBoardStudent bbStudent : bb.getStudents()) {
            Student student = findStudent(bbStudent, book);
            if (student == null) {
                System.out.println("Couldn't find student " + bbStudent);
                continue;
            }

            for (String quizName : quizzes.keySet()) {
                Double score = bbStudent.getGrade( quizName );
                if (score != null) {
                    Assignment assignment = quizzes.get( quizName );
                    Grade grade = student.getGrade( assignment.getName() );
                    if (grade != null) {
                        if (grade.getScore() == score.doubleValue()) {
                            // We've already recorded this grade
                            continue;

                        } else {
                            System.out.println("*** Changing existing grade for " + assignment.getName() + " for " + student.getFullName() + " from " + grade.getScore() + " to " + score);
                        }

                    } else {
                        grade = new Grade( assignment.getName(), score );
                    }
                    student.setGrade( assignment.getName(), grade );
                }
            }
        }

        if (book.isDirty()) {
            new XmlDumper(gradeBookFile).dump( book );
        }
    }

    private static void dumpKnownQuizzes( Map<String, Assignment> quizzes )
    {
        PrintStream out = System.out;
        out.println("Found " + quizzes.size() + " known quizzes");
        int quizCount = 1;
        for (String quizName : quizzes.keySet()) {
            out.println("  " + quizCount + ") " + quizName + " -> " + quizzes.get(quizName));
            quizCount++;
        }
    }

    private static Map<String, Assignment> findKnownQuizzes( BlackBoardClass bb, GradeBook book )
    {
        Map<String, Assignment> knownQuizzes = new HashMap<String, Assignment>();

        NEXT_QUIZ:
        for (String quizName : bb.getQuizNames()) {
            for (String assignmentName : book.getAssignmentNames()) {
                Assignment assignment = book.getAssignment( assignmentName );
                if (quizName.equals( assignment.getDescription() )) {
                    if (assignment.getType() != Assignment.QUIZ) {
                        throw new IllegalStateException( "Assignment " + assignment + " should be a QUIZ");
                    }

                    knownQuizzes.put( quizName, assignment );
                    continue NEXT_QUIZ;
                }
            }

            System.out.println("No assignment for BlackBoard quiz \"" + quizName + "\"");
        }

        return knownQuizzes;
    }

    private static Student findStudent( BlackBoardStudent bbStudent, GradeBook book )
    {
        for (String id : book.getStudentIds()) {
            Student student = book.getStudent( id );
            if (student.getId().equals( bbStudent.getId() )) {
                return student;
            }

            if (student.getFirstName().equals( bbStudent.getFirstName() )) {
                if (student.getLastName().equals( bbStudent.getLastName() )) {
                    return student;
                }
            }
        }

        return null;
    }

    private static void dumpClass( BlackBoardClass bb )
    {
        PrintStream out = System.out;
        out.println("Parsed Black Board Export with " + bb.getStudents().size() + " students and " + bb.getQuizNames().size() + " quizzes");
        int studentCount = 1;
        for ( BlackBoardStudent student : bb.getStudents()) {
            out.println( "Student " + studentCount + ") " + student );
            out.flush();

            int quizCount = 1;
            for (String quizName : bb.getQuizNames()) {
                Double grade = student.getGrade(quizName);
                out.println("  Quiz " + quizCount + ") " + quizName + ": " + grade);
                out.flush();
                quizCount++;
            }

            studentCount++;
        }
    }

    private static BlackBoardClass parseBlackBoardExport( File exportFile ) throws IOException
    {
        CSVReader reader = new CSVReader( new FileReader( exportFile) );
        String[] firstLine = reader.readNext();

        checkFirstLine( "Last Name", LAST_NAME_INDEX, firstLine );
        checkFirstLine( "First Name", FIRST_NAME_INDEX, firstLine );
        checkFirstLine( "User ID", USER_ID_INDEX, firstLine );
        checkFirstLine( "Role", ROLE_INDEX, firstLine );

        BlackBoardClass bb = new BlackBoardClass();

        for (int i = ROLE_INDEX + 1; i < firstLine.length; i++) {
            bb.addQuizName( firstLine[i] );
        }

        String[] studentLine;
        while ((studentLine = reader.readNext()) != null) {
            BlackBoardStudent.Builder builder = new BlackBoardStudent.Builder();
            builder.setFirstName(studentLine[FIRST_NAME_INDEX]);
            builder.setLastName(studentLine[LAST_NAME_INDEX]);
            builder.setId(studentLine[USER_ID_INDEX]);
            BlackBoardStudent student = builder.create();

            for (int i = ROLE_INDEX + 1; i < firstLine.length; i++) {
                student.setGrade(firstLine[i], studentLine[i]);
            }

            bb.addStudent( student );
        }

        return bb;
    }

    private static void checkFirstLine( String expected, int index, String[] array )
    {
        String s = array[index];
        if (!expected.equals( s ) ) {
            throw new IllegalStateException("Expected column " + index + " to contain \"" + expected + "\" not \"" + s + "\"");
        }
    }

    /**
     * A class imported from the BlackBoard export
     */
    static class BlackBoardClass {
        private List<String> quizNames = new ArrayList<String>();
        private List<BlackBoardStudent> students = new ArrayList<BlackBoardStudent>();

        void addQuizName(String quizName) {
            if (quizNames.contains( quizName )) {
                throw new IllegalStateException( "Already added quiz " + quizName);
            }
            quizNames.add(quizName);
        }

        void addStudent(BlackBoardStudent student) {
            if (students.contains( student )) {
                throw new IllegalStateException( "Already added student " + student);
            }
            students.add(student);
        }

        public List<String> getQuizNames() {
            return this.quizNames;
        }

        public List<BlackBoardStudent> getStudents() {
            return this.students;
        }
    }

    /**
     * A student imported form the BlackBoard export
     */
    static class BlackBoardStudent {
        private final String firstName;
        private final String lastName;
        private final String id;
        private final Map<String, Double> grades = new HashMap<String, Double>();

        private BlackBoardStudent( Builder builder )
        {
            this.firstName = builder.firstName;
            this.lastName = builder.lastName;
            this.id = builder.id;
        }

        public String getFirstName()
        {
            return firstName;
        }

        public String getId()
        {
            return id;
        }

        public String getLastName()
        {
            return lastName;
        }

        @Override
        public boolean equals( Object o )
        {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            BlackBoardStudent that = (BlackBoardStudent) o;

            return !(id != null ? !id.equals( that.id ) : that.id != null);

        }

        @Override
        public int hashCode()
        {
            return id != null ? id.hashCode() : 0;
        }

        public String toString()
        {
            return this.getFirstName() + " " + this.getLastName() + " (" + this.getId() + ")";
        }


        public void setGrade( String quiz, String grade )
        {
            if (grades.containsKey( quiz )) {
                throw new IllegalStateException( "Already have a grade of " + grades.get(quiz) + " for quiz " + quiz);
            }

            if (grade == null || "".equals( grade.trim() ) || "Not completed".equals( grade.trim() )) {
                return;
            }

            grades.put(quiz, Double.parseDouble( grade ));
        }

        public Double getGrade( String quizName )
        {
            return grades.get(quizName);
        }

        private static class Builder
        {
            private String firstName;
            private String lastName;
            private String id;

            public BlackBoardStudent create()
            {
                return new BlackBoardStudent(this);
            }

            public Builder setFirstName( String firstName )
            {
                this.firstName = firstName;
                return this;
            }

            public Builder setLastName( String lastName )
            {
                this.lastName = lastName;
                return this;
            }

            public Builder setId( String id )
            {
                this.id = id;
                return this;
            }
        }

    }

}
