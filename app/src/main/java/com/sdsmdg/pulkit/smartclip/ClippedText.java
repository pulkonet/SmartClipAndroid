package com.sdsmdg.pulkit.smartclip;


public class ClippedText {

    private String text;
    private String email;
    private String time;

    public ClippedText(){

    }

    public ClippedText(String text, String email, String time){
        this.email=email;
        this.time=time;
        this.text=text;
    }

    public String getText() {return text;}

    public void setText(String text) {
        this.text = text;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTime(){return time;}

    public void setTime(String time){ this.time = time; }
}
