package com.danchuo.pricing.service;

import com.danchuo.pricing.entity.ShopUnit;
import com.danchuo.pricing.entity.ShopUnitType;
import com.danchuo.pricing.model.CategoryPriceCounter;
import com.danchuo.pricing.model.ShopUnitImportRequest;
import com.danchuo.pricing.repository.ShopUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

@Service
public class PricingService {

  @Autowired private ShopUnitRepository shopUnitRepository;

  public Optional<ShopUnit> getNodes(String id) {
    Optional<ShopUnit> node = shopUnitRepository.findById(id);

    if (node.isPresent() && node.get().getType() == ShopUnitType.CATEGORY) {
      var counter = new CategoryPriceCounter();
      getChildrenRec(node.get(), counter);

      if (counter.getAmount() > 0) {
        node.get().setPrice(counter.getAverage());
      }
    }

    return node;
  }

  private void getChildrenRec(ShopUnit parentCategory, CategoryPriceCounter counter) {
    parentCategory.setChildren(new HashSet<>());

    var children = shopUnitRepository.findAllByParentId(parentCategory.getId());

    for (var child : children) {
      if (child.getType() == ShopUnitType.OFFER) {
        counter.addOnePrice(child.getPrice());
      } else {
        var counterNext = new CategoryPriceCounter();
        getChildrenRec(child, counterNext);

        if (counterNext.getAmount() > 0) {
          child.setPrice(counterNext.getAverage());
        }

        counter.addPrice(counterNext.getPrice());
        counter.addAmount(counterNext.getAmount());
      }
      parentCategory.getChildren().add(child);
    }
  }

  public boolean isDeletingSuccessful(String id) {
    var existedWithSameId = shopUnitRepository.findById(id);

    if (existedWithSameId.isEmpty()) {
      return false;
    }
    shopUnitRepository.delete(existedWithSameId.get());

    if (existedWithSameId.get().getType() == ShopUnitType.CATEGORY) {
      deleteUnitChildrenRec(existedWithSameId.get().getId());
    }

    return true;
  }

  private void deleteUnitChildrenRec(String parentId) {
    var children = shopUnitRepository.findAllByParentId(parentId);

    for (var child : children) {
      shopUnitRepository.delete(child);

      if (child.getType() == ShopUnitType.CATEGORY) {
        deleteUnitChildrenRec(child.getId());
      }
    }
  }

  public boolean isImportSuccessful(ShopUnitImportRequest shopUnitImportRequest) {
    if (shopUnitImportRequest.getUpdateDate() == null) {
      return false;
    }

    var receivedShopUnits = new HashMap<String, ShopUnit>();

    for (var shopUnitRequest : shopUnitImportRequest.getItems()) {
      if (shopUnitRequest.getName() == null
          || shopUnitRequest.getId() == null
          || shopUnitRequest.getType() == null) {
        return false;
      }

      if (receivedShopUnits.containsKey(shopUnitRequest.getId())) {
        return false;
      }

      var shopUnit = new ShopUnit();

      if (shopUnitRequest.getType() == ShopUnitType.OFFER) {
        if (shopUnitRequest.getPrice() == null || shopUnitRequest.getPrice() < 0) {
          return false;
        } else {
          shopUnit.setPrice(shopUnitRequest.getPrice());
        }
      } else {
        if (shopUnitRequest.getPrice() != null) {
          return false;
        }
      }

      var existedWithSameId = shopUnitRepository.findById(shopUnitRequest.getId());

      if (existedWithSameId.isPresent()
          && existedWithSameId.get().getType() != shopUnitRequest.getType()) {
        return false;
      }

      if (shopUnitRequest.getParentId() != null) {
        if (receivedShopUnits.containsKey(shopUnitRequest.getParentId())
            && receivedShopUnits.get(shopUnitRequest.getParentId()).getType()
                != ShopUnitType.CATEGORY) {
          return false;
        }

        var existedParentId = shopUnitRepository.findById(shopUnitRequest.getParentId());

        if (existedParentId.isPresent()
            && existedParentId.get().getType() != ShopUnitType.CATEGORY) {
          return false;
        }
      }

      shopUnit.setId(shopUnitRequest.getId());
      shopUnit.setName(shopUnitRequest.getName());
      shopUnit.setParentId(shopUnitRequest.getParentId());
      shopUnit.setType(shopUnitRequest.getType());
      shopUnit.setDate(shopUnitImportRequest.getUpdateDate());
      receivedShopUnits.put(shopUnit.getId(), shopUnit);
    }

    shopUnitRepository.saveAll(receivedShopUnits.values());

    return true;
  }
}
