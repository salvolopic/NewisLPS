package com.coffee.monitoring.rest;

import com.coffee.monitoring.entity.Distributore;
import com.coffee.monitoring.service.MonitoringService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

// REST resource per gestire il monitoring dei distributori
@Path("/monitoring")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MonitoringResource {

    private static final Logger LOGGER = Logger.getLogger(MonitoringResource.class.getName());

    @Inject
    private MonitoringService monitoringService;

    private LocalDateTime ultimoControlloGuasti = LocalDateTime.now();

    // Riceve heartbeat periodici dai distributori
    @POST
    @Path("/heartbeat/{id}")
    public Response riceviHeartbeat(@PathParam("id") String distributoreId) {
        LOGGER.info("Heartbeat ricevuto da distributore: " + distributoreId);
        monitoringService.registraHeartbeat(distributoreId);
        return Response.ok().build();
    }

    // Ritorna la lista distributori con coordinate per visualizzazione mappa
    @GET
    @Path("/mappa")
    public Response getMappa() {
        // Ricalcola stato guasti solo se è passato almeno 1 minuto
        LocalDateTime ora = LocalDateTime.now();
        if (ultimoControlloGuasti.plusMinutes(1).isBefore(ora)) {
            LOGGER.info("Ricalcolo distributori guasti...");
            monitoringService.controllaDistributoriGuasti();
            ultimoControlloGuasti = ora;
        }

        List<Distributore> distributori = monitoringService.getAllDistributori();
        return Response.ok(distributori).build();
    }

    // Aggiunge nuovo distributore
    @POST
    @Path("/distributori")
    public Response aggiungiDistributore(Distributore distributore) {
        Distributore nuovo = monitoringService.aggiungiDistributore(distributore);
        return Response.status(Response.Status.CREATED).entity(nuovo).build();
    }

    // Rimuove distributore
    @DELETE
    @Path("/distributori/{id}")
    public Response rimuoviDistributore(@PathParam("id") String id) {
        monitoringService.rimuoviDistributore(id);
        return Response.noContent().build();
    }

    // Imposta distributore in manutenzione
    @PUT
    @Path("/distributori/{id}/manutenzione")
    public Response mettiInManutenzione(@PathParam("id") String id) {
        Distributore distributore = monitoringService.mettiInManutenzione(id);
        if (distributore == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(distributore).build();
    }

    // Riattiva distributore dopo manutenzione
    @PUT
    @Path("/distributori/{id}/attiva")
    public Response attivaDistributore(@PathParam("id") String id) {
        Distributore distributore = monitoringService.attivaDistributore(id);
        if (distributore == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(distributore).build();
    }

    // Ottiene lista completa distributori
    @GET
    @Path("/distributori")
    public Response getAllDistributori() {
        List<Distributore> distributori = monitoringService.getAllDistributori();
        return Response.ok(distributori).build();
    }
}
