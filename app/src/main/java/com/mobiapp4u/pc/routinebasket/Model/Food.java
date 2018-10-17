package com.mobiapp4u.pc.routinebasket.Model;

public class Food {
    private String name1;
    private String image;
    private String description;
    private String price;
    private String menuId;
    private String discount;
    private String foodId;
    private String userPhone;


    public Food() {

    }

    public Food(String name1, String image, String description, String price, String menuId, String discount) {
        this.name1 = name1;
        this.image = image;
        this.description = description;
        this.price = price;
        this.menuId = menuId;
        this.discount = discount;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
