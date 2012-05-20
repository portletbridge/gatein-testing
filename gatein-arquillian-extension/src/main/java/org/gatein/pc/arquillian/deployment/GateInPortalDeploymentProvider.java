/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.gatein.pc.arquillian.deployment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.portal.spi.container.deployment.PortalContainerDeploymentProvider;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

/**
 * @author <a href="mailto:ken@kenfinnigan.me">Ken Finnigan</a>
 */
public class GateInPortalDeploymentProvider implements PortalContainerDeploymentProvider {

    /**
     * @see org.jboss.arquillian.portal.spi.container.deployment.PortalContainerDeploymentProvider#build()
     */
    @Override
    public Archive<?> build() {
        List<WebArchive> gateinPortalContainerArchives = new ArrayList<WebArchive>();

        // get all maven dependencies
        File[] artifacts = DependencyResolvers.use(MavenDependencyResolver.class)
                .loadEffectivePom("pom.xml")
                .importAllDependencies()
                .resolveAsFiles();

        // look for gatein-portal-container artifacts
        for(File artifactFile : artifacts) {
            String fileName = artifactFile.getName();
            if(fileName.startsWith("gatein-portal-container") && fileName.endsWith(".war")) {
                gateinPortalContainerArchives.add(ShrinkWrap.createFromZipFile(WebArchive.class, artifactFile));
            }
        }

        if(gateinPortalContainerArchives.isEmpty()) {
            throw new RuntimeException("No gatein-portal-container artifact found");
        }
        if(gateinPortalContainerArchives.size() > 1) {
            throw new RuntimeException("Multiple gatein-portal-container artifacts found");
        }

        return gateinPortalContainerArchives.iterator().next();
    }

}
