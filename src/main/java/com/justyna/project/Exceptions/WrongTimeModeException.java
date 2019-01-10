package com.justyna.project.Exceptions;

public class WrongTimeModeException extends Exception {
    private String message;

    public WrongTimeModeException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
