package com.dogadoption.app.models;

public class Dog {
    private int id;
    private String name;
    private String breed;
    private String bloodType;
    private float height;
    private float weight;
    private String color;
    private int age;
    private String gender;
    private String description;
    private String photoPath;
    private boolean isAdopted;
    private String dateAdded;

    public Dog() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }
    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public boolean isAdopted() { return isAdopted; }
    public void setAdopted(boolean adopted) { isAdopted = adopted; }
    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }
}
