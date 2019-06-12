package com.oppo.csc.gradle.archetype

import com.oppo.csc.gradle.archetype.util.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

class ArchetypeCleanTask extends DefaultTask {

    static final Logger LOGGER = Logging.getLogger(ArchetypeCleanTask.class)

    @TaskAction
    create() {
        LOGGER.info('Cleaning...')
        File targetDir = FileUtils.getResourceFile(project.projectDir, ArchetypePlugin.DIR_TARGET)
        if (targetDir.exists()) {
            targetDir.deleteDir()
            LOGGER.info('Deleted {}', targetDir.getAbsolutePath())
        } else {
            LOGGER.info('Skipped, not existing: {}', targetDir.getAbsolutePath())
        }
    }

    @Override
    String getGroup() {
        'Archetype'
    }

    @Override
    String getDescription() {
        'Cleans generated project(s)'
    }
}
