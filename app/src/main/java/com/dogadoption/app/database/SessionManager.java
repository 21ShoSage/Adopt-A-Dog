package com.dogadoption.app.database;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME      = "DogAdoptionSession";
    private static final String KEY_LOGGED_IN  = "isLoggedIn";
    private static final String KEY_USER_ID    = "userId";
    private static final String KEY_EMAIL      = "userEmail";
    private static final String KEY_NAME       = "userName";
    private static final String KEY_PHOTO      = "userPhoto";
    private static final String KEY_ROLE       = "userRole";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private static SessionManager instance;

    public static SessionManager getInstance(Context context) {
        if (instance == null) instance = new SessionManager(context.getApplicationContext());
        return instance;
    }

    private SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createLoginSession(int userId, String email, String name, String photo, String role) {
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.putInt   (KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL,  email);
        editor.putString(KEY_NAME,   name);
        editor.putString(KEY_PHOTO,  photo  != null ? photo : "");
        editor.putString(KEY_ROLE,   role   != null ? role  : "user");
        editor.apply();
    }

    // convenience overloads
    public void createLoginSession(int userId, String email, String name, String photo) {
        createLoginSession(userId, email, name, photo, "user");
    }
    public void createLoginSession(int userId, String email, String name) {
        createLoginSession(userId, email, name, "", "user");
    }

    public boolean isLoggedIn()  { return prefs.getBoolean(KEY_LOGGED_IN, false); }
    public int    getUserId()    { return prefs.getInt(KEY_USER_ID, -1); }
    public String getUserEmail() { return prefs.getString(KEY_EMAIL, ""); }
    public String getUserName()  { return prefs.getString(KEY_NAME,  ""); }
    public String getUserPhoto() { return prefs.getString(KEY_PHOTO, ""); }
    public String getUserRole()  { return prefs.getString(KEY_ROLE,  "user"); }
    public boolean isAdmin()     { return "admin".equals(getUserRole()); }

    public void clearSession() { editor.clear(); editor.apply(); }
    public void logout()       { clearSession(); }
}
