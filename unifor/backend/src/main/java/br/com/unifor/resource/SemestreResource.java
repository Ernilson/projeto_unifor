package br.com.unifor.resource;

import br.com.unifor.entity.Semestre;
import br.com.unifor.service.SemestreService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.util.List;

@Path("/semestres")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SemestreResource {

    @Inject
    SemestreService service;

    @GET
    @PermitAll
    public List<Semestre> list(@QueryParam("page") @DefaultValue("0") int page,
                               @QueryParam("size") @DefaultValue("20") int size,
                               @QueryParam("ano") Integer ano,
                               @QueryParam("periodo") Integer periodo,
                               @QueryParam("ativo") Boolean ativo) {
        return service.list(page, size, ano, periodo, ativo);
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Semestre get(@PathParam("id") Long id) {
        return service.findById(id).orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("/by")
    @PermitAll
    public Semestre getByAnoPeriodo(@QueryParam("ano") Integer ano,
                                    @QueryParam("periodo") Integer periodo) {
        if (ano == null || periodo == null) throw new BadRequestException("ano e periodo são obrigatórios");
        return service.findByAnoPeriodo(ano, periodo).orElseThrow(NotFoundException::new);
    }

    @POST
    @PermitAll
    public Response create(@Valid Semestre in, @Context UriInfo uri) {
        Semestre s = service.create(in);
        URI location = uri.getAbsolutePathBuilder().path(String.valueOf(s.id)).build();
        return Response.created(location).entity(s).build();
    }

    @PUT
    @Path("/{id}")
    @PermitAll
    public Semestre update(@PathParam("id") Long id, @Valid Semestre in) {
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
