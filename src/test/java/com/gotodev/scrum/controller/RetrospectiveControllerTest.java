package com.gotodev.scrum.controller;

import com.gotodev.scrum.model.Feedback;
import com.gotodev.scrum.model.Retrospective;
import com.gotodev.scrum.repository.RetrospectiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


@ExtendWith(SpringExtension.class)
@WebMvcTest(value = RetrospectiveController.class)
class RetrospectiveControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetrospectiveRepository retrospectiveRepository;

    Retrospective mockRetro = new Retrospective();
    Retrospective mockRetroWithFeedback = new Retrospective();
    Page<Retrospective> mockPageRetro = (Page<Retrospective>) mock(Page.class);

    @BeforeEach
    void setUp() {
        mockRetro.setDate(LocalDate.now());
        mockRetro.setName("name");
        mockRetro.setSummary("summary");
        mockRetro.setParticipants(new String[]{"Ben"});
        mockRetro.setFeedback(new ArrayList<>());


        Feedback feedback = new Feedback();
        feedback.setId(1);
        feedback.setName("name");
        feedback.setBody("body");
        feedback.setType("type");
        mockRetroWithFeedback.setDate(LocalDate.now());
        mockRetroWithFeedback.setName("name");
        mockRetroWithFeedback.setSummary("summary");
        mockRetroWithFeedback.setParticipants(new String[]{"Ben"});
        mockRetroWithFeedback.setFeedback(Collections.singletonList(feedback));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void whenPostingARetro_shouldReturnASuccess() throws Exception {

        Mockito.when(retrospectiveRepository.save(Mockito.any())).thenReturn(mockRetro);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/retro")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\":\"name\",\n" +
                        "  \"summary\":\"summary\",\n" +
                        "  \"date\":\"2023-10-10\",\n" +
                        "  \"participants\":[\"one\", \"two\", \"three\"]\n" +
                        "}");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals("success", result.getResponse().getContentAsString());
    }


    @Test
    void whenPostingARetro_withoutDate_shouldReturnAnError() throws Exception {

        Mockito.when(retrospectiveRepository.save(Mockito.any())).thenReturn(mockRetro);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/retro")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\":\"name\",\n" +
                        "  \"summary\":\"summary\",\n" +
                        "}");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    void whenGettingPagedRestros_withPageSize_shouldReturnSuccessfully() throws Exception {
        Mockito.when(retrospectiveRepository.findAll((Pageable) Mockito.any())).thenReturn(mockPageRetro);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/retro")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("currentPage", "0")
                .param("pageSize", "3");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    void whenGettingPagedRestros_withDate_shouldReturnSuccessfully() throws Exception {
        Mockito.when(retrospectiveRepository.findByDate(Mockito.any())).thenReturn(Collections.singletonList(mockRetro));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/retro/date")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("filter", "2022-11-11");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals("[{\"name\":\"name\",\"summary\":\"summary\",\"date\":\"2023-09-27\",\"participants\":[\"Ben\"],\"feedback\":[]}]", result.getResponse().getContentAsString());
    }

    @Test
    void whenAddingFeedbackToRetro_shouldBeSuccessful() throws Exception {
        Mockito.when(retrospectiveRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(mockRetro));
        Mockito.when(retrospectiveRepository.save(Mockito.any())).thenReturn(mockRetro);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("http://localhost:8080/retro/name/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"Ben\",\n" +
                        "    \"body\": \"Ipsum lorem\",\n" +
                        "    \"type\": \"good\"\n" +
                        "}");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(201, result.getResponse().getStatus());
    }

    @Test
    void whenAddingFeedbackToRetro_withNoRetro_shouldBe204() throws Exception {
        Mockito.when(retrospectiveRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(retrospectiveRepository.save(Mockito.any())).thenReturn(mockRetro);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("http://localhost:8080/retro/name/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"Ben\",\n" +
                        "    \"body\": \"Ipsum lorem\",\n" +
                        "    \"type\": \"good\"\n" +
                        "}");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(204, result.getResponse().getStatus());
    }

    @Test
    void whenUpdatingRetroFeedback_shouldBeSuccessful() throws Exception {

        Mockito.when(retrospectiveRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(mockRetroWithFeedback));
        Mockito.when(retrospectiveRepository.save(Mockito.any())).thenReturn(mockRetro);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("http://localhost:8080/retro/name/feedback/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"Ben\",\n" +
                        "    \"body\": \"Ipsum lorem\",\n" +
                        "    \"type\": \"good\"\n" +
                        "}");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(201, result.getResponse().getStatus());
    }

    @Test
    void whenUpdatingFeedbackToRetro_withNoRetro_shouldBe204() throws Exception {

        Mockito.when(retrospectiveRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(retrospectiveRepository.save(Mockito.any())).thenReturn(mockRetro);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("http://localhost:8080/retro/name/feedback/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"Ben\",\n" +
                        "    \"body\": \"Ipsum lorem\",\n" +
                        "    \"type\": \"good\"\n" +
                        "}");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(204, result.getResponse().getStatus());
    }

    @Test
    void whenUpdatingFeedbackToRetro_withNoFeedback_shouldBe204() throws Exception {

        Mockito.when(retrospectiveRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(mockRetroWithFeedback));
        Mockito.when(retrospectiveRepository.save(Mockito.any())).thenReturn(mockRetro);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("http://localhost:8080/retro/name/feedback/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"Ben\",\n" +
                        "    \"body\": \"Ipsum lorem\",\n" +
                        "    \"type\": \"good\"\n" +
                        "}");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(204, result.getResponse().getStatus());
    }
}