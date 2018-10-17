package com.mobiapp4u.pc.routinebasket.Model;

public class Favourates {
    private String FoodId,UserPhone,FoodName,FoodPrice,FoodMenuId,FoodImage,FoodDescription,FoodDiscount;

    public Favourates() {
    }

    public Favourates(String foodId, String userPhone, String foodName, String foodPrice, String foodMenuId, String foodImage, String foodDescription, String foodDiscount) {
        FoodId = foodId;
        UserPhone = userPhone;
        FoodName = foodName;
        FoodPrice = foodPrice;
        FoodMenuId = foodMenuId;
        FoodImage = foodImage;
        FoodDescription = foodDescription;
        FoodDiscount = foodDiscount;
    }

    public String getFoodDescription() {
        return FoodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        FoodDescription = foodDescription;
    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getFoodPrice() {
        return FoodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        FoodPrice = foodPrice;
    }

    public String getFoodMenuId() {
        return FoodMenuId;
    }

    public void setFoodMenuId(String foodMenuId) {
        FoodMenuId = foodMenuId;
    }

    public String getFoodImage() {
        return FoodImage;
    }

    public void setFoodImage(String foodImage) {
        FoodImage = foodImage;
    }

    public String getFoodDiscount() {
        return FoodDiscount;
    }

    public void setFoodDiscount(String foodDiscount) {
        FoodDiscount = foodDiscount;
    }
}
