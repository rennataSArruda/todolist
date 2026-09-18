package br.com.rennataarruda.todolist.controller;

import br.com.rennataarruda.todolist.dto.auth.FirstAccessRequest;
import br.com.rennataarruda.todolist.service.PrimeiroAcessoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FirstAccessControllerTest {

    @Mock
    private PrimeiroAcessoService primeiroAcessoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FirstAccessController(primeiroAcessoService)).build();
    }

    @Test
    void shouldCreateUserWithoutReturningTokens() throws Exception {
        mockMvc.perform(post("/public/auth/first-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "maria",
                                  "email": "maria@email.com",
                                  "name": "Maria",
                                  "password": "senha123"
                                }
                                """))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(primeiroAcessoService).criar(
                new FirstAccessRequest("maria", "maria@email.com", "Maria", "senha123")
        );
    }
}
