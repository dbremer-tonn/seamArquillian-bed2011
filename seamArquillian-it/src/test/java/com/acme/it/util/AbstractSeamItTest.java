package com.acme.it.util;

import java.util.logging.Logger;

import com.acme.logic.framework.AbstractEntityListService;
import com.acme.logic.framework.AbstractEntityListServiceBean;
import com.acme.model.AbstractEntity;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

/**
 * Seam 2 Base class for integration tests. Will be executed within container. Test includes required files, only.
 *
 * @author <a href="mailto:michaelschuetz83@gmail.com">Michael Schuetz</a>
 */
@RunWith(Arquillian.class)
public abstract class AbstractSeamItTest {

    private static final String MAVEN_ARTIFACT_SEAM = "org.jboss.seam:jboss-seam:2.2.1.FINAL";
    private static final String MAVEN_ARTIFACT_JBOSS_EL = "org.jboss.el:jboss-el:1.0_02.CR5";

    protected static final Logger LOG = Logger.getLogger(AbstractSeamItTest.class.getName());

    protected static Archive<?> getTestArchive(final Class... clazzes) {

        // WAR
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
                .setWebXML("war/web.xml")
                .addWebResource("war/components.xml");

        // EJB-JAR
        final JavaArchive ejb = ShrinkWrap.create(JavaArchive.class, "test.jar")
                // specific test dependent classes
                .addClasses(clazzes)
                .addClasses(
                        AbstractSeamItTest.class, SeamUtil.class, AbstractEntityListService.class, AbstractEntityListServiceBean.class,
                        AbstractEntity.class)

                .addResource("ejb/seam.properties", "seam.properties")
                .addResource("ejb/components.properties", "components.properties")
                .addManifestResource("ejb/ejb-jar.xml", "ejb-jar.xml")
                .addManifestResource("ejb/persistence.xml", "persistence.xml");

        // EAR
        final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
                .addModule(ejb)
                .addModule(war)
                .addModule(MavenArtifactResolver.resolve(MAVEN_ARTIFACT_SEAM))
                .addLibrary(MavenArtifactResolver.resolve(MAVEN_ARTIFACT_JBOSS_EL));

        LOG.info(ear.toString(true));
        return ear;
    }
}
