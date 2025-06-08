package madstodolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AcercaDeController.class)
public class AcercaDeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAcercaDePage() throws Exception {
        mockMvc.perform(get("/acerca-de"))
                .andExpect(status().isOk())
                .andExpect(view().name("acerca-de"));
    }
} 