package br.com.unifor.resource;

import br.com.unifor.entity.Curso;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
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

    @GET
    @PermitAll
    public List<Curso> listAll() { return Curso.listAll(); }

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
        var c = Curso.findByIdOptional(id).orElseThrow(NotFoundException::new);
      //  c.nome = in.nome; c.codigo = in.codigo; c.ativo = in.ativo;
        return (Curso) c;
    }

    @DELETE @Path("/{id}") @Transactional
    @PermitAll
    public void delete(@PathParam("id") Long id) {
        Curso.deleteById(id);
    }

}

