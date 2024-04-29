package com.lodsve.gradle.archetype

import com.lodsve.gradle.archetype.util.ConsoleUtils
import com.lodsve.gradle.archetype.util.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

class ArchetypeGenerateTask extends DefaultTask {

    static final Logger LOGGER = Logging.getLogger(ArchetypeGenerateTask.class)

    ArchetypeGenerateTask() {
        ext {

            // This property allows the consuming plugin to configure this task with a closure that will be
            // invoked just prior to performing the actual generation.  It gives the project a chance to
            // add/modify the bindings as needed.

            bindingProcessor = {} // Default processor does nothing
        }
    }

    @TaskAction
    create() {
        String projectGroup = getParam('group', 'Please enter the group name.')
        String projectName = getParam('name', 'Please enter the project name.')
        String packageName = getParam('packageName', "Please enter the package name , use '.' split folder name.")
        String projectVersion = getParam('version', 'Please enter the version name', '1.0.0-SNAPSHOT')
        String author = getParam('author', 'Please enter the your name', 'Administrator')
        String port = getParam('port', 'Please enter the service port', '8080')
        String contextPath = getParam('contextPath', 'Please enter the service context path', '/')

        String templatePath = System.getProperty('templates', ArchetypePlugin.DIR_TEMPLATES)

        Map binding = [
                'groupId'    : projectGroup,
                'artifactId' : projectName,
                'packageName': packageName,
                'version'    : projectVersion,
                'author'     : author,
                'port'       : port,
                'contextPath': contextPath
        ]
        extendedBinding(binding)

        // Now that all the bindings have been resolved, let the consuming project perform any customizations they need
        bindingProcessor.call(binding)

        logBindings(binding)

        FileUtils.generate(project.projectDir, templatePath, binding, isFailIfFileExist())
    }

    private static void extendedBinding(Map binding) {
        addCommandLinePropertiesToBinding(binding)
        addPropertyScopedBindings(binding)

        String packageName = binding.get('packageName')

        !binding.containsKey('packagePath') && binding.put('packagePath', packageName.replaceAll("\\.", "/"))
    }

    private static void addCommandLinePropertiesToBinding(binding) {
        String extraProperties = System.getProperty("sun.java.command")
        if (null != extraProperties) {
            extraProperties.split('\\s+').each { item ->
                int equalSignIndex
                if (item.startsWith("-D") && (equalSignIndex = item.indexOf('=')) > 2) {
                    String key = item.substring(2, equalSignIndex)
                    String value = item.substring(equalSignIndex + 1, item.length())
                    binding.put(key, value)
                }
            }
        }
    }

    private static void addPropertyScopedBindings(binding) {
        final String propertyBinding = "com.lodsve.gradle.archetype.binding"
        final int scopeSize = propertyBinding.length() + 1
        System.getProperties().findAll { p -> p.key.startsWith(propertyBinding) }
                .each { p -> binding.put(p.key.substring(scopeSize), p.value) }
    }

    private static String getParam(String paramName, String prompt, String defaultValue = null) {
        String value = System.getProperty(paramName)

        if (!value) {
            value = ConsoleUtils.prompt(prompt, defaultValue)
        }

        if (!value) {
            throw new IllegalArgumentException("Parameter required: $paramName")
        }

        return value
    }

    private static void logBindings(Map map) {
        LOGGER.info('Variables:')
        map.each { k, v -> LOGGER.info("  {}='{}'", k.padRight(25), v) }
    }

    private static boolean isFailIfFileExist() {
        String value = System.getProperty('failIfFileExist', 'y').trim().toLowerCase().charAt(0)
        'y' == value || 't' == value || '1' == value
    }

    @Override
    String getGroup() {
        'Archetype'
    }

    @Override
    String getDescription() {
        'Generates project(s) from template(s)'
    }
}
