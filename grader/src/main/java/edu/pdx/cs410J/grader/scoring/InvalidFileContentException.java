package edu.pdx.cs410J.grader.scoring;

/**
 * Created by sushamkumar on 9/1/17.
 */
public class InvalidFileContentException extends Exception {
    public InvalidFileContentException() { super("Out file is corrupted"); }
    public InvalidFileContentException(String message) { super(message); }

}
