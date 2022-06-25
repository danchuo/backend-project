package com.danchuo.pricing;


import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopUnitRepository shopUnitRepository;

//    @Test
//    @Sql(value = "/create-shopunit-root-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    void testNodes_GetSingleShopUnitRequest() throws Exception {
//        var id = "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1";
//
//        var shopUnit = shopUnitRepository.findById(id);
//        //var shopUnitJson= new ObjectMapper().findAndRegisterModules().writeValueAsString(shopUnit);
//
//        var response =mockMvc
//                .perform(get("/nodes/"+id))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//                .andReturn();
//        //.andExpect(MockMvcResultMatchers.content().json(shopUnitJson));
//        ShopUnit receivedShopUnit = new ObjectMapper().findAndRegisterModules().readValue(response.getResponse().getContentAsString(), ShopUnit.class);
//
//        assertEquals(shopUnit.get(),receivedShopUnit);
//    }
}
