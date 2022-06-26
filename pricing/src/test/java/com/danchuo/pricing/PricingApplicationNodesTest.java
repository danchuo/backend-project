package com.danchuo.pricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.danchuo.pricing.entity.ShopUnit;
import com.danchuo.pricing.repository.ShopUnitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class PricingApplicationNodesTest {

    private static final String VALIDATION_FAILED =
            """
                    {"code": 400, "message": "Validation Failed"}""";

    private static final String ITEM_NOT_FOUND = """
                    {"code": 404, "message": "Item not found"}""";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopUnitRepository shopUnitRepository;

    @Test
    @Sql(value = "/create-shopunit-root-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testNodes_GetSingleShopUnitRequest() throws Exception {
        var id = "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1";

        var shopUnit = shopUnitRepository.findById(id);

        var response = mockMvc
                .perform(get("/nodes/" + id))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ShopUnit receivedShopUnit =
            new ObjectMapper().findAndRegisterModules().readValue(response.getResponse().getContentAsString(), ShopUnit.class);

        assertEquals(shopUnit.get().getId(), receivedShopUnit.getId());
    }

    @Test
    void testNodes_GetNonExistentItem() throws Exception{
        var id = "non-existent";
        mockMvc
            .perform(get("/nodes/" + id))
            .andExpect(status().isNotFound())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json(ITEM_NOT_FOUND));
    }

    @Test
    @Sql(value = "/fill-shopunit-table-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testNodes_GetTreeWithCorrectPrices() throws Exception {
        var rootId = "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1";
        var expectedPriceOfRoot = 58599L;

        var response = mockMvc
            .perform(get("/nodes/" + rootId))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        ShopUnit receivedShopUnit =
            new ObjectMapper().findAndRegisterModules().readValue(response.getResponse().getContentAsString(), ShopUnit.class);

        assertFalse(receivedShopUnit.getChildren().isEmpty());
        assertEquals((long) receivedShopUnit.getPrice(), expectedPriceOfRoot);
    }

}
