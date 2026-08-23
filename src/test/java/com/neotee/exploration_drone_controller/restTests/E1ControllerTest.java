package com.neotee.exploration_drone_controller.restTests;


import com.neotee.exploration_drone_controller.certification.ExplorationDroneControl;
import com.neotee.exploration_drone_controller.certification.PlanetExamining;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CommandRequestDto;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.ExplorationDroneResponseDTO;
import com.neotee.exploration_drone_controller.explorationdrone.application.service.SpawnService;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class  E1ControllerTest {

    private UUID originId;
    private static final UUID northernNeighbour = UUID.randomUUID();

    @Autowired
    private ExplorationDroneControl explorationDroneControl;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanetExamining planetExamining;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        originId = explorationDroneControl.resetAll();
        planetExamining.neighboursDetected(originId, northernNeighbour, null, null, null);
    }

    private ExplorationDroneResponseDTO getRandomExplorationDroneDTO(){
        int generation=new Random().nextInt(117);
        String name = "Herbert der "+generation+".";
        ExplorationDroneResponseDTO botDTO=new ExplorationDroneResponseDTO(name, null, null, null);
        return botDTO;
    }

    @Transactional
    @Test
    public void postAndGetTest() throws Exception{

        ExplorationDroneResponseDTO botDTO=getRandomExplorationDroneDTO();
        var jsonString = objectMapper.writeValueAsString(botDTO);

        //POST new Entity
        MvcResult result = mockMvc.perform(post("/explorationDrones")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.is(botDTO.getName()))).andReturn();
        String content = result.getResponse().getContentAsString();
        String location = result.getResponse().getHeader("Location");
        ExplorationDroneResponseDTO resultDTO = objectMapper.readValue(content, ExplorationDroneResponseDTO.class);

        //GET the Posted Entity using the location-Header
        mockMvc.perform(get(location)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(resultDTO.getId().toString())))
                .andExpect(jsonPath("$.name", Matchers.is(botDTO.getName())));

    }

    @Transactional
    @Test
    public void postAndGetAllTest() throws Exception{
        int size=5;
        //POST 5 new Entities
        for(int i=0;i<size;i++) {
            ExplorationDroneResponseDTO botDTO = getRandomExplorationDroneDTO();
            var jsonString = objectMapper.writeValueAsString(botDTO);
            MvcResult result = mockMvc.perform(post("/explorationDrones")
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", Matchers.is(botDTO.getName()))).andReturn();
        }
        //GET all Entites
        mockMvc.perform(get("/explorationDrones")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(size)));
    }

    @Transactional
    @Test
    public void postPatchGetTest() throws Exception{

        ExplorationDroneResponseDTO botDTO=getRandomExplorationDroneDTO();
        var jsonString = objectMapper.writeValueAsString(botDTO);

        //POST new Entity
        MvcResult result = mockMvc.perform(post("/explorationDrones")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.is(botDTO.getName()))).andReturn();
        String content = result.getResponse().getContentAsString();
        String location = result.getResponse().getHeader("Location");
        ExplorationDroneResponseDTO resultDTO = objectMapper.readValue(content, ExplorationDroneResponseDTO.class);

        ExplorationDroneResponseDTO newBotDTO=getRandomExplorationDroneDTO();
        var newJsonString = objectMapper.writeValueAsString(newBotDTO);

        //PATCH the posted entity
        mockMvc.perform(patch(location)
                        .content(newJsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(newBotDTO.getName()))).andReturn();

        //GET the Patched Entity
        mockMvc.perform(get(location)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(resultDTO.getId().toString())))
                .andExpect(jsonPath("$.name", Matchers.is(newBotDTO.getName())));

    }

    @Transactional
    @Test
    public void postDeleteGetTest() throws Exception{

        ExplorationDroneResponseDTO botDTO=getRandomExplorationDroneDTO();
        var jsonString = objectMapper.writeValueAsString(botDTO);

        //POST new Entity
        MvcResult result = mockMvc.perform(post("/explorationDrones")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.is(botDTO.getName()))).andReturn();
        String content = result.getResponse().getContentAsString();
        String location = result.getResponse().getHeader("Location");
        ExplorationDroneResponseDTO resultDTO = objectMapper.readValue(content, ExplorationDroneResponseDTO.class);

        //GET the Posted Entity
        mockMvc.perform(get(location)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(resultDTO.getId().toString())))
                .andExpect(jsonPath("$.name", Matchers.is(botDTO.getName())));

        //DELETE the Posted Entity
        mockMvc.perform(delete(location)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        //Try to GET the deleted Entity
        mockMvc.perform(get(location)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Autowired
    private PlanetService planetService;
    @Autowired
    private SpawnService explorationDroneService;

    @Test
    @Transactional
    public void postCommandTest() throws Exception {

        ExplorationDroneResponseDTO botDTO=getRandomExplorationDroneDTO();
        var jsonString = objectMapper.writeValueAsString(botDTO);

        //POST new Entity
        MvcResult result = mockMvc.perform(post("/explorationDrones")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.is(botDTO.getName()))).andReturn();
        String content = result.getResponse().getContentAsString();
        String location = result.getResponse().getHeader("Location");
        ExplorationDroneResponseDTO resultDTO = objectMapper.readValue(content, ExplorationDroneResponseDTO.class);

        //Give Command to move north
        String direction="north";
        CommandRequestDto commandDTO = new CommandRequestDto(direction, resultDTO.getId());
        jsonString = objectMapper.writeValueAsString(commandDTO);

        List<Planet> planets = planetService.getAllPlanet();

        mockMvc.perform(post(location+"/commands")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        //GET the Updated Entity
        mockMvc.perform(get(location)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(resultDTO.getId().toString())))
                .andExpect(jsonPath("$.name", Matchers.is(botDTO.getName())))
                .andExpect(jsonPath("$.planetId", Matchers.is(northernNeighbour.toString())));

    }

    @Test
    @Transactional
    public void postGetCommandHistoryTest() throws Exception {

        ExplorationDroneResponseDTO botDTO=getRandomExplorationDroneDTO();
        var jsonString = objectMapper.writeValueAsString(botDTO);

        //POST new Entity
        MvcResult result = mockMvc.perform(post("/explorationDrones")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.is(botDTO.getName()))).andReturn();
        String content = result.getResponse().getContentAsString();
        String location = result.getResponse().getHeader("Location");
        ExplorationDroneResponseDTO resultDTO = objectMapper.readValue(content, ExplorationDroneResponseDTO.class);

        //Give 5 commands
        for (int i=0; i<5; i++) {
            String direction = i%2==0? "north" : "south";
            CommandRequestDto commandDTO = new CommandRequestDto(direction, resultDTO.getId());
            jsonString = objectMapper.writeValueAsString(commandDTO);

            mockMvc.perform(post(location + "/commands")
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }

        //Check if there are 5 commands
        mockMvc.perform(get(location + "/commands")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(5)));

    }


    @Test
    @Transactional
    public void clearCommandHistoryTest() throws Exception {

        ExplorationDroneResponseDTO botDTO=getRandomExplorationDroneDTO();
        var jsonString = objectMapper.writeValueAsString(botDTO);

        //POST new Entity
        MvcResult result = mockMvc.perform(post("/explorationDrones")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.is(botDTO.getName()))).andReturn();
        String content = result.getResponse().getContentAsString();
        String location = result.getResponse().getHeader("Location");
        ExplorationDroneResponseDTO resultDTO = objectMapper.readValue(content, ExplorationDroneResponseDTO.class);

        //Give 5 commands
        for (int i=0; i<5; i++) {
            String direction = i%2==0? "north" : "south";
            CommandRequestDto commandDTO = new CommandRequestDto(direction, resultDTO.getId());
            jsonString = objectMapper.writeValueAsString(commandDTO);

            mockMvc.perform(post("/explorationDrones/" + resultDTO.getId() + "/commands")
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }

        //Check if there are 5 commands
        mockMvc.perform(get(location + "/commands")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(5)));

        //perform DELETE on command history
        mockMvc.perform(delete(location + "/commands")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        //Check if there are no more commands
        mockMvc.perform(get(location+ "/commands")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(0)));
    }


}
