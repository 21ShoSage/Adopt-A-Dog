package com.dogadoption.app.models;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String photoPath;
    private String role; // "admin" or "user"

    public User() {}

    public User(String name, String email, String password, String phone, String address) {
        this.name = name; this.email = email; this.password = password;
        this.phone = phone; this.address = address;
    }

    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }
    public String getName()          { return name; }
    public void setName(String n)    { this.name = n; }
    public String getEmail()         { return email; }
    public void setEmail(String e)   { this.email = e; }
    public String getPassword()      { return password; }
    public void setPassword(String p){ this.password = p; }
    public String getPhone()         { return phone; }
    public void setPhone(String p)   { this.phone = p; }
    public String getAddress()       { return address; }
    public void setAddress(String a) { this.address = a; }
    public String getPhotoPath()     { return photoPath; }
    public void setPhotoPath(String p){ this.photoPath = p; }
    public String getRole()          { return role; }
    public void setRole(String r)    { this.role = r; }
    public boolean isAdmin()         { return "admin".equals(role); }
}
