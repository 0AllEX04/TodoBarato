package com.todobarato.pcl.todobarato.controller;

import com.todobarato.pcl.todobarato.dto.request.MarcaRequestDTO;
import com.todobarato.pcl.todobarato.exception.GlobalExceptionHandler;
import com.todobarato.pcl.todobarato.exception.ResourceNotFoundException;
import com.todobarato.pcl.todobarato.service.MarcaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MarcaControllerTest {
    private MarcaService marcaService;
    private MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        marcaService = mock(MarcaService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new MarcaController(marcaService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void actualizarMarcaInexistenteDevuelve404() throws Exception {
        when(marcaService.actualizar(eq(999), any(MarcaRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("No existe una marca con id: 999"));

        mockMvc.perform(put("/api/v1/marcas/999").contextPath("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Toyota\",\"estado\":true}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No existe una marca con id: 999"))
                .andExpect(jsonPath("$.path").value("/api/v1/marcas/999"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void eliminarMarcaInexistenteDevuelve404SinEliminar() throws Exception {
        when(marcaService.buscarPorId(999))
                .thenThrow(new ResourceNotFoundException("No existe una marca con id: 999"));

        mockMvc.perform(delete("/api/v1/marcas/999").contextPath("/api/v1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No existe una marca con id: 999"))
                .andExpect(jsonPath("$.path").value("/api/v1/marcas/999"));

        verify(marcaService, never()).eliminar(anyInt());
    }

    @Test
    void actualizarConNombreVacioMantiene400() throws Exception {
        mockMvc.perform(put("/api/v1/marcas/999").contextPath("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"estado\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.nombre").isNotEmpty());

        verify(marcaService, never()).actualizar(anyInt(), any(MarcaRequestDTO.class));
    }
}
