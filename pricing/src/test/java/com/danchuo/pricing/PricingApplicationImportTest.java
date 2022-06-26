package com.danchuo.pricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.danchuo.pricing.entity.ShopUnitType;
import com.danchuo.pricing.model.ShopUnitImport;
import com.danchuo.pricing.model.ShopUnitImportRequest;
import com.danchuo.pricing.repository.ShopUnitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = "/clear-shopunit-table-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PricingApplicationImportTest {

  private static final String VALIDATION_FAILED =
          """
                  {"code": 400, "message": "Validation Failed"}""";

  private static final String IMPORT_SUCCESS = """
          {"code": 200, "message": "The insert or update was successful."}""";

  @Autowired private MockMvc mockMvc;
  @Autowired
  private ShopUnitRepository shopUnitRepository;

  private static ShopUnitImportRequest getRequestWithSet(Set<ShopUnitImport> set) {
    var request = new ShopUnitImportRequest();
    request.setUpdateDate(LocalDateTime.now());
    request.setItems(set);
    return request;
  }

  void createMockMvcPerformImportsWithExpectedValidationFailed(ShopUnitImportRequest shopUnitImportRequest) throws Exception{
    mockMvc
            .perform(post("/imports").contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().findAndRegisterModules().writeValueAsString(shopUnitImportRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json(VALIDATION_FAILED));
  }

  @Test
  @Sql(value = "/create-shopunit-root-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void testImportUnits_ImportExistedShopUnitToUpdatePriceOkRequest() throws Exception {
    var id = "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1";
    Long updatedPrice = 150L;
    var firstImport = new ShopUnitImport(ShopUnitType.OFFER, "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1", "Товар", null, updatedPrice);

    var shopUnitItemsRequest = getRequestWithSet(Set.of(firstImport));

    mockMvc
            .perform(post("/imports").contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().findAndRegisterModules().writeValueAsString(shopUnitItemsRequest)))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json(IMPORT_SUCCESS));

    var importedShopUnit = shopUnitRepository.findById(id);

    assertTrue(importedShopUnit.isPresent());
    assertEquals((long) importedShopUnit.get().getPrice(), updatedPrice);
  }

  @Test
  @Sql(value = "/create-shopunit-root-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void testImportUnits_ChangeTypeOfShopUnitFromDataBaseBadRequest() throws Exception {
    var firstImport = new ShopUnitImport(ShopUnitType.CATEGORY, "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1", "first", null, null);

    var shopUnitItemsRequest = getRequestWithSet(Set.of(firstImport));

    createMockMvcPerformImportsWithExpectedValidationFailed(shopUnitItemsRequest);
  }

  @Test
  @Sql(value = "/create-shopunit-root-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void testImportUnits_SetParentOfOfferTypeFromDataBaseBadRequest() throws Exception {
    var firstImport = new ShopUnitImport(ShopUnitType.OFFER, "2222", "first", "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1", 150L);

    var shopUnitItemsRequest = getRequestWithSet(Set.of(firstImport));

    createMockMvcPerformImportsWithExpectedValidationFailed(shopUnitItemsRequest);
  }

  @Test
  void testImportUnits_ImportOneShopUnitOkRequest() throws Exception {
    var id = "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1";
    var firstImport = new ShopUnitImport(ShopUnitType.CATEGORY, id, "Товары", null, null);

    var shopUnitItemsRequest = getRequestWithSet(Set.of(firstImport));

    mockMvc
            .perform(post("/imports").contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().findAndRegisterModules().writeValueAsString(shopUnitItemsRequest)))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json(IMPORT_SUCCESS));

    var importedShopUnit = shopUnitRepository.findById(id);

    assertTrue(importedShopUnit.isPresent());
    assertSame(importedShopUnit.get().getId(), id);
  }

  @Test
  void testImportUnits_TwoUnitsWithSameIdBadRequest() throws Exception {
    var firstImport = new ShopUnitImport(ShopUnitType.OFFER, "1", "first", null, 100L);
    var secondImport = new ShopUnitImport(ShopUnitType.OFFER, "1", "second", null, 200L);

    var shopUnitItemsRequest = getRequestWithSet(Set.of(firstImport, secondImport));

    createMockMvcPerformImportsWithExpectedValidationFailed(shopUnitItemsRequest);
  }

  @Test
  void testImportUnits_ShopUnitItemsRequestWithNullUpdateDateBadRequest() throws Exception {
    var firstImport = new ShopUnitImport(ShopUnitType.OFFER, "1", "first", null, 100L);

    var shopUnitItemsRequest = new ShopUnitImportRequest();
    shopUnitItemsRequest.setItems(Set.of(firstImport));

    createMockMvcPerformImportsWithExpectedValidationFailed(shopUnitItemsRequest);
  }

  @Test
  void testImportUnits_OfferWithParentOfferInOneRequestBadRequest() throws Exception{
    var firstImport = new ShopUnitImport(ShopUnitType.OFFER, "1", "first", "2", 100L);
    var secondImport = new ShopUnitImport(ShopUnitType.OFFER, "2", "second", "1", 200L);

    var shopUnitItemsRequest = getRequestWithSet(Set.of(secondImport,firstImport));

    createMockMvcPerformImportsWithExpectedValidationFailed(shopUnitItemsRequest);
  }

  @Test
  void testImportUnits_CategoryWithPriceBadRequest() throws Exception {
    var firstImport = new ShopUnitImport(ShopUnitType.CATEGORY, "1", "first", null, 100L);
    var shopUnitItemsRequest = getRequestWithSet(Set.of(firstImport));

    createMockMvcPerformImportsWithExpectedValidationFailed(shopUnitItemsRequest);
  }

  @Test
  void testImportUnits_OfferWithLessZeroPriceBadRequest()  throws  Exception{
    var firstImport = new ShopUnitImport(ShopUnitType.OFFER, "1", "first", null, -100L);
    var shopUnitItemsRequest = getRequestWithSet(Set.of(firstImport));

    createMockMvcPerformImportsWithExpectedValidationFailed(shopUnitItemsRequest);
  }

  @Test
  void testImportUnits_UnitWithNullTypeBadRequest() throws Exception {
    var nullTypeImport = new ShopUnitImport(null, "1", "first", null, 100L);
    var shopUnitItemsRequest = getRequestWithSet(Set.of(nullTypeImport));

    createMockMvcPerformImportsWithExpectedValidationFailed(shopUnitItemsRequest);
  }

  @Test
  void testImportUnits_UnitWithNullIdBadRequest() throws Exception {
    var nullIdImport = new ShopUnitImport(ShopUnitType.OFFER, null, "first", null, 100L);
    var shopUnitItemsRequest = getRequestWithSet(Set.of(nullIdImport));

    createMockMvcPerformImportsWithExpectedValidationFailed(shopUnitItemsRequest);
  }

  @Test
  void testImportUnits_UnitWithNullNameBadRequest() throws Exception {
    var nullNameImport = new ShopUnitImport(ShopUnitType.OFFER, "1", null, null, 100L);
    var shopUnitItemsRequest = getRequestWithSet(Set.of(nullNameImport));

    createMockMvcPerformImportsWithExpectedValidationFailed(shopUnitItemsRequest);
  }
}
