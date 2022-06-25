package com.danchuo.pricing.model;

import java.time.LocalDateTime;
import java.util.Set;

public class ShopUnitImportRequest {

  public LocalDateTime getUpdateDate() {
    return updateDate;
  }

  public Set<ShopUnitImport> getItems() {
    return items;
  }

  public void setUpdateDate(LocalDateTime updateDate) {
    this.updateDate = updateDate;
  }

  public void setItems(Set<ShopUnitImport> items) {
    this.items = items;
  }

  private LocalDateTime updateDate;
  private Set<ShopUnitImport> items;
}
