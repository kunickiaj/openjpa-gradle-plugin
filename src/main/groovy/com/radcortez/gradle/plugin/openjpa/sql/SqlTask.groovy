package com.radcortez.gradle.plugin.openjpa.sql

import com.radcortez.gradle.plugin.openjpa.OpenJpa
import com.radcortez.gradle.plugin.openjpa.OpenJpaExtension
import org.apache.openjpa.lib.util.Options
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.persistence.Entity

/**
 * Description.
 *
 * @author Roberto Cortez
 */
class SqlTask extends DefaultTask {

    @TaskAction
    void sql() {
        OpenJpaExtension openJpaConfiguration = project.extensions.findByType(OpenJpaExtension)
        SqlExtension configuration = openJpaConfiguration.extensions.findByType(SqlExtension)

        def openJpa = OpenJpa.openJpa(openJpaConfiguration.classpath, new Options([
                "propertiesFile"      : openJpaConfiguration.persistenceXmlFile,
                "ConnectionDriverName": configuration.connectionDriverName,
                "schemaAction"        : configuration.schemaAction,
                "sqlFile"             : configuration.sqlFile
        ]))

        def entities = []
        openJpaConfiguration.classes.each { classFile ->
            openJpa.parseTypes(classFile.absolutePath).each { klass ->
                if (klass.isAnnotationPresent(Entity.class)) {
                    entities.add(classFile.absolutePath)
                }
            }
        }

        openJpa.mappingTool(entities as String[])
        openJpa.dispose()
    }
}
