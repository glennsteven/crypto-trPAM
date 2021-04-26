package com.example.tugasrancang;

class Biodata {
    public String fullname, email;
    public String  topUp;

    public Biodata(){
    }

    public Biodata(String topUp){
        this.topUp = topUp;
    }

    public Biodata(String fullname, String email){
        this.fullname = fullname;
        this.email = email;

    }
}
