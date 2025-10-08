package br.com.unifor.resource;

import br.com.unifor.entity.Usuario;
import br.com.unifor.service.UsuarioService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService usuarioService;

    @GET
    public List<Usuario> listarTodos() {
        return Usuario.listAll();
    }

    @POST
    public Response criar(Usuario usuario) {
        Usuario novoUsuario = usuarioService.criarUsuario(usuario);
        return Response.status(Response.Status.CREATED).entity(novoUsuario).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            return Response.ok(usuario).build(); // 200 OK
        }
        return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, @Valid Usuario usuario) {
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuario);
        if (usuarioAtualizado != null) {
            return Response.ok(usuarioAtualizado).build(); // 200 OK
        }
        return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id) {
        if (usuarioService.deletarUsuario(id)) {
            return Response.noContent().build(); // 204 No Content
        }
        return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
    }
}
