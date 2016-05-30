package com.cure.model;

/**
 * Created by DeepakGupta on 5/28/16.
 */
public class CheckLoginDTO {

    private boolean status;
    private String redirectTo;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }
}
