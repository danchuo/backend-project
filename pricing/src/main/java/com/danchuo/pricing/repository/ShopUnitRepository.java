package com.danchuo.pricing.repository;

import com.danchuo.pricing.entity.ShopUnit;
import org.springframework.data.repository.CrudRepository;

public interface ShopUnitRepository extends CrudRepository<ShopUnit, String> {

  Iterable<ShopUnit> findAllByParentId(String id);
}
