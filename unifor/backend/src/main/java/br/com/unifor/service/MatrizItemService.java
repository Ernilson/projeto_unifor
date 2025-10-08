package br.com.unifor.service;

import br.com.unifor.entity.Disciplina;
import br.com.unifor.entity.MatrizCurricular;
import br.com.unifor.entity.MatrizItem;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MatrizItemService {

    public List<MatrizItem> listByMatriz(Long matrizId) {
        if (matrizId == null) throw new BadRequestException("matrizId é obrigatório");
        return MatrizItem.find("matriz.id = ?1 ORDER BY ordem ASC", matrizId).list();
    }

    public Optional<MatrizItem> findById(Long id) {
        return MatrizItem.findByIdOptional(id);
    }

    @Transactional
    public MatrizItem create(Long matrizId, Long disciplinaId, Integer ordem) {
        MatrizCurricular m = MatrizCurricular.findById(matrizId);
        Disciplina d = Disciplina.findById(disciplinaId);
        if (m == null || d == null) throw new NotFoundException("Matriz ou Disciplina não encontrada");

        // se não informou ordem, joga para o final
        if (ordem == null) {
            Integer max = MatrizItem.find(
                            "select max(m.ordem) from MatrizItem m where m.matriz.id = ?1",
                            matrizId
                    ).project(Integer.class)
                    .firstResult();
        }

        // respeita a unique (matriz, disciplina)
        Optional<MatrizItem> existente = MatrizItem
                .find("matriz.id=?1 and disciplina.id=?2", matrizId, disciplinaId)
                .firstResultOptional();
        if (existente.isPresent()) return existente.get();

        MatrizItem mi = new MatrizItem();
        mi.matriz = m;
        mi.disciplina = d;
        mi.ordem = ordem;
        mi.persist();
        return mi;
    }

    /**
     * Substitui itens da matriz pela lista informada (ordens 1..n).
     * Se replace=false, apenas adiciona novos itens ao final mantendo os atuais.
     */
    @Transactional
    public List<MatrizItem> bulk(Long matrizId, List<Long> disciplinaIds, boolean replace) {
        if (matrizId == null) throw new BadRequestException("matrizId é obrigatório");
        MatrizCurricular m = MatrizCurricular.findById(matrizId);
        if (m == null) throw new NotFoundException("Matriz não encontrada");

        if (replace) {
            MatrizItem.delete("matriz.id", matrizId);
        }

        int startOrder = 0;
        if (replace) {
            startOrder = 1;
        } else {
            Integer max = MatrizItem.find(
                            "select max(m.ordem) from MatrizItem m where m.matriz.id = ?1",
                            matrizId
                    ).project(Integer.class)
                    .firstResult();
        }

        List<MatrizItem> out = new ArrayList<>();
        int ordem = startOrder;
        for (Long did : disciplinaIds) {
            Disciplina d = Disciplina.findById(did);
            if (d == null) throw new NotFoundException("Disciplina id=" + did + " não encontrada");

            Optional<MatrizItem> existente = MatrizItem
                    .find("matriz.id=?1 and disciplina.id=?2", matrizId, did)
                    .firstResultOptional();
            if (existente.isPresent()) {
                // se não está substituindo, apenas mantém o existente
                out.add(existente.get());
                continue;
            }

            MatrizItem mi = new MatrizItem();
            mi.matriz = m;
            mi.disciplina = d;
            mi.ordem = ordem++;
            mi.persist();
            out.add(mi);
        }
        return out;
    }

    @Transactional
    public MatrizItem update(Long id, Long disciplinaId, Integer ordem) {
        MatrizItem mi = (MatrizItem) MatrizItem.findByIdOptional(id).orElseThrow(NotFoundException::new);

        if (disciplinaId != null && (mi.disciplina == null || !mi.disciplina.id.equals(disciplinaId))) {
            Disciplina d = Disciplina.findById(disciplinaId);
            if (d == null) throw new NotFoundException("Disciplina não encontrada");
            // garantir unique (matriz, disciplina)
            Optional<MatrizItem> dup = MatrizItem.find("matriz.id=?1 and disciplina.id=?2",
                    mi.matriz.id, disciplinaId).firstResultOptional();
            if (dup.isPresent() && !dup.get().id.equals(mi.id)) {
                throw new BadRequestException("Já existe item para essa disciplina na matriz");
            }
            mi.disciplina = d;
        }
        if (ordem != null) mi.ordem = ordem;
        return mi;
    }

    @Transactional
    public void move(Long id, int novaOrdem) {
        MatrizItem mi = (MatrizItem) MatrizItem.findByIdOptional(id).orElseThrow(NotFoundException::new);
        mi.ordem = novaOrdem;
    }

    @Transactional
    public void delete(Long id) {
        boolean ok = MatrizItem.deleteById(id);
        if (!ok) throw new NotFoundException();
    }
}
