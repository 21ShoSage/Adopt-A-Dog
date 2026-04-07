package com.dogadoption.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dogadoption.app.models.AdoptionRecord;
import com.dogadoption.app.models.Dog;
import com.dogadoption.app.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DogAdoptionDB";
    private static final int DATABASE_VERSION = 2; // bumped for role column

    public static final String TABLE_USERS     = "users";
    public static final String COL_USER_ID     = "id";
    public static final String COL_USER_NAME   = "name";
    public static final String COL_USER_EMAIL  = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_PHONE  = "phone";
    public static final String COL_USER_ADDRESS = "address";
    public static final String COL_USER_PHOTO  = "photo_path";
    public static final String COL_USER_ROLE   = "role"; // "admin" or "user"

    public static final String TABLE_DOGS        = "dogs";
    public static final String COL_DOG_ID        = "id";
    public static final String COL_DOG_NAME      = "name";
    public static final String COL_DOG_BREED     = "breed";
    public static final String COL_DOG_BLOOD_TYPE = "blood_type";
    public static final String COL_DOG_HEIGHT    = "height";
    public static final String COL_DOG_WEIGHT    = "weight";
    public static final String COL_DOG_COLOR     = "color";
    public static final String COL_DOG_AGE       = "age";
    public static final String COL_DOG_GENDER    = "gender";
    public static final String COL_DOG_DESCRIPTION = "description";
    public static final String COL_DOG_PHOTO     = "photo_path";
    public static final String COL_DOG_IS_ADOPTED = "is_adopted";
    public static final String COL_DOG_DATE_ADDED = "date_added";

    public static final String TABLE_ADOPTIONS      = "adoptions";
    public static final String COL_ADOPTION_ID      = "id";
    public static final String COL_ADOPTION_USER_ID = "user_id";
    public static final String COL_ADOPTION_DOG_ID  = "dog_id";
    public static final String COL_ADOPTION_DATE    = "adoption_date";
    public static final String COL_ADOPTION_NOTES   = "notes";

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER  = "user";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT NOT NULL, "
                + COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COL_USER_PASSWORD + " TEXT NOT NULL, "
                + COL_USER_PHONE + " TEXT, "
                + COL_USER_ADDRESS + " TEXT, "
                + COL_USER_PHOTO + " TEXT, "
                + COL_USER_ROLE + " TEXT DEFAULT 'user')";

        String CREATE_DOGS_TABLE = "CREATE TABLE " + TABLE_DOGS + " ("
                + COL_DOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DOG_NAME + " TEXT NOT NULL, "
                + COL_DOG_BREED + " TEXT, "
                + COL_DOG_BLOOD_TYPE + " TEXT, "
                + COL_DOG_HEIGHT + " REAL, "
                + COL_DOG_WEIGHT + " REAL, "
                + COL_DOG_COLOR + " TEXT, "
                + COL_DOG_AGE + " INTEGER, "
                + COL_DOG_GENDER + " TEXT, "
                + COL_DOG_DESCRIPTION + " TEXT, "
                + COL_DOG_PHOTO + " TEXT, "
                + COL_DOG_IS_ADOPTED + " INTEGER DEFAULT 0, "
                + COL_DOG_DATE_ADDED + " TEXT)";

        String CREATE_ADOPTIONS_TABLE = "CREATE TABLE " + TABLE_ADOPTIONS + " ("
                + COL_ADOPTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ADOPTION_USER_ID + " INTEGER, "
                + COL_ADOPTION_DOG_ID + " INTEGER, "
                + COL_ADOPTION_DATE + " TEXT, "
                + COL_ADOPTION_NOTES + " TEXT, "
                + "FOREIGN KEY(" + COL_ADOPTION_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), "
                + "FOREIGN KEY(" + COL_ADOPTION_DOG_ID + ") REFERENCES " + TABLE_DOGS + "(" + COL_DOG_ID + "))";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_DOGS_TABLE);
        db.execSQL(CREATE_ADOPTIONS_TABLE);

        // Seed default admin account
        ContentValues admin = new ContentValues();
        admin.put(COL_USER_NAME, "Admin");
        admin.put(COL_USER_EMAIL, "admin@gmail.com");
        admin.put(COL_USER_PASSWORD, "123123");
        admin.put(COL_USER_ROLE, ROLE_ADMIN);
        db.insert(TABLE_USERS, null, admin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add role column to existing installs without wiping data
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_USER_ROLE + " TEXT DEFAULT 'user'");
            // Re-seed admin if not present
            Cursor c = db.rawQuery("SELECT id FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "='admin@gmail.com'", null);
            boolean adminExists = c != null && c.getCount() > 0;
            if (c != null) c.close();
            if (!adminExists) {
                ContentValues admin = new ContentValues();
                admin.put(COL_USER_NAME, "Admin");
                admin.put(COL_USER_EMAIL, "admin@gmail.com");
                admin.put(COL_USER_PASSWORD, "123123");
                admin.put(COL_USER_ROLE, ROLE_ADMIN);
                db.insert(TABLE_USERS, null, admin);
            } else {
                // Make sure existing admin row has correct role
                ContentValues v = new ContentValues();
                v.put(COL_USER_ROLE, ROLE_ADMIN);
                db.update(TABLE_USERS, v, COL_USER_EMAIL + "='admin@gmail.com'", null);
            }
        }
    }

    // ==================== USER METHODS ====================

    public long registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_PASSWORD, user.getPassword());
        values.put(COL_USER_PHONE, user.getPhone());
        values.put(COL_USER_ADDRESS, user.getAddress());
        values.put(COL_USER_PHOTO, user.getPhotoPath());
        values.put(COL_USER_ROLE, ROLE_USER); // new registrations are always users
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        db.close();
        return user;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID},
                COL_USER_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        db.close();
        return user;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_PHONE, user.getPhone());
        values.put(COL_USER_ADDRESS, user.getAddress());
        values.put(COL_USER_PHOTO, user.getPhotoPath());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            values.put(COL_USER_PASSWORD, user.getPassword());
        }
        int rows = db.update(TABLE_USERS, values, COL_USER_ID + "=?",
                new String[]{String.valueOf(user.getId())});
        db.close();
        return rows;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_ROLE + "=?", new String[]{ROLE_USER},
                null, null, COL_USER_NAME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) users.add(cursorToUser(cursor));
            cursor.close();
        }
        db.close();
        return users;
    }
    public int deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADOPTIONS, COL_ADOPTION_USER_ID + "=?", new String[]{String.valueOf(userId)});
        int rows = db.delete(TABLE_USERS, COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return rows;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)));
        user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)));
        user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ADDRESS)));
        user.setPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHOTO)));
        int roleIdx = cursor.getColumnIndex(COL_USER_ROLE);
        user.setRole(roleIdx >= 0 ? cursor.getString(roleIdx) : ROLE_USER);
        return user;
    }


    public long addDog(Dog dog) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = dogToContentValues(dog);
        long id = db.insert(TABLE_DOGS, null, values);
        db.close();
        return id;
    }

    public int updateDog(Dog dog) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = dogToContentValues(dog);
        int rows = db.update(TABLE_DOGS, values, COL_DOG_ID + "=?",
                new String[]{String.valueOf(dog.getId())});
        db.close();
        return rows;
    }

    public int deleteDog(int dogId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADOPTIONS, COL_ADOPTION_DOG_ID + "=?", new String[]{String.valueOf(dogId)});
        int rows = db.delete(TABLE_DOGS, COL_DOG_ID + "=?", new String[]{String.valueOf(dogId)});
        db.close();
        return rows;
    }

    public List<Dog> getAllDogs() {
        List<Dog> dogs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DOGS, null, null, null, null, null,
                COL_DOG_DATE_ADDED + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) dogs.add(cursorToDog(cursor));
            cursor.close();
        }
        db.close();
        return dogs;
    }

    public List<Dog> getAvailableDogs() {
        List<Dog> dogs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DOGS, null, COL_DOG_IS_ADOPTED + "=0",
                null, null, null, COL_DOG_DATE_ADDED + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) dogs.add(cursorToDog(cursor));
            cursor.close();
        }
        db.close();
        return dogs;
    }

    public List<Dog> searchDogs(String query) {
        List<Dog> dogs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "%" + query + "%";
        Cursor cursor = db.query(TABLE_DOGS, null,
                COL_DOG_NAME + " LIKE ? OR " + COL_DOG_BREED + " LIKE ? OR " + COL_DOG_COLOR + " LIKE ?",
                new String[]{q, q, q}, null, null, COL_DOG_DATE_ADDED + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) dogs.add(cursorToDog(cursor));
            cursor.close();
        }
        db.close();
        return dogs;
    }

    public Dog getDogById(int dogId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DOGS, null,
                COL_DOG_ID + "=?", new String[]{String.valueOf(dogId)}, null, null, null);
        Dog dog = null;
        if (cursor != null && cursor.moveToFirst()) {
            dog = cursorToDog(cursor);
            cursor.close();
        }
        db.close();
        return dog;
    }

    private ContentValues dogToContentValues(Dog dog) {
        ContentValues values = new ContentValues();
        values.put(COL_DOG_NAME, dog.getName());
        values.put(COL_DOG_BREED, dog.getBreed());
        values.put(COL_DOG_BLOOD_TYPE, dog.getBloodType());
        values.put(COL_DOG_HEIGHT, dog.getHeight());
        values.put(COL_DOG_WEIGHT, dog.getWeight());
        values.put(COL_DOG_COLOR, dog.getColor());
        values.put(COL_DOG_AGE, dog.getAge());
        values.put(COL_DOG_GENDER, dog.getGender());
        values.put(COL_DOG_DESCRIPTION, dog.getDescription());
        values.put(COL_DOG_PHOTO, dog.getPhotoPath());
        values.put(COL_DOG_IS_ADOPTED, dog.isAdopted() ? 1 : 0);
        values.put(COL_DOG_DATE_ADDED, dog.getDateAdded());
        return values;
    }

    private Dog cursorToDog(Cursor cursor) {
        Dog dog = new Dog();
        dog.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_DOG_ID)));
        dog.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_NAME)));
        dog.setBreed(cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_BREED)));
        dog.setBloodType(cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_BLOOD_TYPE)));
        dog.setHeight(cursor.getFloat(cursor.getColumnIndexOrThrow(COL_DOG_HEIGHT)));
        dog.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(COL_DOG_WEIGHT)));
        dog.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_COLOR)));
        dog.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COL_DOG_AGE)));
        dog.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_GENDER)));
        dog.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_DESCRIPTION)));
        dog.setPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_PHOTO)));
        dog.setAdopted(cursor.getInt(cursor.getColumnIndexOrThrow(COL_DOG_IS_ADOPTED)) == 1);
        dog.setDateAdded(cursor.getString(cursor.getColumnIndexOrThrow(COL_DOG_DATE_ADDED)));
        return dog;
    }

    // ==================== ADOPTION METHODS ====================

    public long addAdoptionRecord(AdoptionRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ADOPTION_USER_ID, record.getUserId());
        values.put(COL_ADOPTION_DOG_ID, record.getDogId());
        values.put(COL_ADOPTION_DATE, record.getAdoptionDate());
        values.put(COL_ADOPTION_NOTES, record.getNotes());
        long id = db.insert(TABLE_ADOPTIONS, null, values);
        ContentValues dogValues = new ContentValues();
        dogValues.put(COL_DOG_IS_ADOPTED, 1);
        db.update(TABLE_DOGS, dogValues, COL_DOG_ID + "=?",
                new String[]{String.valueOf(record.getDogId())});
        db.close();
        return id;
    }

    public List<AdoptionRecord> getAdoptionsByUser(int userId) {
        List<AdoptionRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT a.*, d." + COL_DOG_NAME + " as dog_name, d." + COL_DOG_BREED
                + " as dog_breed, d." + COL_DOG_PHOTO + " as dog_photo "
                + "FROM " + TABLE_ADOPTIONS + " a "
                + "JOIN " + TABLE_DOGS + " d ON a." + COL_ADOPTION_DOG_ID + " = d." + COL_DOG_ID
                + " WHERE a." + COL_ADOPTION_USER_ID + " = ? "
                + "ORDER BY a." + COL_ADOPTION_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AdoptionRecord record = new AdoptionRecord();
                record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ADOPTION_ID)));
                record.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ADOPTION_USER_ID)));
                record.setDogId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ADOPTION_DOG_ID)));
                record.setAdoptionDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_ADOPTION_DATE)));
                record.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_ADOPTION_NOTES)));
                record.setDogName(cursor.getString(cursor.getColumnIndexOrThrow("dog_name")));
                record.setDogBreed(cursor.getString(cursor.getColumnIndexOrThrow("dog_breed")));
                record.setDogPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow("dog_photo")));
                records.add(record);
            }
            cursor.close();
        }
        db.close();
        return records;
    }

    /** Admin: all adoption records across all users */
    public List<AdoptionRecord> getAllAdoptionRecords() {
        List<AdoptionRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT a.*, d." + COL_DOG_NAME + " as dog_name, d." + COL_DOG_BREED
                + " as dog_breed, d." + COL_DOG_PHOTO + " as dog_photo, "
                + "u." + COL_USER_NAME + " as user_name, u." + COL_USER_EMAIL + " as user_email "
                + "FROM " + TABLE_ADOPTIONS + " a "
                + "JOIN " + TABLE_DOGS  + " d ON a." + COL_ADOPTION_DOG_ID  + " = d." + COL_DOG_ID
                + " JOIN " + TABLE_USERS + " u ON a." + COL_ADOPTION_USER_ID + " = u." + COL_USER_ID
                + " ORDER BY a." + COL_ADOPTION_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AdoptionRecord record = new AdoptionRecord();
                record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ADOPTION_ID)));
                record.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ADOPTION_USER_ID)));
                record.setDogId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ADOPTION_DOG_ID)));
                record.setAdoptionDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_ADOPTION_DATE)));
                record.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_ADOPTION_NOTES)));
                record.setDogName(cursor.getString(cursor.getColumnIndexOrThrow("dog_name")));
                record.setDogBreed(cursor.getString(cursor.getColumnIndexOrThrow("dog_breed")));
                record.setDogPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow("dog_photo")));
                record.setAdopterName(cursor.getString(cursor.getColumnIndexOrThrow("user_name")));
                record.setAdopterEmail(cursor.getString(cursor.getColumnIndexOrThrow("user_email")));
                records.add(record);
            }
            cursor.close();
        }
        db.close();
        return records;
    }

    public int getTotalDogs()        { return countWhere(TABLE_DOGS, null); }
    public int getAvailableDogsCount() { return countWhere(TABLE_DOGS, COL_DOG_IS_ADOPTED + "=0"); }
    public int getAdoptedDogsCount()   { return countWhere(TABLE_DOGS, COL_DOG_IS_ADOPTED + "=1"); }
    public int getTotalUsers()         { return countWhere(TABLE_USERS, COL_USER_ROLE + "='" + ROLE_USER + "'"); }

    private int countWhere(String table, String where) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + table + (where != null ? " WHERE " + where : "");
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) { count = cursor.getInt(0); cursor.close(); }
        db.close();
        return count;
    }
}
