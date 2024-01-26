package com.project.pharmacy3jmobileapp.model;

public class OrdersModel {
    public OrdersModel() {
    }

    public OrdersModel(String amount, String contactNumber, String dateDelivered, String dateOrder, double discount, String fullName,
                       String itemName, int itemNumber, String deliveryMode, String paymentMode, String prescription, int productId,
                       int quantity, String shipAddress, String status, double totalPay, double unitPrice, String seniorCitizenId) {
        this.amount = amount;
        this.contactNumber = contactNumber;
        this.dateDelivered = dateDelivered;
        this.dateOrder = dateOrder;
        this.discount = discount;
        this.fullName = fullName;
        this.itemName = itemName;
        this.itemNumber = itemNumber;
        this.deliveryMode = deliveryMode;
        this.paymentMode = paymentMode;
        this.prescription = prescription;
        this.productId = productId;
        this.quantity = quantity;
        this.shipAddress = shipAddress;
        this.status = status;
        this.totalPay = totalPay;
        this.unitPrice = unitPrice;
        this.seniorCitizenId = seniorCitizenId;
    }

    private String amount;
    private String contactNumber;
    private String dateDelivered;
    private String dateOrder;
    private double discount;
    private String fullName;
    private String itemName;
    private int itemNumber;
    private String deliveryMode;
    private String paymentMode;
    private String prescription;
    private int productId;
    private int quantity;
    private String shipAddress;
    private String status;
    private double totalPay;
    private double unitPrice;

    private String seniorCitizenId;

    public String getSeniorCitizenId() {
        return seniorCitizenId;
    }

    public void setSeniorCitizenId(String seniorCitizenId) {
        this.seniorCitizenId = seniorCitizenId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDateDelivered() {
        return dateDelivered;
    }

    public void setDateDelivered(String dateDelivered) {
        this.dateDelivered = dateDelivered;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(double totalPay) {
        this.totalPay = totalPay;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
