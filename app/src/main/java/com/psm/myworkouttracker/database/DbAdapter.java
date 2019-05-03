package com.psm.myworkouttracker.database;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbAdapter{
    private DbHelper dbH;
    private SQLiteDatabase sqlDb;

    public DbAdapter(Context c){
        dbH = new DbHelper(c);
    }

    public void Open(){
        sqlDb = dbH.getWritableDatabase();
    }

    public void Close(){
        sqlDb.close();
    }

    //====================Exercises===================

    public List<String> getAllExercises(){
        List<String> category = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM " + DbHelper.TABLE_MACHINE;

        Open();
        Cursor c = sqlDb.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                category.add(c.getString(1));
            } while (c.moveToNext());
        }

        c.close();
        Close();

        return category;
    }

    public long insertExercisesData(String category){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.NAME_MACHINE, category);
        Open();
        long v = sqlDb.insert(DbHelper.TABLE_MACHINE, null, cv);
        Close();
        return v;
    }

    public Cursor getExercisesData(){
        String[] cols = {DbHelper.ID_MACHINE,DbHelper.NAME_MACHINE};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_MACHINE, cols, null,
                null, null, null, null);
        return c;
    }

    public Cursor getExercisesDataValue(String string){
        String[] cols = {DbHelper.ID_MACHINE,DbHelper.NAME_MACHINE};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_MACHINE, cols, DbHelper.NAME_MACHINE + "=" + string,
                null, null, null, null);
        return c;
    }

    public Cursor getAllExercisesData(int rowId){
        String[] cols = {DbHelper.ID_MACHINE,DbHelper.NAME_MACHINE};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_MACHINE, cols,
                DbHelper.ID_MACHINE + "=" + rowId, null, null, null, null);
        return c;
    }

    public long updateExercisesData(int rowId, String category) {
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.NAME_MACHINE, category);
        Open();
        long v = sqlDb.update(DbHelper.TABLE_MACHINE, cv,
                DbHelper.ID_MACHINE + "=" + rowId, null);
        Close();
        return v;
    }

    public int deleteExercisesData(int rowId){
        Open();
        int v = sqlDb.delete(DbHelper.TABLE_MACHINE,
                DbHelper.ID_MACHINE + "=" + rowId, null);
        Close();
        return v;
    }

    /*//====================Password===================
    public long insertPasswordData(String pass, String word){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.SET_PASSWORD, pass);
        cv.put(DbHelper.SECURITY_WORD, word);
        Open();
        sqlDb.execSQL(DbHelper.DROP_PASSWORD);
        sqlDb.execSQL(DbHelper.CREATE_PASSWORD);
        long v = sqlDb.insert(DbHelper.TABLE_PASSWORD, null, cv);
        Close();
        return v;
    }

    public Cursor getAllPasswordData(){
        String[] cols = {DbHelper.ID_PASSWORD,DbHelper.SET_PASSWORD,
                DbHelper.SECURITY_WORD};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_PASSWORD, cols, null,
                null, null, null, null);
        return c;
    }

    public String getPasswordData(){
        String password = new String();

        String selectQuery = "SELECT  * FROM " + DbHelper.TABLE_PASSWORD;

        Open();
        Cursor c = sqlDb.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                password = c.getString(1);
            } while (c.moveToNext());
        }

        c.close();
        Close();

        return password;
    }

    public void deleteAllPasswordData(){
        Open();
        sqlDb.execSQL(DbHelper.DROP_PASSWORD);
        sqlDb.execSQL(DbHelper.CREATE_PASSWORD);
        Close();
    }


    //====================Category===================

    public List<String> getAllLabels(){
        List<String> category = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM " + DbHelper.TABLE_CATEGORY;

        Open();
        Cursor c = sqlDb.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                category.add(c.getString(1));
            } while (c.moveToNext());
        }

        c.close();
        Close();

        return category;
    }

    public long insertCategoryData(String category){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.LIST_CATEGORY, category);
        Open();
        long v = sqlDb.insert(DbHelper.TABLE_CATEGORY, null, cv);
        Close();
        return v;
    }

    public Cursor getCategoryData(){
        String[] cols = {DbHelper.ID_CATEGORY,DbHelper.LIST_CATEGORY};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_CATEGORY, cols, null,
                null, null, null, null);
        return c;
    }

    public Cursor getAllCategoryData(int rowId){
        String[] cols = {DbHelper.ID_CATEGORY,DbHelper.LIST_CATEGORY};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_CATEGORY, cols,
                DbHelper.ID_CATEGORY + "=" + rowId, null, null, null, null);
        return c;
    }

    public long updateCategoryData(int rowId, String category) {
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.LIST_CATEGORY, category);
        Open();
        long v = sqlDb.update(DbHelper.TABLE_CATEGORY, cv,
                DbHelper.ID_CATEGORY + "=" + rowId, null);
        Close();
        return v;
    }

    public int deleteCategoryData(int rowId){
        Open();
        int v = sqlDb.delete(DbHelper.TABLE_CATEGORY,
                DbHelper.ID_CATEGORY + "=" + rowId, null);
        Close();
        return v;
    }

    public void deleteAllCategoryData(){
        Open();
        sqlDb.execSQL(DbHelper.DROP_CATEGORY);
        sqlDb.execSQL(DbHelper.CREATE_CATEGORY);
        sqlDb.execSQL("INSERT INTO " + DbHelper.TABLE_CATEGORY + " (" + DbHelper.LIST_CATEGORY + ") VALUES" +
                "('Allowance')," +
                "('Auto')," +
                "('Bill Payments')," +
                "('Bonus')," +
                "('Book/Stationary')," +
                "('Clothing')," +
                "('Debt')," +
                "('Electronic')," +
                "('Entertaiment')," +
                "('Food')," +
                "('Gift')," +
                "('Groceries')," +
                "('Medical')," +
                "('Miscellaneous')," +
                "('Personal Care')," +
                "('Salary')," +
                "('Travel')," +
                "('Vehicle');");
        Close();
    }


    //====================Income===================

    public long insertIncomeData(String price, String category, String date, String description){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.AMOUNT_INCOME, price);
        cv.put(DbHelper.CATEGORY_INCOME, category);
        cv.put(DbHelper.DATE_INCOME, date);
        cv.put(DbHelper.DESCRIPTION_INCOME, description);
        Open();
        long v = sqlDb.insert(DbHelper.TABLE_INCOME, null, cv);
        Close();
        return v;
    }

    public Cursor getIncomeData(){
        String[] cols = {DbHelper.ID_INCOME,DbHelper.AMOUNT_INCOME,
                DbHelper.CATEGORY_INCOME, DbHelper.DATE_INCOME};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_INCOME, cols, null,
                null, null, null, null);
        return c;
    }

    public Cursor getAllIncomeData(int rowId){
        String[] cols = {DbHelper.ID_INCOME,DbHelper.AMOUNT_INCOME,
                DbHelper.CATEGORY_INCOME, DbHelper.DATE_INCOME ,
                DbHelper.DESCRIPTION_INCOME};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_INCOME, cols,
                DbHelper.ID_INCOME + "=" + rowId, null, null, null, null);
        return c;
    }

    public long updateIncomeData(int rowId, String price, String category, String date, String description) {
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.AMOUNT_INCOME, price);
        cv.put(DbHelper.CATEGORY_INCOME, category);
        cv.put(DbHelper.DATE_INCOME, date);
        cv.put(DbHelper.DESCRIPTION_INCOME, description);
        Open();
        long v = sqlDb.update(DbHelper.TABLE_INCOME, cv,
                DbHelper.ID_INCOME + "=" + rowId, null);
        Close();
        return v;
    }

    public int deleteIncomeData(int rowId){
        Open();
        int v = sqlDb.delete(DbHelper.TABLE_INCOME,
                DbHelper.ID_INCOME + "=" + rowId, null);
        Close();
        return v;
    }

    public void deleteAllIncomeData(){
        Open();
        sqlDb.execSQL(DbHelper.DROP_INCOME);
        sqlDb.execSQL(DbHelper.CREATE_INCOME);
        Close();
    }

    public double getIncomeBalance(){
        Open();
        double sum = 0;
        Cursor c = sqlDb.rawQuery(
                "SELECT SUM(" + DbHelper.AMOUNT_INCOME + ") FROM "+DbHelper.TABLE_INCOME, null);
        c.moveToFirst();
        if(c.getCount()>0){
            sum=c.getDouble(0);
        }
        Close();
        return sum;
    }


    //====================Expense===================

    public long insertExpenseData(String price, String category, String date, String description){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.AMOUNT_EXPENSE, price);
        cv.put(DbHelper.CATEGORY_EXPENSE, category);
        cv.put(DbHelper.DATE_EXPENSE, date);
        cv.put(DbHelper.DESCRIPTION_EXPENSE, description);
        Open();
        long v = sqlDb.insert(DbHelper.TABLE_EXPENSE, null, cv);
        Close();
        return v;
    }

    public Cursor getExpenseData(){
        String[] cols = {DbHelper.ID_EXPENSE,DbHelper.AMOUNT_EXPENSE,
                DbHelper.CATEGORY_EXPENSE, DbHelper.DATE_EXPENSE};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_EXPENSE, cols, null,
                null, null, null, null);
        return c;
    }

    public Cursor getAllExpenseData(int rowId){
        String[] cols = {DbHelper.ID_EXPENSE,DbHelper.AMOUNT_EXPENSE,
                DbHelper.CATEGORY_EXPENSE, DbHelper.DATE_EXPENSE ,
                DbHelper.DESCRIPTION_EXPENSE};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_EXPENSE, cols,
                DbHelper.ID_EXPENSE + "=" + rowId, null, null, null, null);
        return c;
    }

    public long updateExpenseData(int rowId, String price, String category, String date, String description){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.AMOUNT_EXPENSE, price);
        cv.put(DbHelper.CATEGORY_EXPENSE, category);
        cv.put(DbHelper.DATE_EXPENSE, date);
        cv.put(DbHelper.DESCRIPTION_EXPENSE, description);
        Open();
        long v = sqlDb.update(DbHelper.TABLE_EXPENSE, cv,
                DbHelper.ID_EXPENSE + "=" + rowId, null);
        Close();
        return v;
    }

    public int deleteExpenseData(int rowId){
        Open();
        int v = sqlDb.delete(DbHelper.TABLE_EXPENSE,
                DbHelper.ID_EXPENSE + "=" + rowId, null);
        Close();
        return v;
    }

    public void deleteAllExpenseData(){
        Open();
        sqlDb.execSQL(DbHelper.DROP_EXPENSE);
        sqlDb.execSQL(DbHelper.CREATE_EXPENSE);
        Close();
    }

    public double getExpenseBalance(){
        Open();
        double sum = 0;
        Cursor c = sqlDb.rawQuery(
                "SELECT SUM(" + DbHelper.AMOUNT_EXPENSE + ") FROM "+DbHelper.TABLE_EXPENSE, null);
        c.moveToFirst();
        if(c.getCount()>0){
            sum=c.getDouble(0);
        }
        Close();
        return sum;
    }


    //====================Reminder===================

    public long insertReminderData(String price, String category, String date, String description, String time, String addto, String title){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.AMOUNT_REMINDER, price);
        cv.put(DbHelper.CATEGORY_REMINDER, category);
        cv.put(DbHelper.DATE_REMINDER, date);
        cv.put(DbHelper.DESCRIPTION_REMINDER, description);
        cv.put(DbHelper.TIME_REMINDER, time);
        cv.put(DbHelper.ADDTO_REMINDER, addto);
        cv.put(DbHelper.TITLE_REMINDER, title);
        Open();
        long v = sqlDb.insert(DbHelper.TABLE_REMINDER, null, cv);
        Close();
        return v;
    }

    public Cursor getReminderData(){
        String[] cols = {DbHelper.ID_REMINDER,DbHelper.TITLE_REMINDER};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_REMINDER, cols, null,
                null, null, null, null);
        return c;
    }

    public Cursor getAllReminderData(int rowId){
        String[] cols = {DbHelper.ID_REMINDER,DbHelper.AMOUNT_REMINDER,
                DbHelper.CATEGORY_REMINDER, DbHelper.DATE_REMINDER,
                DbHelper.DESCRIPTION_REMINDER, DbHelper.TIME_REMINDER,
                DbHelper.ADDTO_REMINDER, DbHelper.TITLE_REMINDER};
        Open();
        Cursor c = sqlDb.query(DbHelper.TABLE_REMINDER, cols,
                DbHelper.ID_REMINDER + "=" + rowId, null, null, null, null);
        return c;
    }

    public long updateReminderData(int rowId, String price, String category, String date, String description, String time, String addto, String title){
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.AMOUNT_REMINDER, price);
        cv.put(DbHelper.CATEGORY_REMINDER, category);
        cv.put(DbHelper.DATE_REMINDER, date);
        cv.put(DbHelper.DESCRIPTION_REMINDER, description);
        cv.put(DbHelper.TIME_REMINDER, time);
        cv.put(DbHelper.ADDTO_REMINDER, addto);
        cv.put(DbHelper.TITLE_REMINDER, title);
        Open();
        long v = sqlDb.update(DbHelper.TABLE_REMINDER, cv,
                DbHelper.ID_REMINDER + "=" + rowId, null);
        Close();
        return v;
    }

    public int deleteReminderData(int rowId){
        Open();
        int v = sqlDb.delete(DbHelper.TABLE_REMINDER,
                DbHelper.ID_REMINDER + "=" + rowId, null);
        Close();
        return v;
    }

    public void deleteAllReminderData(){
        Open();
        sqlDb.execSQL(DbHelper.DROP_REMINDER);
        sqlDb.execSQL(DbHelper.CREATE_REMINDER);
        Close();
    }*/
}
