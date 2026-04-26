package br.com.rennataarruda.todolist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PapelPermissaoId implements Serializable {

    @Column(name = "PAPEL_ID")
    private Long papelId;

    @Column(name = "PERMISSAO_ID")
    private Long permissaoId;
}
