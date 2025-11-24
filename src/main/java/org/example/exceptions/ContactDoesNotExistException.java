package org.example.exceptions;

public class ContactDoesNotExistException extends Exception {
    public ContactDoesNotExistException(String message) {
        super(message);
    }
}
