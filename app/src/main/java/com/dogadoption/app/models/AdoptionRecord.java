package com.dogadoption.app.models;

public class AdoptionRecord {
    private int id, userId, dogId;
    private String adoptionDate, notes;
    private String dogName, dogBreed, dogPhotoPath;
    private String adopterName, adopterEmail; // used in admin view

    public AdoptionRecord() {}

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }
    public int getUserId()                { return userId; }
    public void setUserId(int u)          { this.userId = u; }
    public int getDogId()                 { return dogId; }
    public void setDogId(int d)           { this.dogId = d; }
    public String getAdoptionDate()       { return adoptionDate; }
    public void setAdoptionDate(String d) { this.adoptionDate = d; }
    public String getNotes()              { return notes; }
    public void setNotes(String n)        { this.notes = n; }
    public String getDogName()            { return dogName; }
    public void setDogName(String n)      { this.dogName = n; }
    public String getDogBreed()           { return dogBreed; }
    public void setDogBreed(String b)     { this.dogBreed = b; }
    public String getDogPhotoPath()       { return dogPhotoPath; }
    public void setDogPhotoPath(String p) { this.dogPhotoPath = p; }
    public String getAdopterName()        { return adopterName; }
    public void setAdopterName(String n)  { this.adopterName = n; }
    public String getAdopterEmail()       { return adopterEmail; }
    public void setAdopterEmail(String e) { this.adopterEmail = e; }
}
