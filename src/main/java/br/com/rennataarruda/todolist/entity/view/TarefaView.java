package br.com.rennataarruda.todolist.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.type.NumericBooleanConverter;

import java.time.LocalDateTime;

@Getter
@Entity
@Immutable
@Table(name = "VW_TAREFA")
public class TarefaView {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "USUARIO_ID", nullable = false)
    private Long usuarioId;

    @Column(name = "CATEGORIA_ID")
    private Long categoriaId;

    @Column(name = "CATEGORIA_NOME")
    private String categoriaNome;

    @Column(name = "CATEGORIA_DESCRICAO")
    private String categoriaDescricao;

    @Column(name = "STATUS_ID", nullable = false)
    private Long statusId;

    @Column(name = "STATUS_CODIGO")
    private String statusCodigo;

    @Column(name = "STATUS_DESCRICAO")
    private String statusDescricao;

    @Column(name = "PRIORIDADE_ID", nullable = false)
    private Long prioridadeId;

    @Column(name = "PRIORIDADE_CODIGO")
    private String prioridadeCodigo;

    @Column(name = "PRIORIDADE_DESCRICAO")
    private String prioridadeDescricao;

    @Column(name = "PRIORIDADE_ORDEM")
    private Long prioridadeOrdem;

    @Column(name = "TITULO")
    private String titulo;

    @Column(name = "DESCRICAO")
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
    @Column(name = "IMPORTANTE")
    private Boolean importante;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ATIVO")
    private Boolean ativo;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
