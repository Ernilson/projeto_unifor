package br.com.unifor.resource;

import br.com.unifor.entity.DTO.MatrizDTO;
import br.com.unifor.service.MatrizService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/matrizes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MatrizResource {

    @Inject
    MatrizService service;

    /**
     * GET /api/matrizes
     * Lista todas as matrizes cadastradas.
     */
    @GET
    public List<MatrizDTO> listar() {
        return service.listarTodos();
    }

    /**
     * GET /api/matrizes/busca?cursoId=1&semestreId=2
     * Busca por curso e semestre (retorna 404 se não encontrar).
     */
    @GET
    @Path("/busca")
    public Response buscarPorCursoESemestre(@QueryParam("cursoId") Long cursoId,
                                            @QueryParam("semestreId") Long semestreId) {
        if (cursoId == null || semestreId == null) {
            throw new BadRequestException("Parâmetros cursoId e semestreId são obrigatórios");
        }

        return service.buscarPorCursoESemestre(cursoId, semestreId)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    /**
     * POST /api/matrizes
     * Body: { "cursoId": 1, "semestreId": 2, "ativa": true }
     * - Se já existir: 200 OK + DTO existente
     * - Se não existir: 201 Created + DTO criado
     */
    @POST
    public Response criarOuObter(MatrizDTO dto) {
        return service.criarOuObter(dto);
    }
}

