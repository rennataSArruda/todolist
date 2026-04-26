package br.com.rennataarruda.todolist.entity;

import br.com.rennataarruda.todolist.entity.commons.UsuarioScopedEntity;
import br.com.rennataarruda.todolist.entity.commons.WithUpdatedAt;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "TAREFA")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tarefa extends WithUpdatedAt implements UsuarioScopedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "USUARIO_ID", nullable = false)
    private Long usuarioId;

    @Column(name = "CATEGORIA_ID")
    private Long categoriaId;

    @Column(name = "STATUS_ID", nullable = false)
    private Long statusId;

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }


    @Column(name = "PRIORIDADE_ID", nullable = false)
    private Long prioridadeId;

    @Column(name = "TITULO", nullable = false, length = 150)
    private String titulo;

    @Column(name = "DESCRICAO", length = 1000)
    private String descricao;

    @Column(name = "DATA_INICIO")
    private LocalDateTime dataInicio;

    @Column(name = "DATA_FIM")
    private LocalDateTime dataFim;

    @Column(name = "DATA_CONCLUSAO")
    private LocalDateTime dataConclusao;

    @Column(name = "POSICAO")
    private Long posicao;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "IMPORTANTE", nullable = false)
    private Boolean importante = false;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = true;

    public Tarefa(
            Long categoriaId,
            Long statusId,
            Long prioridadeId,
            String titulo,
            String descricao,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            LocalDateTime dataConclusao,
            Long posicao,
            Boolean importante
    ) {
        atualizar(categoriaId, statusId, prioridadeId, titulo, descricao, dataInicio, dataFim, dataConclusao, posicao, importante);
    }

    @Override
    public void definirUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void atualizar(
            Long categoriaId,
            Long statusId,
            Long prioridadeId,
            String titulo,
            String descricao,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            LocalDateTime dataConclusao,
            Long posicao,
            Boolean importante
    ) {
        this.categoriaId = categoriaId;
        this.statusId = statusId;
        this.prioridadeId = prioridadeId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.dataConclusao = dataConclusao;
        this.posicao = posicao;
        this.importante = Boolean.TRUE.equals(importante);
    }

    public void alternarAtivo() {
        this.ativo = !Boolean.TRUE.equals(this.ativo);
    }
}
