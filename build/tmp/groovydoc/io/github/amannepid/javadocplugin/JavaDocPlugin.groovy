package io.github.amannepid.javadocplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.JavadocMemberLevel

/**
 * Created by amannepid on 10/13/16.
 */
class JavaDocPlugin implements Plugin<Project> {

    def static destinationDir = 'javadoc'

    /**
     * Apply this plugin to the given target object.
     *
     * @param target The target project
     */
    @Override
    void apply(Project target) {
        if (target == null || !target.hasProperty('android')) {
            throw new UnsupportedOperationException('Project is not Android Project.')
        }

        if (target.plugins.hasPlugin('com.android.application')) {
            target.android.applicationVariants.all { variant ->

                target.build.finalizedBy target.task(
                        "generate${variant.name.capitalize()}${target.name.capitalize()}Javadoc",
                        type: Javadoc) {
                    println ">>> ${target.name.capitalize()} Documentation: v$target.android.defaultConfig.versionName"
                    group 'Documentation'

                    source = variant.javaCompile.source
                    destinationDir = getDestinationDirectory(target, variant.name)

                    ext.androidJar =
                            "${target.android.sdkDirectory}/platforms/${target.android.compileSdkVersion}/android.jar"
                    classpath = target.files(variant.javaCompile.classpath.files) + target.files(ext.androidJar) +
                            target.files(target.android.getBootClasspath().join(File.pathSeparator))

                    options {
                        title = "${target.name.capitalize()} Documentation: v$target.android.defaultConfig.versionName"

                        memberLevel = JavadocMemberLevel.PUBLIC

                        links "http://docs.oracle.com/javase/8/docs/api/"
                        links "http://developer.android.com/reference/"
                        linksOffline "http://developer.android.com/reference/",
                                "${target.android.sdkDirectory}/docs/reference/"
                    }

                    exclude '**/BuildConfig.java'
                    exclude '**/R.java'
                }
            }
        }

        if (target.plugins.hasPlugin('com.android.library')) {
            target.android.libraryVariants.all { variant ->

                target.build.finalizedBy target.task(
                        "generate${variant.name.capitalize()}${target.name.capitalize()}Javadoc",
                        type: Javadoc) {
                    println ">>> ${target.name.capitalize()} Documentation: v$target.android.defaultConfig.versionName"
                    group 'Documentation'

                    source = variant.javaCompile.source
                    destinationDir = getDestinationDirectory(target, variant.name)

                    ext.androidJar =
                            "${target.android.sdkDirectory}/platforms/${target.android.compileSdkVersion}/android.jar"
                    classpath = target.files(variant.javaCompile.classpath.files) + target.files(ext.androidJar) +
                            target.files(target.android.getBootClasspath().join(File.pathSeparator))

                    options {
                        title = "${target.name.capitalize()} Documentation: v$target.android.defaultConfig.versionName"

                        memberLevel = JavadocMemberLevel.PUBLIC

                        links "http://docs.oracle.com/javase/8/docs/api/"
                        links "http://developer.android.com/reference/"
                        linksOffline "http://developer.android.com/reference/",
                                "${target.android.sdkDirectory}/docs/reference/"
                    }

                    exclude '**/BuildConfig.java'
                    exclude '**/R.java'
                }
            }
        }

        addDeleteTaskBeforeCleanTask(target)
    }

    static def getDestinationDirectory(final Project project, final String variantName) {
        return new File("${project.getProjectDir()}/$destinationDir/", variantName)
    }

    static def addDeleteTaskBeforeCleanTask(final Project project) {
        project.clean.dependsOn project.task('deleteJavaDoc', type: Delete) {
            delete "${project.getProjectDir()}/$destinationDir"
        }
    }
}
