package br.com.unifor.resource;

import br.com.unifor.entity.MatrizItem;
import br.com.unifor.service.MatrizItemService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("/matriz-itens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MatrizItemResource {

    @Inject
    MatrizItemService service;

    // DTOs simples
    public static record CreateDTO(Long matrizId, Long disciplinaId, Integer ordem) {}
    public static record UpdateDTO(Long disciplinaId, Integer ordem) {}
    public static record BulkDTO(Long matrizId, List<Long> disciplinas, Boolean replace) {}

    @GET
    @PermitAll
    public List<MatrizItem> listByMatriz(@QueryParam("matrizId") Long matrizId) {
        if (matrizId == null) throw new BadRequestException("Informe matrizId");
        return service.listByMatriz(matrizId);
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public MatrizItem get(@PathParam("id") Long id) {
        return service.findById(id).orElseThrow(NotFoundException::new);
    }

    @POST
    @PermitAll
    public Response create(CreateDTO dto, @jakarta.ws.rs.core.Context UriInfo uri) {
        if (dto == null || dto.matrizId() == null || dto.disciplinaId() == null)
            throw new BadRequestException("matrizId e disciplinaId são obrigatórios");
        MatrizItem mi = service.create(dto.matrizId(), dto.disciplinaId(), dto.ordem());
        URI location = uri.getAbsolutePathBuilder().path(String.valueOf(mi.id)).build();
        return Response.created(location).entity(mi).build();
    }

    @POST
    @Path("/bulk")
    @PermitAll
    public List<MatrizItem> bulk(BulkDTO dto) {
        if (dto == null || dto.matrizId() == null || dto.disciplinas() == null)
            throw new BadRequestException("matrizId e lista de disciplinas são obrigatórios");
        boolean replace = dto.replace() != null && dto.replace();
        return service.bulk(dto.matrizId(), dto.disciplinas(), replace);
    }

    @PUT
    @Path("/{id}")
    @PermitAll
    public MatrizItem update(@PathParam("id") Long id, UpdateDTO dto) {
        if (dto == null) throw new BadRequestException("body requerido");
        return service.update(id, dto.disciplinaId(), dto.ordem());
    }

    @PATCH
    @Path("/{id}/move")
    @PermitAll
    public Response move(@PathParam("id") Long id, @QueryParam("ordem") @Min(1) int ordem) {
        service.move(id, ordem);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    @PermitAll
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
