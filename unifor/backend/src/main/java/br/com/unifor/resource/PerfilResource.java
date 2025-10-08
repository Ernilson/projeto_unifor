package br.com.unifor.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;

@Path("/me")
@Produces(MediaType.APPLICATION_JSON)
public class PerfilResource {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @PermitAll
    public UserProfile getMeuPerfil() {
        // Pega o ID único do usuário (geralmente o 'sub' do token JWT)
        String userId = securityIdentity.getPrincipal().getName();

        // Pega um atributo customizado do token (ex: 'name' ou 'email')
        String nome = securityIdentity.getAttribute("name");
        String email = securityIdentity.getAttribute("email");

        // Verifica se o usuário tem um determinado papel/role
        boolean isProfessor = securityIdentity.hasRole("PROFESSOR");

        // Agora você pode usar essas informações para buscar dados no banco,
        // montar um DTO de resposta, etc.
        return new UserProfile(userId, nome, email, isProfessor);
    }

    // Classe auxiliar para a resposta
    public static class UserProfile {
        public String id;
        public String nome;
        public String email;
        public boolean isProfessor;

        public UserProfile(String id, String nome, String email, boolean isProfessor) {
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.isProfessor = isProfessor;
        }
    }
}
