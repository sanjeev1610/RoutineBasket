package com.mobiapp4u.pc.routinebasket.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.mobiapp4u.pc.routinebasket.Model.Favourates;
import com.mobiapp4u.pc.routinebasket.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database  extends SQLiteAssetHelper {
    private static final String DB_NAME = "RoutineDB.db";
    private static final int DB_VER = 2;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCart(String userPhone)
    {
     SQLiteDatabase db = getReadableDatabase();
     SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

     String[] sqlSelect = {"UserPhone", "ProductId", "ProductName", "Quantity", "Price", "Discount","Image"};
     String tableName = "OrderDetail";

     qb.setTables(tableName);
     Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);
      final List<Order> result = new ArrayList<>();
      if(c.moveToFirst()){
          do{
              result.add(new Order(
                      c.getString(c.getColumnIndex("UserPhone")),
                      c.getString(c.getColumnIndex("ProductId")),
                      c.getString(c.getColumnIndex("ProductName")),
                              c.getString(c.getColumnIndex("Quantity")),
                                      c.getString(c.getColumnIndex("Price")),
                                              c.getString(c.getColumnIndex("Discount")),
                                                c.getString(c.getColumnIndex("Image"))));
          }while (c.moveToNext());
      }
      return result;
    }//getCart

    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(UserPhone,ProductId,ProductName,Quantity,Price,Discount,Image) VALUES('%s','%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        db.execSQL(query);
    }

    public void cleanCart(String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'",userPhone);
        db.execSQL(query);
    }
    public int getCounterCount(String userPhone) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail WHERE UserPhone='%s'",userPhone);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){

            do{
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }


        return count;
    }
    public Boolean checkFoodExists(String foodid,String userPhone){
          Boolean flag = false;
          SQLiteDatabase db = getReadableDatabase();
          Cursor cursor = null;
          String sqlQuery = String.format("SELECT * FROM OrderDetail WHERE UserPhone='%s' AND ProductId='%s'",userPhone,foodid);
          cursor = db.rawQuery(sqlQuery,null);
          if(cursor.getCount()>0){
              flag=true;
          }else {
              flag = false;
          }
          cursor.close();
          return flag;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = '%s' WHERE UserPhone = '%s' AND ProductId='%s'",order.getQuantity(),order.getUserPhone(),order.getProductId());
        db.execSQL(query);
    }
    public void increaseCart(String userPhone,String foodid) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = Quantity+1 WHERE UserPhone = '%s' AND ProductId='%s'",userPhone,foodid);
        db.execSQL(query);
    }
    public void increaseCartfoodDetail(String userPhone,String foodid, int num) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = Quantity+'%s' WHERE UserPhone = '%s' AND ProductId='%s'",num,userPhone,foodid);
        db.execSQL(query);
    }



    public Boolean isFavourate(String foodid,String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorates WHERE FoodId = '%s' AND UserPhone = '%s';",foodid,userPhone);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() <= 0 ){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    public void addToFav(Favourates food){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorates(FoodId,UserPhone,FoodName,FoodPrice,FoodMenuId,FoodImage,FoodDescription,FoodDiscount) VALUES('%s','%s','%s','%s','%s','%s','%s', '%s');",
                food.getFoodId(),
                food.getUserPhone(),
                food.getFoodName(),food.getFoodPrice(),food.getFoodMenuId(),food.getFoodImage(),food.getFoodDescription(),food.getFoodDiscount());
        db.execSQL(query);
    }

    public List<Favourates> getAllFavourates(String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"FoodId", "UserPhone", "FoodName", "FoodPrice", "FoodMenuId", "Foodimage","FoodDescription, FoodDiscount"};
        String tableName = "Favorates";

        qb.setTables(tableName);
        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);
        final List<Favourates> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                result.add(new Favourates(
                        c.getString(c.getColumnIndex("FoodId")),
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("FoodName")),
                        c.getString(c.getColumnIndex("FoodPrice")),
                        c.getString(c.getColumnIndex("FoodMenuId")),
                        c.getString(c.getColumnIndex("Foodimage")),
                        c.getString(c.getColumnIndex("FoodDescription")),
                        c.getString(c.getColumnIndex("FoodDiscount"))));
            }while (c.moveToNext());
        }
        return result;
    }
    public void removeFromFav(String foodid,String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorates WHERE FoodId = '%s' AND UserPhone = '%s';",foodid, userPhone);
        db.execSQL(query);
    }


    public void removeFromCart(String productId, String phone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s' AND ProductId='%s'",phone,productId);
        db.execSQL(query);
    }
}
