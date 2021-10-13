package com.mobileleader.edoc.exception;

public class EdocException extends RuntimeException {
    private static final long serialVersionUID = -1L;

    private EdocError error;

    public EdocException() {
    }
    
    public EdocException(String message) {
    	super(message);
    }

    public EdocException(String message, Throwable t) {
        super(message, t);
    }

    public EdocException(Throwable t) {
        super(t);
    }

    public EdocException(Throwable t, EdocError error) {
        super(t);
        this.error = error;
    }

    public EdocException(String message, EdocError error) {
        super(message);
        this.error = error;
    }

    public EdocException(String message, Throwable t, EdocError error) {
        super(message, t);
        this.error = error;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getDetailMessage() {
        String detailMessage = getMessage();

        for (Throwable t = this; t.getCause() != null && t != t.getCause();) {
            t = t.getCause();
            detailMessage = (new StringBuilder()).append(detailMessage).append("\n").toString();
            detailMessage = (new StringBuilder()).append(detailMessage).append(t.getMessage()).toString();
        }

        return detailMessage;
    }

    public EdocError getError() {
        return error;
    }
}
