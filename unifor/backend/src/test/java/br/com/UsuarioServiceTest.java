package br.com;

import br.com.unifor.entity.Usuario; // Importe sua entidade Usuario
import br.com.unifor.service.UsuarioService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

@QuarkusTest
class UsuarioServiceTest {

    @Inject
    UsuarioService service;

    /**
     * Este método é executado antes de cada @Test.
     * A anotação @Transactional garante que a limpeza do banco
     * seja efetivada antes do próximo teste começar.
     */
    @BeforeEach
    @Transactional
    void setUp() {
        Usuario.deleteAll();
    }

    @Test
    @Transactional
    void deveCriarUsuarioComSucesso() {
        // Arrange (Preparação)
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome("Emilson");
        novoUsuario.setEmail("emilson@teste.com");
        novoUsuario.setRole(null);

        // Act (Ação)
        Usuario usuarioCriado = service.criarUsuario(novoUsuario);

        // Assert (Verificação)
        Assertions.assertNotNull(usuarioCriado);
        Assertions.assertNotNull(usuarioCriado.id, "O ID não deveria ser nulo após persistir.");
        Assertions.assertEquals("Emilson", usuarioCriado.getNome());
    }

    @Test
    @Transactional
    void deveListarTodosOsUsuarios() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setNome("Usuario Um");
        usuario1.setEmail("um@teste.com");
        usuario1.setRole(null);
        service.criarUsuario(usuario1);

        Usuario usuario2 = new Usuario();
        usuario2.setNome("Usuario Dois");
        usuario2.setEmail("dois@teste.com");
        usuario2.setRole(null);
        service.criarUsuario(usuario2);

        // Act
        List<Usuario> usuarios = service.listarTodos();

        // Assert
        Assertions.assertNotNull(usuarios);
        Assertions.assertEquals(2, usuarios.size(), "A lista deveria conter 2 usuários.");
    }

    @Test
    @Transactional
    void deveBuscarUsuarioPorIdComSucesso() {
        // Arrange
        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setNome("Buscar Teste");
        usuarioSalvo.setEmail("buscar@teste.com");
        usuarioSalvo.setRole(null);
        service.criarUsuario(usuarioSalvo);

        // Act
        Usuario usuarioEncontrado = service.buscarPorId(usuarioSalvo.id);

        // Assert
        Assertions.assertNotNull(usuarioEncontrado);
        Assertions.assertEquals(usuarioSalvo.id, usuarioEncontrado.id);
        Assertions.assertEquals("Buscar Teste", usuarioEncontrado.getNome());
    }

    @Test
    @Transactional
    void deveAtualizarUsuarioComSucesso() {
        // Arrange
        Usuario usuarioOriginal = new Usuario();
        usuarioOriginal.setNome("Nome Antigo");
        usuarioOriginal.setEmail("email@antigo.com");
        usuarioOriginal.setRole(null);
        service.criarUsuario(usuarioOriginal);

        Usuario dadosParaAtualizar = new Usuario();
        dadosParaAtualizar.setNome("Nome Novo");
        dadosParaAtualizar.setEmail("email@novo.com");
        dadosParaAtualizar.setRole(null);

        // Act
        Usuario usuarioAtualizado = service.atualizarUsuario(usuarioOriginal.id, dadosParaAtualizar);

        // Assert
        Assertions.assertNotNull(usuarioAtualizado);
        Assertions.assertEquals("Nome Novo", usuarioAtualizado.getNome());
        Assertions.assertEquals("email@novo.com", usuarioAtualizado.getEmail());
        //Assertions.assertEquals("admin", usuarioAtualizado.getRole());
    }

    @Test
    @Transactional
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        // Arrange
        Long idInexistente = 999L;
        Usuario dadosParaAtualizar = new Usuario();
        dadosParaAtualizar.setNome("Qualquer Nome");

        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.atualizarUsuario(idInexistente, dadosParaAtualizar),
                "Deveria lançar IllegalArgumentException para um ID inexistente."
        );

        Assertions.assertEquals("Usuário com ID " + idInexistente + " não encontrado.", exception.getMessage());
    }

    @Test
    @Transactional
    void deveDeletarUsuarioComSucesso() {
        // Arrange
        Usuario usuarioParaDeletar = new Usuario();
        usuarioParaDeletar.setNome("Para Deletar");
        usuarioParaDeletar.setEmail("deletar@teste.com");
        usuarioParaDeletar.setRole(null);
        service.criarUsuario(usuarioParaDeletar);

        // Act
        boolean deletado = service.deletarUsuario(usuarioParaDeletar.id);

        // Assert
        Assertions.assertTrue(deletado, "O método deveria retornar true.");
        Usuario usuarioDeletado = service.buscarPorId(usuarioParaDeletar.id);
        Assertions.assertNull(usuarioDeletado, "O usuário não deveria mais ser encontrado no banco.");
    }
}