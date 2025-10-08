package br.com.unifor.resource;


import br.com.unifor.entity.Disciplina;
import br.com.unifor.service.DisciplinaService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.util.List;

@Path("/disciplinas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DisciplinaResource {

    @Inject
    DisciplinaService service;

    @GET
    @PermitAll
    public List<Disciplina> list(@QueryParam("page") @DefaultValue("0") int page,
                                 @QueryParam("size") @DefaultValue("20") int size,
                                 @QueryParam("q") String q) {
        return service.list(page, size, q);
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Disciplina getById(@PathParam("id") Long id) {
        return service.findById(id).orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("/by-codigo/{codigo}")
    @PermitAll
    public Disciplina getByCodigo(@PathParam("codigo") String codigo) {
        return service.findByCodigo(codigo).orElseThrow(NotFoundException::new);
    }

    @POST
    @PermitAll // troque para @RolesAllowed quando ativar auth
    public Response create(@Valid Disciplina in, @Context UriInfo uriInfo) {
        Disciplina saved = service.create(in);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(saved.id)).build();
        return Response.created(uri).entity(saved).build();
    }

    @PUT
    @Path("/{id}")
    @PermitAll
    public Disciplina update(@PathParam("id") Long id, @Valid Disciplina in) {
        return service.update(id, in);
    }

    @DELETE
    @Path("/{id}")
    @PermitAll
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}

