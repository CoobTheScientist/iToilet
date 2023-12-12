package com.example.itoiletmobileapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Rating;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;



public class DBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_database";
    private static final int DATABASE_VERSION = 1;



    // Users Table********************************************************************************
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "_id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SALT = "salt";
    private static final String COLUMN_ACCOUNT_STATUS = "accountStatus";
    private static final String COLUMN_SECURITY = "security";

    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_EMAIL + " TEXT," +
                    COLUMN_PASSWORD + " TEXT," +
                    COLUMN_SALT + " TEXT," +
                    COLUMN_ACCOUNT_STATUS + " INTEGER," +
                    COLUMN_SECURITY + " TEXT" +
                    ")";




    // Admin Table****************************************************************************
    private static final String TABLE_ADMIN = "admin";
    private static final String COLUMN_ADMIN_ID = "_id";
    private static final String COLUMN_ADMIN_EMAIL = "email";
    private static final String COLUMN_ADMIN_PASSWORD = "password";
    private static final String COLUMN_ADMIN_SALT = "salt";

    private static final String CREATE_ADMIN_TABLE =
            "CREATE TABLE " + TABLE_ADMIN + " (" +
                    COLUMN_ADMIN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_ADMIN_EMAIL + " TEXT," +
                    COLUMN_ADMIN_PASSWORD + " TEXT," +
                    COLUMN_ADMIN_SALT + " TEXT" +
                    ")";

    // Bathroom Table********************************************************************
    public static final String TABLE_BATHROOM = "bathroom";
    public static final String COLUMN_BATHROOM_ID = "_id";
    public static final String COLUMN_BATHROOM_ADDRESS = "address";
    public static final String COLUMN_BATHROOM_LOCATION = "location";
    public static final String COLUMN_BATHROOM_GENDER_TYPE = "genderType";


    private static final String CREATE_BATHROOM_TABLE =
            "CREATE TABLE " + TABLE_BATHROOM + " (" +
                    COLUMN_BATHROOM_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_BATHROOM_ADDRESS + " TEXT," +
                    COLUMN_BATHROOM_LOCATION + " TEXT," +
                    COLUMN_BATHROOM_GENDER_TYPE + " TEXT" +
                    ")";


    // Ratings Table*****************************************************************************
    private static final String TABLE_RATINGS = "ratings";
    private static final String COLUMN_RATING_ID = "_id";
    private static final String COLUMN_BATHROOM_ID_FK_RATINGS = "bathroom_id"; // Foreign key referencing bathroom table
    private static final String COLUMN_USER_ID_FK_RATINGS = "user_id"; // Foreign key referencing users table
    public static final String COLUMN_CLEANLINESS = "cleanliness";
    public static final String COLUMN_TOILET_PAPER = "toiletPaper";
    public static final String COLUMN_OVERALL = "overall";
    public static final String COLUMN_HANDICAP_ACCESSIBILITY = "handicapAccessibility"; // Y or N
    public static final String COLUMN_METHOD_OF_DRYING = "methodOfDrying";
    public static final String COLUMN_BABY_STATION = "babyStation"; // Y or N

    private static final String CREATE_RATINGS_TABLE =
            "CREATE TABLE " + TABLE_RATINGS + " (" +
                    COLUMN_RATING_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_BATHROOM_ID_FK_RATINGS + " INTEGER," +
                    COLUMN_USER_ID_FK_RATINGS + " INTEGER," +
                    COLUMN_CLEANLINESS + " INTEGER," +
                    COLUMN_TOILET_PAPER + " INTEGER," +
                    COLUMN_OVERALL + " INTEGER," +
                    COLUMN_HANDICAP_ACCESSIBILITY + " TEXT," +
                    COLUMN_METHOD_OF_DRYING + " TEXT," +
                    COLUMN_BABY_STATION + " TEXT," +
                    "FOREIGN KEY (" + COLUMN_BATHROOM_ID_FK_RATINGS + ") REFERENCES " + TABLE_BATHROOM + "(" + COLUMN_BATHROOM_ID + ")," +
                    "FOREIGN KEY (" + COLUMN_USER_ID_FK_RATINGS + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" +
                    ")";

    public DBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ADMIN_TABLE);
        db.execSQL(CREATE_BATHROOM_TABLE);
        db.execSQL(CREATE_RATINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATHROOM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATINGS);
        onCreate(db);
    }

    // Add methods for adding, updating, deleting, and querying data for each table
    public void addUser(String email, String password, int accountStatus, String securityQ)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        //Make salt
        String salt = makeSalt();
        values.put(COLUMN_SALT, salt);
        //hash password
        String hashed = hashPass(password, salt);
        values.put(COLUMN_PASSWORD, hashed);

        values.put(COLUMN_ACCOUNT_STATUS, accountStatus);
        values.put(COLUMN_SECURITY, securityQ);

        db.insert(TABLE_USERS, null, values);
        db.close();
    }
    public void addAdmin(String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //make salt and hash password
        String salt = makeSalt();
        String hashed = hashPass(password, salt);

        ContentValues values = new ContentValues();
        values.put(COLUMN_ADMIN_EMAIL, email);
        values.put(COLUMN_ADMIN_PASSWORD, hashed);
        values.put(COLUMN_ADMIN_SALT, salt);


        db.insert(TABLE_ADMIN, null, values);
        db.close();
    }

    public void addBathroom(String address, String location, String genderType)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //put values in content
        ContentValues values = new ContentValues();
        values.put(COLUMN_BATHROOM_ADDRESS, address);
        values.put(COLUMN_BATHROOM_LOCATION, location);
        values.put(COLUMN_BATHROOM_GENDER_TYPE, genderType);
        //Store in table
        db.insert(TABLE_BATHROOM, null, values);
        db.close();
    }
    public void addRating(int bathroomId, int userId, int cleanliness, int toiletPaper, int overall,
                          String handicapAccessibility, String methodOfDrying, String babyStation)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //Store values in content
        ContentValues values = new ContentValues();
        values.put(COLUMN_BATHROOM_ID_FK_RATINGS, bathroomId);
        values.put(COLUMN_USER_ID_FK_RATINGS, userId);
        values.put(COLUMN_CLEANLINESS, cleanliness);
        values.put(COLUMN_TOILET_PAPER, toiletPaper);
        values.put(COLUMN_OVERALL, overall);
        values.put(COLUMN_HANDICAP_ACCESSIBILITY, handicapAccessibility);
        values.put(COLUMN_METHOD_OF_DRYING, methodOfDrying);
        values.put(COLUMN_BABY_STATION, babyStation);

        //store in table
        db.insert(TABLE_RATINGS, null, values);
        db.close();
    }

    public int authenticateUser(String email, String password)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        //Columns to get
        String[] columns = {COLUMN_USER_ID, COLUMN_PASSWORD, COLUMN_SALT};

        // make selection
        String selection = COLUMN_EMAIL + " = ?";
        String[] args = {email};

        // query DB
        Cursor cursor = db.query(TABLE_USERS, columns, selection, args, null, null, null);

        //get indexes
        int storedIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
        int saltIndex = cursor.getColumnIndex(COLUMN_SALT);
        int userIndex = cursor.getColumnIndex(COLUMN_USER_ID);

        //read through query
        if(cursor.moveToFirst()){
            int user = cursor.getInt(userIndex);
            String storedHash = cursor.getString(storedIndex);
            String salt = cursor.getString(saltIndex);
            String givenHash = hashPass(password, salt);

            if(givenHash != null && givenHash.equals(storedHash)){
                cursor.close();
                db.close();
                return user;
            }
        }
        // Close the cursor and the database
        cursor.close();
        db.close();
        return -1;
    }
    public boolean authenticateAdmin(String email, String password)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            //columns to get
            String[] columns = {COLUMN_ADMIN_EMAIL, COLUMN_ADMIN_PASSWORD, COLUMN_SALT};

            //make selections
            String selection = COLUMN_ADMIN_EMAIL + " = ?";
            String[] args = {email};

            //query DB
            cursor = db.query(TABLE_ADMIN, columns, selection, args, null, null, null);

            //get indexes
            int storedIndex = cursor.getColumnIndex(COLUMN_ADMIN_PASSWORD);
            int saltIndex = cursor.getColumnIndex(COLUMN_ADMIN_SALT);

            //read through results
            if(cursor.moveToFirst()){
                String storedHash = cursor.getString(storedIndex);
                String salt = cursor.getString(saltIndex);
                String givenHash = hashPass(password, salt);

                if(givenHash != null && givenHash.equals(storedHash)){
                    return true;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        //close resources to prevent leaks
        finally{
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }

        return false;
    }

    public Cursor getAllUsers()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, null, null, null, null, null);
    }
    public Cursor getAllAdmins()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ADMIN, null, null, null, null, null, null);
    }
    public Cursor getAllBathrooms()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] returnable = {COLUMN_BATHROOM_ID, COLUMN_BATHROOM_ADDRESS, COLUMN_BATHROOM_LOCATION, COLUMN_BATHROOM_GENDER_TYPE};
        return db.query(TABLE_BATHROOM, returnable, null, null, null, null, null, null);
    }

    public List<Bathroom> getAllPending()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Bathroom> pending = new ArrayList<>();

        Cursor cursor = bathroomsRatings();

        //read through cursor
        if(cursor != null && cursor.moveToFirst()){
            do {
                //get indexes
                int idIndex = cursor.getColumnIndex(COLUMN_BATHROOM_ID);
                int addIndex = cursor.getColumnIndex(COLUMN_BATHROOM_ADDRESS);
                int locIndex = cursor.getColumnIndex(COLUMN_BATHROOM_LOCATION);
                int genIndex = cursor.getColumnIndex(COLUMN_BATHROOM_GENDER_TYPE);
                int ovrIndex = cursor.getColumnIndex(COLUMN_OVERALL);
                int cleanIndex = cursor.getColumnIndex(COLUMN_CLEANLINESS);
                int tpIndex = cursor.getColumnIndex(COLUMN_TOILET_PAPER);
                int handicapIndex = cursor.getColumnIndex(COLUMN_HANDICAP_ACCESSIBILITY);
                int dryIndex = cursor.getColumnIndex(COLUMN_METHOD_OF_DRYING);
                int babyIndex = cursor.getColumnIndex(COLUMN_BABY_STATION);
                int id = cursor.getInt(idIndex);
                String add = cursor.getString(addIndex);
                String loc = cursor.getString(locIndex);
                String gen = cursor.getString(genIndex);
                int ovr = cursor.getInt(ovrIndex);
                int clean = cursor.getInt(cleanIndex);
                int tp = cursor.getInt(tpIndex);
                String handicap = cursor.getString(handicapIndex);
                String drying = cursor.getString(dryIndex);
                String baby = cursor.getString(babyIndex);

                //Store info in bathroom object
                Bathroom bathroom = new Bathroom(id, add, loc, gen, ovr, clean, tp, handicap, drying, baby);


                pending.add(bathroom);
            }
            while(cursor.moveToNext());
            //TEST+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            Log.d("DBHandler", "Pending List size in getAllPending: " + pending.size());
            cursor.close();

        }
        db.close();

        return pending;
    }
    public Cursor getAllRatings()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RATINGS, null, null, null, null, null, null);
    }
    public Cursor bathroomsRatings()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        //query combination of bathroom and rating table
        String query = "SELECT " +
                "bathroom." + COLUMN_BATHROOM_ID + ", " +
                "bathroom." + COLUMN_BATHROOM_ADDRESS + ", " +
                "bathroom." + COLUMN_BATHROOM_LOCATION + ", " +
                "bathroom." + COLUMN_BATHROOM_GENDER_TYPE + ", " +
                "rating." + COLUMN_OVERALL + ", " +
                "rating." + COLUMN_CLEANLINESS + ", " +
                "rating." + COLUMN_TOILET_PAPER + ", " +
                "rating." + COLUMN_HANDICAP_ACCESSIBILITY + ", " +
                "rating." + COLUMN_METHOD_OF_DRYING + ", " +
                "rating." + COLUMN_BABY_STATION + " " +
                "FROM " + TABLE_BATHROOM + " bathroom " +
                "LEFT JOIN " + TABLE_RATINGS + " rating ON bathroom." +
                COLUMN_BATHROOM_ID + " = rating." + COLUMN_BATHROOM_ID_FK_RATINGS;

        return db.rawQuery(query, null);
    }
    public String getColumnEmail() {
        return COLUMN_EMAIL;
    }

    //add admin to the table
    public void populateAdmins(Context context){
        SQLiteDatabase db = this.getWritableDatabase();

        String entry;


        try {
            //open admin file
            InputStream input = context.getAssets().open("admins.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            //store info in file
            while((entry = reader.readLine()) != null) {
                String[] info = entry.split(",");

                //Get bathroom table info
                String email = info[0];
                String password = info[1];

                //add it to the admin table
                if(!adminExists(email)){
                    //write into table
                    addAdmin(email, password);
                }
            }
            reader.close();
            input.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            if(db != null && db.isOpen()){
                db.close();
            }
        }

    }

    //method to check if the admin is in the database
    private boolean adminExists(String email){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try{
            String[] column = {COLUMN_ADMIN_EMAIL};
            String selection = COLUMN_ADMIN_EMAIL + " = ?";
            String[] args = {email};

            cursor = db.query(TABLE_ADMIN, column, selection, args, null, null, null);
            return cursor.getCount() > 0;
        }
        finally{
            if(cursor != null){
                cursor.close();
                db.close();
            }
        }
    }
    //Check is user exists in DB
    public boolean userExists(String email){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try{
            String[] column = {COLUMN_EMAIL};
            String selection = COLUMN_EMAIL + " = ?";
            String[] args = {email};

            cursor = db.query(TABLE_USERS, column, selection, args, null, null, null);
            return cursor.getCount() > 0;
        }
        finally{
            if(cursor != null){
                cursor.close();
                db.close();
            }
        }
    }
    //method to update the users email
    public void updateEmail(String email, String newEmail){
        SQLiteDatabase db = getWritableDatabase();

        try{
            ContentValues values = new ContentValues();
            values.put(COLUMN_EMAIL, newEmail);
            db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[]{email});
        }
        finally{
            db.close();
        }

    }
    //Check if security question is right for forgot password
    public boolean securityRight(String email, String answer){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try{
            String[] column = {COLUMN_EMAIL, COLUMN_SECURITY};
            String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_SECURITY + " = ?";
            String[] args = {email, answer};

            cursor = db.query(TABLE_USERS, column, selection, args, null, null, null);
            return cursor.getCount() > 0;
        }
        finally{
            if(cursor != null){
                cursor.close();
                db.close();
            }
        }
    }
    //method to check if bathrooms exist
    private boolean bathroomExists(String add, String loc, String gender){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            String query = "SELECT * FROM " + TABLE_BATHROOM + " WHERE " +
                    COLUMN_BATHROOM_ADDRESS + " = ? AND " +
                    COLUMN_BATHROOM_LOCATION + " = ? AND " +
                    COLUMN_BATHROOM_GENDER_TYPE + " = ?";

            cursor = db.rawQuery(query, new String[]{add, loc, gender});

            // Check if the cursor has any rows
            return cursor.getCount() > 0;
        }
        finally{
            if(cursor != null){
                cursor.close();
                db.close();
            }
        }
    }
    //method to make sure passwords are up to par
    public boolean checkPass(String password){
        boolean valid = false;
        if(password.length() < 8){
            return false;
        }

        char[] charArray = password.toCharArray();
        for(int i = 0; i < charArray.length; i++){
            char c = charArray[i];
            if(Character.isDigit(c)){
                valid = true;
                break;
            }
        }
        return valid;
    }
    //Method to check is the admins password is correct upon log in
    public boolean isAdminPassCorrect(String password){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        int id = 1;

        try{
            String[] columns = {COLUMN_ADMIN_PASSWORD, COLUMN_ADMIN_SALT};
            String selection = COLUMN_ADMIN_ID + " = ?";
            String[] args = {String.valueOf(id)};
            cursor = db.query(TABLE_ADMIN, columns, selection, args, null, null, null);

            int storedHashIndex = cursor.getColumnIndex(COLUMN_ADMIN_PASSWORD);
            int saltIndex = cursor.getColumnIndex(COLUMN_ADMIN_SALT);

            if(cursor.moveToFirst()){
                String storedHash = cursor.getString(storedHashIndex);
                String salt = cursor.getString(saltIndex);
                String entry = hashPass(password, salt);
                boolean equal = storedHash.equals(entry);
                return equal;
            }

        }
        finally {
            if(cursor != null){
                cursor.close();
                db.close();
            }
        }
        return false;
    }

    //Method to check if users password is correct for log in
    public boolean isPassCorrect(String password){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        int id = LoginActivity.getUserID();

        try{
            String[] columns = {COLUMN_PASSWORD, COLUMN_SALT};
            String selection = COLUMN_USER_ID + " = ?";
            String[] args = {String.valueOf(id)};
            cursor = db.query(TABLE_USERS, columns, selection, args, null, null, null);

            int storedHashIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
            int saltIndex = cursor.getColumnIndex(COLUMN_SALT);

            if(cursor.moveToFirst()){
                String storedHash = cursor.getString(storedHashIndex);
                String salt = cursor.getString(saltIndex);
                String entry = hashPass(password, salt);
                boolean equal = storedHash.equals(entry);
                return equal;
            }

        }
        finally {
            if(cursor != null){
                cursor.close();
                db.close();
            }
        }
        return false;
    }
    //Method to update users password in settings
    public void updatePass(String id, String newPass){
        SQLiteDatabase db = getWritableDatabase();

        try{
            String salt = makeSalt();
            String newHash = hashPass(newPass, salt);
            ContentValues values = new ContentValues();
            values.put(COLUMN_PASSWORD, newHash);
            values.put(COLUMN_SALT, salt);
            db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{id});
        }
        finally {
            db.close();
        }
    }
    //method to update users password in forgot password
    public void updateForgotPass(String email, String newPass){
        SQLiteDatabase db = getWritableDatabase();

        try{
            String salt = makeSalt();
            String newHash = hashPass(newPass, salt);
            ContentValues values = new ContentValues();
            values.put(COLUMN_PASSWORD, newHash);
            values.put(COLUMN_SALT, salt);
            db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[]{email});
        }
        finally {
            db.close();
        }
    }
    //method to update admin password in settings
    public void updateAdminPass(String newPass){
        SQLiteDatabase db = getWritableDatabase();
        String id = String.valueOf(1);

        try{
            String salt = makeSalt();
            String newHash = hashPass(newPass, salt);
            ContentValues values = new ContentValues();
            values.put(COLUMN_ADMIN_PASSWORD, newHash);
            values.put(COLUMN_ADMIN_SALT, salt);
            db.update(TABLE_ADMIN, values, COLUMN_ADMIN_ID + " = ?", new String[]{id});
        }
        finally {
            db.close();
        }
    }
    //create random salt for users password
    private String makeSalt(){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();

        Random random = new Random();
        while (salt.length() < 6){
            int index = (int) (random.nextFloat() * characters.length());
            salt.append(characters.charAt(index));
        }

        String done = salt.toString();
        return done;
    }

    //method to encrypt passwords using PBKDF2
    private String hashPass(String password, String salt){

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 10, 250);

        try{
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashByte = keyFactory.generateSecret(keySpec).getEncoded();
            return convertToString(hashByte);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            e.printStackTrace();
            return null;
        }
    }
    private static String convertToString(byte[] bytes){
        StringBuilder converted = new StringBuilder();

        for(int i = 0; i < bytes.length; i++){
            byte b = bytes[i];
            converted.append(String.format("%02x", b));
        }
        return converted.toString();
    }
    //method to return the ID of the last inserted bathroom
    public int getLastBathroom(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(" + COLUMN_BATHROOM_ID + ") FROM " + TABLE_BATHROOM, null);

        int last = -1;
        if(cursor != null && cursor.moveToFirst()){
            last = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return last;
    }
    //method to delete bathroom for admin
    public void deleteBathroom(int bathroomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BATHROOM, COLUMN_BATHROOM_ID + " = ?", new String[]{String.valueOf(bathroomId)});
        db.delete(TABLE_RATINGS, COLUMN_BATHROOM_ID_FK_RATINGS + " = ?", new String[]{String.valueOf(bathroomId)});
        db.close();
    }
    public void clearBathrooms(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BATHROOM, null, null);
        db.delete(TABLE_RATINGS, null, null);
        db.close();
    }
    //method to return all users in an array list
    public List<User> getUsers(){
        List<User> list = new ArrayList<>();

        String[] cols = {COLUMN_USER_ID, COLUMN_EMAIL};
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, cols, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                int idIndex = cursor.getColumnIndex(COLUMN_USER_ID);
                int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
                int id = cursor.getInt(idIndex);
                String email = cursor.getString(emailIndex);

                User user = new User(id, email);
                list.add(user);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    //method to delete users for admins
    public void deleteUser(int userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userID)});
        db.close();
    }

    //TEST++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void logPendingTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RATINGS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    StringBuilder rowData = new StringBuilder();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        rowData.append(cursor.getColumnName(i)).append(": ").append(cursor.getString(i)).append(", ");
                    }
                    Log.d("MyApp", "Pending Table Row: " + rowData.toString());
                } while (cursor.moveToNext());
            } else {
                Log.d("MyApp", "No data in the Pending table.");
            }
        } finally {
            cursor.close();
            db.close();
        }
    }
}
