/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.clustering.server.group;

import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.ValueService;
import org.jboss.msc.value.InjectedValue;
import org.jboss.msc.value.Value;
import org.wildfly.clustering.group.Group;
import org.wildfly.clustering.service.Builder;
import org.wildfly.clustering.spi.GroupServiceName;

/**
 * Builds a non-clustered cache-based {@link Group} service.
 * @author Paul Ferraro
 */
public class LocalCacheGroupBuilder extends CacheGroupServiceNameProvider implements Builder<Group>, Value<Group> {

    private final InjectedValue<JGroupsNodeFactory> factory = new InjectedValue<>();

    /**
     * @param containerName
     * @param cacheName
     */
    public LocalCacheGroupBuilder(String containerName, String cacheName) {
        super(containerName, cacheName);
    }

    @Override
    public ServiceBuilder<Group> build(ServiceTarget target) {
        return target.addService(this.getServiceName(), new ValueService<>(this))
                .addDependency(GroupServiceName.NODE_FACTORY.getServiceName(this.containerName), JGroupsNodeFactory.class, this.factory)
                .setInitialMode(ServiceController.Mode.ON_DEMAND);
    }

    @Override
    public Group getValue() {
        return new LocalGroup(this.containerName, this.factory.getValue().createNode(null));
    }
}
