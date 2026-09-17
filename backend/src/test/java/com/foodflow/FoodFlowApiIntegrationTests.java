package com.foodflow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FoodFlowApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void profileCanBeCreatedAndUpdated() throws Exception {
        String created = """
                {"fullName":"Ubay Saifee","phoneNumber":"+919876543210","address":"Food Street",
                 "city":"Mumbai","postalCode":"400001","profileImageUrl":"https://example.com/avatar.png"}
                """;
        String updated = """
                {"fullName":"Ubay Saifee","phoneNumber":"+919876543210","address":"New Food Street",
                 "city":"Mumbai","postalCode":"400001","profileImageUrl":"https://example.com/avatar.png"}
                """;

        mockMvc.perform(post("/api/v1/users/101/profile").contentType(MediaType.APPLICATION_JSON).content(created))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.userId").value(101))
                .andExpect(jsonPath("$.fullName").value("Ubay Saifee"));

        mockMvc.perform(put("/api/v1/users/101/profile").contentType(MediaType.APPLICATION_JSON).content(updated))
                .andExpect(status().isOk()).andExpect(jsonPath("$.address").value("New Food Street"));
    }

    @Test
    void profileRejectsMissingNameAndDuplicateUserProfile() throws Exception {
        String validProfile = """
                {"fullName":"Test Customer","phoneNumber":"+919999999999","address":"Test Street"}
                """;

        mockMvc.perform(post("/api/v1/users/104/profile").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":\"+919999999999\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.fullName").exists());

        mockMvc.perform(post("/api/v1/users/104/profile").contentType(MediaType.APPLICATION_JSON).content(validProfile))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/users/104/profile").contentType(MediaType.APPLICATION_JSON).content(validProfile))
                .andExpect(status().isConflict());
    }

    @Test
    void favoriteCanBeAddedListedAndRemoved() throws Exception {
        mockMvc.perform(post("/api/v1/users/102/favorites").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"foodId\":501}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.foodId").value(501));

        mockMvc.perform(get("/api/v1/users/102/favorites"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].foodId").value(501));

        mockMvc.perform(delete("/api/v1/users/102/favorites/501"))
                .andExpect(status().isNoContent());
    }

    @Test
    void favoriteRejectsDuplicatesAndReturnsNotFoundForMissingFavorite() throws Exception {
        String favorite = "{\"foodId\":502}";

        mockMvc.perform(post("/api/v1/users/105/favorites").contentType(MediaType.APPLICATION_JSON).content(favorite))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/users/105/favorites").contentType(MediaType.APPLICATION_JSON).content(favorite))
                .andExpect(status().isConflict());

        mockMvc.perform(delete("/api/v1/users/105/favorites/503"))
                .andExpect(status().isNotFound());
    }

    @Test
    void restaurantReviewCanBeCreatedUpdatedAndSummarised() throws Exception {
        String review = """
                {"userId":103,"targetType":"RESTAURANT","targetId":201,"rating":4,"comment":"Very good food"}
                """;
        String update = """
                {"userId":103,"rating":5,"comment":"Excellent food"}
                """;

        MvcResult result = mockMvc.perform(post("/api/v1/reviews").contentType(MediaType.APPLICATION_JSON).content(review))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.rating").value(4)).andReturn();
        int reviewId = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(put("/api/v1/reviews/{reviewId}", reviewId).contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isOk()).andExpect(jsonPath("$.rating").value(5));

        mockMvc.perform(get("/api/v1/reviews/summary?targetType=RESTAURANT&targetId=201"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.reviewCount").value(1))
                .andExpect(jsonPath("$.averageRating").value(5.0));
    }

    @Test
    void foodReviewIsListedAndRejectsInvalidAndDuplicateRatings() throws Exception {
        String review = """
                {"userId":106,"targetType":"FOOD","targetId":601,"rating":3,"comment":"Good burger"}
                """;

        mockMvc.perform(post("/api/v1/reviews").contentType(MediaType.APPLICATION_JSON).content(review))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.targetType").value("FOOD"));

        mockMvc.perform(get("/api/v1/reviews?targetType=FOOD&targetId=601"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].comment").value("Good burger"));

        mockMvc.perform(post("/api/v1/reviews").contentType(MediaType.APPLICATION_JSON).content(review))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/v1/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":107,\"targetType\":\"FOOD\",\"targetId\":601,\"rating\":6}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.rating").exists());
    }

    @Test
    void reviewCannotBeChangedOrDeletedByAnotherUser() throws Exception {
        String review = """
                {"userId":108,"targetType":"RESTAURANT","targetId":202,"rating":4,"comment":"Nice service"}
                """;
        MvcResult result = mockMvc.perform(post("/api/v1/reviews").contentType(MediaType.APPLICATION_JSON).content(review))
                .andExpect(status().isCreated()).andReturn();
        int reviewId = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(put("/api/v1/reviews/{reviewId}", reviewId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":109,\"rating\":1,\"comment\":\"Not allowed\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/v1/reviews/{reviewId}", reviewId).queryParam("userId", "109"))
                .andExpect(status().isNotFound());
    }
}
