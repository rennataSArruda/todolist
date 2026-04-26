package br.com.rennataarruda.todolist.security.authorization;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@authorizationAccessService.hasResourcePermission(authentication, T(br.com.rennataarruda.todolist.security.authorization.PapelCodigo).TAREFA_CATEGORIA, T(br.com.rennataarruda.todolist.security.authorization.PermissaoCodigo).EDITAR)")
public @interface PodeEditarTarefaCategoria {
}
