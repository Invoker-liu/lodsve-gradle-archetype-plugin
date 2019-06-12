package com.oppo.csc.gradle.archetype

import org.gradle.api.Plugin
import org.gradle.api.Project

class ArchetypePlugin implements Plugin<Project> {

    static final String DIR_TEMPLATES = 'src/main/resources/templates'
    static final String DIR_TARGET = 'generated'

    @Override
    void apply(Project project) {
        project.task('cleanArchetype', type: ArchetypeCleanTask)
        project.task('generate', type: ArchetypeGenerateTask)
    }

}
