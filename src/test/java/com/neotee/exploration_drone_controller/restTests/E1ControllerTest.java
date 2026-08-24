package com.neotee.exploration_drone_controller.restTests;

import com.neotee.exploration_drone_controller.certification.ExplorationDroneControl;
import com.neotee.exploration_drone_controller.certification.PlanetExamining;
import com.neotee.exploration_drone_controller.config.TestContainersConfiguration;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CommandRequestDto;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.ExplorationDroneResponseDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfiguration.class)
public class E1ControllerTest {

    private UUID originId;

    private static final UUID northernNeighbour = UUID.randomUUID();

    @Autowired
    private ExplorationDroneControl explorationDroneControl;

    @Autowired
    private PlanetExamining planetExamining;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        originId = explorationDroneControl.resetAll();

        planetExamining.neighboursDetected(
                originId,
                northernNeighbour,
                null,
                null,
                null
        );
    }

    /**
     * Spawn a drone and then GET it.
     */
    @Test
    void spawnAndGetTest() throws Exception {

        MvcResult result = mockMvc.perform(
                        post("/api/v1/explorationDrones/spawn")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ExplorationDroneResponseDTO drone =
                objectMapper.readValue(
                        content,
                        ExplorationDroneResponseDTO.class
                );

        UUID droneId = drone.getId();

        mockMvc.perform(
                        get("/api/v1/explorationDrones/" + droneId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id",
                                Matchers.is(droneId.toString()))
                );
    }

    /**
     * Spawn five drones and check GET all.
     */
    @Test
    void spawnAndGetAllTest() throws Exception {

        int size = 5;

        for (int i = 0; i < size; i++) {
            mockMvc.perform(
                            post("/api/v1/explorationDrones/spawn")
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id",
                            Matchers.notNullValue()));
        }

        mockMvc.perform(
                        get("/api/v1/explorationDrones")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.size()", Matchers.is(size))
                );
    }

    /**
     * Spawn a drone and delete it.
     */
    @Test
    void spawnDeleteGetTest() throws Exception {

        MvcResult result = mockMvc.perform(
                        post("/api/v1/explorationDrones/spawn")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ExplorationDroneResponseDTO drone =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        ExplorationDroneResponseDTO.class
                );

        String droneUrl =
                "/api/v1/explorationDrones/" + drone.getId();

        // GET
        mockMvc.perform(
                        get(droneUrl)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        Matchers.is(drone.getId().toString())));

        // DELETE
        mockMvc.perform(
                        delete(droneUrl)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        // GET after DELETE
        mockMvc.perform(
                        get(droneUrl)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /**
     * Spawn a drone and send north command.
     */
    @Test
    void postCommandTest() throws Exception {

        MvcResult result = mockMvc.perform(
                        post("/api/v1/explorationDrones/spawn")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ExplorationDroneResponseDTO drone =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        ExplorationDroneResponseDTO.class
                );

        UUID droneId = drone.getId();

        CommandRequestDto command =
                new CommandRequestDto("north");

        String json =
                objectMapper.writeValueAsString(command);

        mockMvc.perform(
                        post("/api/v1/explorationDrones/"
                                + droneId
                                + "/commands")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        // Check new planet
        mockMvc.perform(
                        get("/api/v1/explorationDrones/" + droneId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id",
                                Matchers.is(droneId.toString()))
                )
                .andExpect(
                        jsonPath("$.planetId",
                                Matchers.is(
                                        northernNeighbour.toString()
                                ))
                );
    }

    /**
     * Send five commands and check command history.
     */
    @Test
    void postGetCommandHistoryTest() throws Exception {

        MvcResult result = mockMvc.perform(
                        post("/api/v1/explorationDrones/spawn")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ExplorationDroneResponseDTO drone =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        ExplorationDroneResponseDTO.class
                );

        UUID droneId = drone.getId();

        for (int i = 0; i < 5; i++) {

            String direction =
                    i % 2 == 0 ? "north" : "south";

            CommandRequestDto command =
                    new CommandRequestDto(direction);

            String json =
                    objectMapper.writeValueAsString(command);

            mockMvc.perform(
                            post("/api/v1/explorationDrones/"
                                    + droneId
                                    + "/commands")
                                    .content(json)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        mockMvc.perform(
                        get("/api/v1/explorationDrones/"
                                + droneId
                                + "/commands")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.size()",
                                Matchers.is(5))
                );
    }

    /**
     * Send five commands, then clear command history.
     */
    @Test
    void clearCommandHistoryTest() throws Exception {

        MvcResult result = mockMvc.perform(
                        post("/api/v1/explorationDrones/spawn")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ExplorationDroneResponseDTO drone =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        ExplorationDroneResponseDTO.class
                );

        UUID droneId = drone.getId();

        String droneUrl =
                "/api/v1/explorationDrones/" + droneId;

        for (int i = 0; i < 5; i++) {

            String direction =
                    i % 2 == 0 ? "north" : "south";

            CommandRequestDto command =
                    new CommandRequestDto(direction);

            String json =
                    objectMapper.writeValueAsString(command);

            mockMvc.perform(
                            post(droneUrl + "/commands")
                                    .content(json)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        // Check five commands
        mockMvc.perform(
                        get(droneUrl + "/commands")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.size()",
                                Matchers.is(5))
                );

        // Clear history
        mockMvc.perform(
                        delete(droneUrl + "/commands")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        // Check history is empty
        mockMvc.perform(
                        get(droneUrl + "/commands")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.size()",
                                Matchers.is(0))
                );
    }
}