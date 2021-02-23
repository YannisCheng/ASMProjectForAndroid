package com.cwj.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor

/**
 *
 * @author  wenjia.Cheng  cwj1714@163.com
 * @date    2021/2/2
 */
class CustomPlugin : Plugin<Project> {


    override fun apply(project: Project) {
        if (project.plugins.hasPlugin(AppPlugin::class.java)) {

            //var extension = project.extensions.create("customExtensions", CustomExtensions::class.java)
            val androidExtension: AppExtension =
                project.extensions.getByType(AppExtension::class.java)
            val isSwitch = project.getProperty("PLUGIN_SWITCH", false)
            println("PLUGIN_SWITCH value is :$isSwitch")
            CustomPluginExtUtil.PLUGIN_COST_SWITCH = isSwitch
            if (isSwitch) {
                androidExtension.registerTransform(CustomTransform(project))
            }
        }
    }
}