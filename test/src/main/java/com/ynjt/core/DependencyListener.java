package com.ynjt.core;

import java.io.Serializable;

public interface DependencyListener extends Serializable {

    void preDependencyAdd(final Node host, final Node dependency) throws DependencyOperationException;
    void postDependencyAdd(final Node host, final Node dependency) throws DependencyOperationException;

    void preDependencyRemoved(final Node host, final Node dependency) throws DependencyOperationException;
    void postDependencyRemoved(final Node host, final Node dependency) throws DependencyOperationException;
}