package stenahe.java_sql_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import stenahe.java_sql_api.run.Location;
import stenahe.java_sql_api.run.Run;
import stenahe.java_sql_api.run.RunController;
import stenahe.java_sql_api.run.RunRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RunController.class)
class RunControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RunRepository runRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_ReturnsListOfRuns() throws Exception {
        Run run1 = new Run(1, "Morning Run", LocalDateTime.now().minusHours(2), LocalDateTime.now(), 5, Location.INDOOR, 1);
        Run run2 = new Run(2, "Evening Jog", LocalDateTime.now().minusHours(4), LocalDateTime.now(), 3, Location.OUTDOOR, 1);

        when(runRepository.findAll()).thenReturn(Arrays.asList(run1, run2));

        mockMvc.perform(get("/api/runs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Morning Run"))
                .andExpect(jsonPath("$[1].title").value("Evening Jog"));
    }

    @Test
    void findById_ReturnsRun() throws Exception {
        Run run = new Run(1, "Morning Run", LocalDateTime.now().minusHours(2), LocalDateTime.now(), 5, Location.INDOOR, 1);

        when(runRepository.findById(1)).thenReturn(Optional.of(run));

        mockMvc.perform(get("/api/runs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Morning Run"));
    }

    @Test
    void findById_NotFound_Returns404() throws Exception {
        when(runRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/runs/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByLocation_ReturnsRuns() throws Exception {
        Run run1 = new Run(1, "Run 1", LocalDateTime.now().minusHours(2), LocalDateTime.now(), 5, Location.INDOOR, 1);
        Run run2 = new Run(2, "Run 2", LocalDateTime.now().minusHours(4), LocalDateTime.now(), 3, Location.INDOOR, 1);

        when(runRepository.findAllByLocation("INDOOR")).thenReturn(Arrays.asList(run1, run2));

        mockMvc.perform(get("/api/runs/location/INDOOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].location").value("INDOOR"))
                .andExpect(jsonPath("$[1].location").value("INDOOR"));
    }

    @Test
    void create_Success() throws Exception {
        Run run = new Run(1, "Morning Run", LocalDateTime.now().minusHours(2), LocalDateTime.now(), 5, Location.OUTDOOR, null);

        when(runRepository.save(Mockito.any(Run.class))).thenReturn(run);

        mockMvc.perform(post("/api/runs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(run)))
                .andExpect(status().isCreated());
    }

    @Test
    void update_RunExists_UpdatesSuccessfully() throws Exception {
        Run existingRun = new Run(1, "Morning Run", LocalDateTime.now().minusHours(2), LocalDateTime.now(), 5, Location.INDOOR, 1);
        Run updatedRun = new Run(1, "Updated Run", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(1), 7, Location.OUTDOOR, 1);

        when(runRepository.findById(1)).thenReturn(Optional.of(existingRun));
        when(runRepository.save(any(Run.class))).thenReturn(updatedRun);

        mockMvc.perform(put("/api/runs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRun)))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_RunNotFound_ThrowsException() throws Exception {
        when(runRepository.findById(1)).thenReturn(Optional.empty());

        Run updatedRun = new Run(1, "Updated Run", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(1), 7, Location.OUTDOOR, 1);

        mockMvc.perform(put("/api/runs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRun)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_RunExists_DeletesSuccessfully() throws Exception {
        Run run = new Run(1, "Morning Run", LocalDateTime.now().minusHours(2), LocalDateTime.now(), 5, Location.OUTDOOR, 1);

        when(runRepository.findById(1)).thenReturn(Optional.of(run));
        doNothing().when(runRepository).delete(run);

        mockMvc.perform(delete("/api/runs/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_RunNotFound_ThrowsException() throws Exception {
        when(runRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/runs/1"))
                .andExpect(status().isNotFound());
    }
}