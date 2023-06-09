package com.example.library.controller;

import com.example.library.controller.Implement.AuthorControllerImpl;
import com.example.library.service.Implement.AuthorServiceImpl;
import org.junit.jupiter.api.Test;

import com.example.library.entity.Author;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthorControllerImplTest {
    private MockMvc mockMvc;
    @Mock
    private AuthorServiceImpl authorServiceImpl;
    @InjectMocks
    private AuthorControllerImpl authorControllerImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorControllerImpl = new AuthorControllerImpl(authorServiceImpl);
        mockMvc = MockMvcBuilders.standaloneSetup(authorControllerImpl).build();
    }

    @Test
    void testGetAllAuthors() throws Exception {
        // Prepare
        List<Author> authors = new ArrayList<>();
        authors.add(new Author(1L, "John Doe"));
        authors.add(new Author(2L, "Jane Smith"));
        when(authorServiceImpl.getAll()).thenReturn(authors);

        // Execute and Verify
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));
        verify(authorServiceImpl, times(1)).getAll();
    }

    @Test
    void testGetAuthorById_ExistingId() throws Exception {
        // Prepare
        long authorId = 1;
        Author author = new Author(authorId, "John Doe");
        when(authorServiceImpl.findById(authorId)).thenReturn(Optional.of(author));

        // Execute and Verify
        mockMvc.perform(get("/api/authors/{id}", authorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(authorId))
                .andExpect(jsonPath("$.name").value("John Doe"));
        verify(authorServiceImpl, times(1)).findById(authorId);
    }

    @Test
    void testGetAuthorById_NonExistingId() throws Exception {
        // Prepare
        long authorId = 100;
        when(authorServiceImpl.findById(authorId)).thenReturn(Optional.empty());

        // Execute and Verify
        mockMvc.perform(get("/api/authors/{id}", authorId))
                .andExpect(status().isNotFound());
        verify(authorServiceImpl, times(1)).findById(authorId);
    }

    @Test
    void testCreateAuthor() throws Exception {
        // Prepare
        Author author = new Author(null, "John Doe");
        Author createdAuthor = new Author(1L, "John Doe");

        when(authorServiceImpl.save(any(Author.class))).thenReturn(createdAuthor);

        // Execute and Verify
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));
        verify(authorServiceImpl, times(1)).save(any(Author.class));
    }

    @Test
    void testDeleteAuthor_ExistingId() throws Exception {
        // Prepare
        long authorId = 1;

        // Execute and Verify
        mockMvc.perform(delete("/api/authors/{id}", authorId))
                .andExpect(status().isNoContent());
        verify(authorServiceImpl, times(1)).deleteById(authorId);
    }

    @Test
    void testDeleteAuthor_NonExistingId() throws Exception {
        // Prepare
        long authorId = 100;
        doThrow(IllegalArgumentException.class).when(authorServiceImpl).deleteById(authorId);

        // Execute and Verify
        mockMvc.perform(delete("/api/authors/{id}", authorId))
                .andExpect(status().isInternalServerError());
        verify(authorServiceImpl, times(1)).deleteById(authorId);
    }
}
