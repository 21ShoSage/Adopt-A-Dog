# 🐾 Adopt-A-Dog — Dog Adoption Android App

A clean, simple Android dog adoption app built with **Java + XML + SQLite**.

---

## 📱 Features

### Authentication
- User Registration (name, email, phone, address, profile photo)
- Login / Logout with session persistence (SharedPreferences)
- Password update from profile screen

### Dog Management (CRUD)
- **Add** dogs with: name, breed, blood type, height, weight, color, age, gender, description, photo
- **Edit** any dog's information at any time
- **Delete** dogs (also removes related adoption records)
- **View** full dog detail screen with all info

### Dog Listing
- Grid card view of all dogs
- **Search** by name, breed, or color
- **Filter**: All / Available only
- Live stats bar: Total | Available | Adopted
- Adoption status chip on every card

### Adopt a Dog
- Tap "Adopt This Dog" on the detail screen
- Marks dog as Adopted in the database
- Creates a timestamped adoption record

### User Profile
- Edit name, email, phone, address
- Change profile picture
- Change password (optional — leave blank to keep current)

### Adoption History
- Lists every dog the logged-in user has adopted
- Shows dog photo, name, breed, and adoption date
- Empty state when no history exists

---

## 🗄️ Database Schema (SQLite)

### `users`
| Column | Type |
|--------|------|
| id | INTEGER PK |
| name | TEXT |
| email | TEXT UNIQUE |
| password | TEXT |
| phone | TEXT |
| address | TEXT |
| photo_path | TEXT |

### `dogs`
| Column | Type |
|--------|------|
| id | INTEGER PK |
| name | TEXT |
| breed | TEXT |
| blood_type | TEXT |
| height | REAL (cm) |
| weight | REAL (kg) |
| color | TEXT |
| age | INTEGER |
| gender | TEXT |
| description | TEXT |
| photo_path | TEXT |
| is_adopted | INTEGER (0/1) |
| date_added | TEXT |

### `adoptions`
| Column | Type |
|--------|------|
| id | INTEGER PK |
| user_id | INTEGER FK |
| dog_id | INTEGER FK |
| adoption_date | TEXT |
| notes | TEXT |

---

## 🚀 Setup in Android Studio

### Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 34
- Java 8+
- Minimum Android API 24 (Android 7.0)

### Steps

1. **Open the project**
   - Launch Android Studio
   - Click **File → Open**
   - Navigate to and select the `DogAdoptionApp` folder
   - Click **OK**

2. **Sync Gradle**
   - Android Studio will prompt to sync — click **Sync Now**
   - Wait for dependencies to download (requires internet)

3. **Run the app**
   - Connect a physical device **or** start an Android Emulator (API 24+)
   - Click the green ▶ **Run** button
   - Select your device and click **OK**

### Dependencies (auto-downloaded via Gradle)
```
androidx.appcompat:appcompat:1.6.1
com.google.android.material:material:1.10.0
androidx.recyclerview:recyclerview:1.3.1
androidx.cardview:cardview:1.0.0
com.github.bumptech.glide:glide:4.16.0
de.hdodenhof:circleimageview:3.1.0
androidx.activity:activity:1.8.0
```

---

## 📂 Project Structure

```
app/src/main/
├── java/com/dogadoption/app/
│   ├── activities/
│   │   ├── SplashActivity.java        ← Launch screen
│   │   ├── LoginActivity.java         ← User login
│   │   ├── RegisterActivity.java      ← New account
│   │   ├── MainActivity.java          ← Dog list + search
│   │   ├── AddEditDogActivity.java    ← Add / edit dog form
│   │   ├── DogDetailActivity.java     ← Dog info + adopt
│   │   ├── ProfileActivity.java       ← Edit user profile
│   │   └── AdoptionHistoryActivity.java
│   ├── adapters/
│   │   ├── DogAdapter.java            ← Grid RecyclerView
│   │   └── AdoptionAdapter.java       ← History list
│   ├── database/
│   │   ├── DatabaseHelper.java        ← All SQLite operations
│   │   └── SessionManager.java        ← Login session
│   └── models/
│       ├── Dog.java
│       ├── User.java
│       └── AdoptionRecord.java
├── res/
│   ├── layout/          ← All XML layouts
│   ├── drawable/        ← Vector icons + backgrounds
│   ├── values/          ← colors, strings, themes, dimens
│   ├── menu/            ← Toolbar menu
│   └── xml/             ← FileProvider paths
└── AndroidManifest.xml
```

---

## 🎨 Design

- **Primary color**: `#FF6B35` (warm orange)
- **Background**: `#FFF8F5` (soft warm white)
- **Available status**: Green `#4CAF50`
- **Adopted status**: Gray `#9E9E9E`
- Material Components throughout (cards, chips, text fields, FAB)

---

## 📸 Permissions

The app requests:
- `READ_MEDIA_IMAGES` (Android 13+) / `READ_EXTERNAL_STORAGE` (older) — for picking photos
- `CAMERA` — optional, for future camera capture support

---

## 💡 Tips

- **First launch**: Register a new account — no default users
- **Dog photos**: Tap the photo area in Add/Edit Dog or dog detail card
- **Adopt a dog**: Open any available dog → tap "Adopt This Dog"
- **History**: Tap the ⋮ menu → "Adoption History"
- **Edit profile**: Tap ⋮ menu → "My Profile"
