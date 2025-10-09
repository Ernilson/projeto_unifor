package br.com.unifor.resource;

import br.com.unifor.entity.Curso;
import br.com.unifor.service.CursoService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

@Path("/cursos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CursoResource {

    @Inject
    CursoService service;

    @GET
    @PermitAll
    public List<Curso> listAll() { return Curso.listAll(); }

    @GET
    @Path("/{id}")
    public Curso findById(@PathParam("id") Long id) {
        return Curso.findByIdOptional(id)
                .map(c -> (Curso) c)
                .orElseThrow(() -> new NotFoundException("Curso não encontrado com ID: " + id));
    }

    @POST
    @Transactional
    @PermitAll
    public Response create(@Valid Curso dto, @Context UriInfo uri) {
        dto.id = null; dto.persist();
        return Response.created(uri.getAbsolutePathBuilder().path(dto.id.toString()).build())
                .entity(dto).build();
    }

    @PUT
    @Path("/{id}") @Transactional
    @PermitAll
    public Curso update(@PathParam("id") Long id, @Valid Curso in) {
        Curso cursoExistente = Curso.<Curso>findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Curso não encontrado com ID: " + id));

        cursoExistente.nome = in.nome;
        cursoExistente.codigo = in.codigo;
        cursoExistente.ativo = in.ativo;

        return cursoExistente;
    }

    @DELETE
    @Path("/{id}")
    @PermitAll
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }

}

