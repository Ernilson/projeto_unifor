package br.com.unifor.resource;

import br.com.unifor.entity.MatrizCurricular;
import br.com.unifor.entity.dto.MatrizMapper;
import br.com.unifor.entity.dto.MatrizResponseDTO;
import br.com.unifor.service.MatrizCurricularService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.util.List;

@Path("/matrizes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MatrizCurricularResource {

    @Inject
    MatrizCurricularService service;

    // ---------- DTOs simples ----------
    public static record MatrizCreateDTO(Long cursoId, Long semestreId, Boolean ativa) {}
    public static record MatrizUpdateDTO(Long cursoId, Long semestreId, Boolean ativa) {}

    // ---------- Endpoints ----------

    @GET
    @PermitAll
    public List<MatrizCurricular> list(@QueryParam("page") @DefaultValue("0") int page,
                                       @QueryParam("size") @DefaultValue("20") int size,
                                       @QueryParam("cursoId") Long cursoId,
                                       @QueryParam("semestreId") Long semestreId,
                                       @QueryParam("ativa") Boolean ativa) {
        return service.list(page, size, cursoId, semestreId, ativa);
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public MatrizCurricular get(@PathParam("id") Long id) {
        return service.findById(id).orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("/curso/{cursoId}/semestre/{semestreId}")
    @PermitAll
    public MatrizCurricular getByCursoSemestre(@PathParam("cursoId") Long cursoId,
                                               @PathParam("semestreId") Long semestreId) {
        return service.findByCursoSemestre(cursoId, semestreId).orElseThrow(NotFoundException::new);
    }

    @POST
    @PermitAll
    public Response create(MatrizCreateDTO dto, @Context UriInfo uri) {
        if (dto == null || dto.cursoId() == null || dto.semestreId() == null)
            throw new BadRequestException("cursoId e semestreId são obrigatórios");
        MatrizCurricular m = service.create(dto.cursoId(), dto.semestreId(), dto.ativa());
        URI location = uri.getAbsolutePathBuilder().path(String.valueOf(m.id)).build();
        return Response.created(location).entity(m).build();
    }

    @PUT
    @Path("/{id}")
    @PermitAll
    public MatrizResponseDTO update(@PathParam("id") Long id, MatrizUpdateDTO dto) {
        if (dto == null) throw new BadRequestException("body requerido");
        var matriz = service.update(id, dto.cursoId(), dto.semestreId(), dto.ativa());
        return MatrizMapper.toDTO(matriz);
    }


    @PATCH
    @Path("/{id}/ativa")
    @PermitAll
    public Response toggleAtiva(@PathParam("id") Long id, @QueryParam("value") boolean value) {
        service.setAtiva(id, value);
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
