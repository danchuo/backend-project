package com.danchuo.pricing.model;

import com.danchuo.pricing.entity.ShopUnitType;

public class ShopUnitImport {
  private ShopUnitType type;
  private String id;
  private String name;
  private String parentId;
  private Long price;

  public ShopUnitImport(ShopUnitType type, String id, String name, String parentId, Long price) {
    this.type = type;
    this.id = id;
    this.name = name;
    this.parentId = parentId;
    this.price = price;
  }

  public ShopUnitImport() {}

  public ShopUnitType getType() {
    return type;
  }

  public void setType(ShopUnitType type) {
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public Long getPrice() {
    return price;
  }

  public void setPrice(Long price) {
    this.price = price;
  }
}
