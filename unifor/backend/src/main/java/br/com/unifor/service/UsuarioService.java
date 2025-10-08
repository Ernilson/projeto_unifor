package br.com.unifor.service;

import br.com.unifor.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class UsuarioService {

    @Transactional
    public Usuario criarUsuario(Usuario novoUsuario) {
        novoUsuario.persist();
        return novoUsuario;
    }

    public List<Usuario> listarTodos() {
        return Usuario.listAll();
    }

    public Usuario buscarPorId(Long id) {
        return Usuario.findById(id);
    }

    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario dadosUsuario) {
        Usuario usuarioExistente = buscarPorId(id);
        if (usuarioExistente == null) {
            throw new IllegalArgumentException("Usuário com ID " + id + " não encontrado.");
        }
        usuarioExistente.setNome(dadosUsuario.getNome());
        usuarioExistente.setEmail(dadosUsuario.getEmail());
        usuarioExistente.setRole(dadosUsuario.getRole());
        usuarioExistente.persist();

        return usuarioExistente;
    }

    @Transactional
    public boolean deletarUsuario(Long id) {
        return Usuario.deleteById(id);
    }
}