package com.entity;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class Product {

	private String productName;
	private double productPrice;
	private String orderId;
	private Date orderDate = new Date();

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = new Date();
	}

	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Product(String productName, double productPrice, String orderId, Date orderDate) {
		super();
		this.productName = productName;
		this.productPrice = productPrice;
		this.orderId = orderId;
		this.orderDate = orderDate;
	}

}
