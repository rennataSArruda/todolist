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
@Table(name = "VW_TAREFA_ANALITICO")
public class TarefaAnaliticoView {

    @Id
    @Column(name = "TAREFA_ID", nullable = false)
    private Long tarefaId;

    @Column(name = "USUARIO_ID", nullable = false)
    private Long usuarioId;

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

    @Column(name = "CATEGORIA_ID")
    private Long categoriaId;

    @Column(name = "CATEGORIA_NOME")
    private String categoriaNome;

    @Column(name = "TITULO")
    private String titulo;

    @Column(name = "DATA_INICIO")
    private LocalDateTime dataInicio;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ATIVO")
    private Boolean ativo;

    @Column(name = "DATA_FIM")
    private LocalDateTime dataFim;

    @Column(name = "DATA_CONCLUSAO")
    private LocalDateTime dataConclusao;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}
