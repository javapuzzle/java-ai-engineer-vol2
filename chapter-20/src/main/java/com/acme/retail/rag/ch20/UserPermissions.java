package com.acme.retail.rag.ch20;

import java.util.Set;

/** What a specific requesting user is actually cleared to see -- checked, never assumed. */
public record UserPermissions(String userId, Set<AccessScope> grantedScopes) {

    public boolean canAccess(AccessScope scope) {
        return grantedScopes.contains(scope);
    }
}
