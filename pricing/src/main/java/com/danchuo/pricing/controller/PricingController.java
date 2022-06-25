package com.danchuo.pricing.controller;

import com.danchuo.pricing.model.ShopError;
import com.danchuo.pricing.model.ShopUnitImportRequest;
import com.danchuo.pricing.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PricingController {

  @Autowired private PricingService pricingService;

  //  @Autowired private ShopUnitRepository shopUnitRepository;
  //
  //  @PostMapping(path = "/admin")
  //  public Iterable<ShopUnit> admin() {
  //    // return ResponseEntity.ok().body("ok master");
  //
  //    shopUnitRepository.deleteAll();
  //
  //    return shopUnitRepository.findAll();
  //  }

  @PostMapping("/imports")
  @ResponseBody
  public ResponseEntity<ShopError> importUnits(
      @RequestBody ShopUnitImportRequest shopUnitItemsRequest) {
    var isSuccessfully = pricingService.isImportSuccessful(shopUnitItemsRequest);

    ResponseEntity<ShopError> response;

    if (isSuccessfully) {
      response =
          ResponseEntity.ok()
              .body(new ShopError(ShopError.OK, "Вставка или обновление прошли успешно."));
    } else {
      response =
          ResponseEntity.badRequest()
              .body(new ShopError(ShopError.BAD_REQUEST, "Validation Failed"));
    }

    return response;
  }

  @DeleteMapping("/delete/{id}")
  @ResponseBody
  public ResponseEntity<ShopError> deleteUnit(@PathVariable String id) {
    var isSuccessfully = pricingService.isDeletingSuccessful(id);

    ResponseEntity<ShopError> response;

    if (isSuccessfully) {
      response = ResponseEntity.ok().body(new ShopError(ShopError.OK, "Удаление прошло успешно."));
    } else {
      response =
          ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new ShopError(ShopError.NOT_FOUND, "Item not found"));
    }

    return response;
  }

  @GetMapping(path = "/nodes/{id}")
  @ResponseBody
  public ResponseEntity<?> getNodes(@PathVariable String id) {
    var nodes = pricingService.getNodes(id);

    ResponseEntity<?> response;

    if (nodes.isPresent()) {
      response = ResponseEntity.ok().body(nodes.get());
    } else {
      response =
          ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new ShopError(ShopError.NOT_FOUND, "Item not found"));
    }

    return response;
  }
}
