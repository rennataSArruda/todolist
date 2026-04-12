package br.com.rennataarruda.todolist.security;

import br.com.rennataarruda.todolist.security.authorization.PapelCodigo;
import br.com.rennataarruda.todolist.security.authorization.PermissaoCodigo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationAccessService {

    private static final String ROOT_AUTHORITY = "ROOT";

    public boolean hasAuthorityOrRoot(Authentication authentication, String authority) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .anyMatch(grantedAuthority -> ROOT_AUTHORITY.equals(grantedAuthority) || authority.equals(grantedAuthority));
    }

    public boolean hasResourcePermission(Authentication authentication, PapelCodigo papel, PermissaoCodigo permissao) {
        return hasAuthorityOrRoot(authentication, papel.comporAuthority(permissao));
    }

    public boolean hasRoot(Authentication authentication) {
        return hasAuthorityOrRoot(authentication, ROOT_AUTHORITY);
    }
}
