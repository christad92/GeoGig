/* Copyright (c) 2013 OpenPlans. All rights reserved.
 * This code is licensed under the BSD New License, available at the root
 * application directory.
 */
package org.geogit.di;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.geogit.api.AbstractGeoGitOp;
import org.geogit.api.plumbing.merge.ConflictsCheckOp;

/**
 * An interceptor to avoid incompatible running commands while merge conflicts exist
 * 
 */
class ConflictInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        AbstractGeoGitOp<?> operation = (AbstractGeoGitOp<?>) invocation.getThis();
        Boolean conflicts = operation.command(ConflictsCheckOp.class).call();
        if (conflicts.booleanValue()) {
            throw new IllegalStateException("Cannot run operation while merge conflicts exist.");
        }
        return invocation.proceed();
    }

}
