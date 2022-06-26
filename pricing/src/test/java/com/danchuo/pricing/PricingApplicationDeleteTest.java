package com.danchuo.pricing;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.danchuo.pricing.repository.ShopUnitRepository;
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
@Sql(
    value = "/clear-shopunit-table-after.sql",
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PricingApplicationDeleteTest {

  private static final String ITEM_NOT_FOUND = """
                    {"code": 404, "message": "Item not found"}""";


  private static final String DELETE_SUCCESS = """
          {"code": 200, "message": "The deleting was successful."}""";


  @Autowired private MockMvc mockMvc;

  @Autowired private ShopUnitRepository shopUnitRepository;



  @Test
  void testDelete_DeleteNonExistentItem() throws Exception{
    var id = "non-existent";
    mockMvc
        .perform(delete("/delete/" + id))
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.content().json(ITEM_NOT_FOUND));
  }

  @Test
  @Sql(
      value = "/fill-shopunit-table-before.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void testDelete_GetSingleShopUnitRequest() throws Exception {
    var rootId = "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1";

    mockMvc
        .perform(delete("/delete/" + rootId))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.content().json(DELETE_SUCCESS));

    var root = shopUnitRepository.findById(rootId);

    assertTrue(root.isEmpty());

    var findAll = shopUnitRepository.findAll();

    assertTrue(findAll.isEmpty());
  }
}
