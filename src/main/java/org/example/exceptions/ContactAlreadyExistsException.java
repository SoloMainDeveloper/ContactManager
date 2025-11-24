package org.example.exceptions;

public class ContactAlreadyExistsException extends Exception {
    public ContactAlreadyExistsException(String message) {
        super(message);
    }
}
