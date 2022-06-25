package com.danchuo.pricing.model;

public class CategoryPriceCounter {
  private long amount;
  private long price;

  public long getAmount() {
    return amount;
  }

  public long getPrice() {
    return price;
  }

  public void addOnePrice(long inputPrice) {
    price += inputPrice;
    ++amount;
  }

  public void addPrice(long inputPrice) {
    price += inputPrice;
  }

  public void addAmount(long inputAmount) {
    amount += inputAmount;
  }

  public long getAverage() {
    return price / amount;
  }
}
