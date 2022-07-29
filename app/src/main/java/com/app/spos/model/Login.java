package com.app.spos.model;

import com.google.gson.annotations.SerializedName;

public class Login {


    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;

    @SerializedName("cell")
    private String cell;

    @SerializedName("store_id")
    private String storeId;

    @SerializedName("user_type")
    private String userType;



    @SerializedName("password")
    private String password;

    @SerializedName("value")
    private String value;
    @SerializedName("message")
    private String massage;

    @SerializedName("shop_name")
    private String shopName;

    @SerializedName("shop_address")
    private String shopAddress;

    @SerializedName("shop_email")
    private String shopEmail;


    @SerializedName("shop_contact")
    private String shopContact;

    @SerializedName("tax")
    private String tax;
    @SerializedName("currency_symbol")
    private String currencySymbol;
    @SerializedName("shop_status")
    private String shopStatus;

    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("owner_id")
    private String ownerID;
    @SerializedName("shop_vat_no")
    private String shop_vat_no;
    @SerializedName("logo")
    private String logo;
    @SerializedName("tax_type")
    private String tax_type;






    public String getStaffId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCell() {
        return cell;
    }

    public String getPassword() {
        return password;
    }


    public String getValue() {
        return value;
    }


    public String getStoreID() {
        return storeId;
    }




    public String getUserType() {
        return userType;
    }

    public String getMassage() {
        return massage;
    }


    public String getShopName() {
        return shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }


    public String getShopEmail() {
        return shopEmail;
    }

    public String getShopContact() {
        return shopContact;
    }


    public String getShopStatus() {
        return shopStatus;
    }


    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getTax() {
        return tax;
    }


    public String getShopId() {
        return shopId;
    }

    public String getOwnerID() {
        return ownerID;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public void setShopEmail(String shopEmail) {
        this.shopEmail = shopEmail;
    }

    public void setShopContact(String shopContact) {
        this.shopContact = shopContact;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getShop_vat_no() {
        return shop_vat_no;
    }

    public void setShop_vat_no(String shop_vat_no) {
        this.shop_vat_no = shop_vat_no;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTax_type() {
        return tax_type;
    }

    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
    }
}