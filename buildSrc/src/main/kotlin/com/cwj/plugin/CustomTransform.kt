package com.cwj.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.cwj.plugin.DataConstant.PLUGIN_NAME
import com.cwj.plugin.asm.CustomClassVisitor
import org.apache.commons.compress.utils.IOUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.util.CheckClassAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *
 * @author  wenjia.Cheng  cwj1714@163.com
 * @date    2021/2/2
 */
class CustomTransform(val project: Project) : Transform() {

    /**
     * 设置插件名称
     */
    override fun getName(): String {
        return PLUGIN_NAME
    }

    /**
     * 设置过滤类型
     */
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 设置过滤范围
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 设置是否支持增量编译
     */
    override fun isIncremental(): Boolean {
        return false
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        val startTime = System.currentTimeMillis()
        // 获取inputs
        val inputs = transformInvocation?.inputs
        // 遍历inputs，分2种
        inputs?.forEach { transformInput ->
            println("==> transformInput is : ${transformInput.directoryInputs}")
            run {
                // 目录文件夹是我们的源代码和生成的R文件和BuildConfig文件等
                transformInput.directoryInputs.forEach { directoryInput ->

                    run {
                        // 后续统一操作
                        val dest = transformInvocation.outputProvider.getContentLocation(
                            directoryInput.name + directoryInput.file.absoluteFile.hashCode(),
                            directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY
                        )


                        for (classFile in com.android.utils.FileUtils.getAllFiles(directoryInput.file)) {
                            /**
                             * directoryInput.file is : /Users/yannischeng/Android_MySelf/ASMProject/app/build/tmp/kotlin-classes/debug
                             * classFile is : /Users/yannischeng/Android_MySelf/ASMProject/app/build/tmp/kotlin-classes/debug/META-INF/app_debug.kotlin_module
                             *
                             * directoryInput.file is : /Users/yannischeng/Android_MySelf/ASMProject/app/build/tmp/kotlin-classes/debug
                             * classFile is : /Users/yannischeng/Android_MySelf/ASMProject/app/build/tmp/kotlin-classes/debug/com/cwj/myapplication/MainActivity.class
                             * classFile.parent is : /Users/yannischeng/Android_MySelf/ASMProject/app/build/tmp/kotlin-classes/debug/com/cwj/myapplication
                             * classFile.absolutePath : /Users/yannischeng/Android_MySelf/ASMProject/app/build/tmp/kotlin-classes/debug/com/cwj/myapplication/MainActivity.class
                             * classFile.name : MainActivity.class
                             */
                            if (classFile.name.endsWith(".class")) {
                                val beIgnore = isAndroidGenerated(classFile.name) || isNeedIgnore(classFile.absolutePath)
                                if (!beIgnore) {
                                    println("directoryInput.file is : ${directoryInput.file}")
                                    println("classFile is : ${classFile}")
                                    println("classFile.parent is : ${classFile.parent}")
                                    println("classFile.absolutePath : ${classFile.absolutePath}")
                                    println("classFile.name : ${classFile.name}")
                                    println("classFile.path : ${classFile.path}")
                                    println()
                                    val classWriter = ClassWriter(0)
                                    val checkClassAdapter = CheckClassAdapter(classWriter)
                                    val reader = ClassReader(classFile.readBytes())
                                    reader.accept(CustomClassVisitor(checkClassAdapter), 0)
                                    val toByteArray = classWriter.toByteArray()
                                    //writeToFile(classFile.name,toByteArray)
                                    if (toByteArray != null) {
                                        val file1 = File(classFile.absolutePath)
                                        if (file1.exists()) {
                                            file1.delete()
                                        }
                                        file1.createNewFile()
                                        val fileOutputStream = FileOutputStream(file1)
                                        fileOutputStream.write(toByteArray)
                                        IOUtils.closeQuietly(fileOutputStream)
                                    }
                                } else {
                                    println("被排除的类：${classFile.name}, path is ${classFile.absolutePath}")
                                }
                            }
                        }

                        // 将input的目录复制到output指定目录，
                        // 因为input一定要output，否则就丢失了(transform为链式的)
                        FileUtils.copyDirectory(directoryInput.file, dest)
                    }
                }

                // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

                // jar文件一般就是依赖，该jar部分一定要处理，否则会导致找不到jar中的类
                transformInput.jarInputs.forEach { jarInput ->
                    run {
                        val dest = transformInvocation.outputProvider.getContentLocation(
                            jarInput.name + jarInput.file.absoluteFile.hashCode(),
                            jarInput.contentTypes, jarInput.scopes, Format.JAR
                        )
                        FileUtils.copyFile(jarInput.file, dest)
                    }
                }
            }
        }
        println("注意 ----> 本次方法耗时埋点共计花费 ：${System.currentTimeMillis() - startTime} 毫秒")
    }

    /**
     * 需要排除Android自动生成的类
     * @param className
     * @return
     */
    fun isAndroidGenerated(className:String):Boolean {
        return className.startsWith("R$") ||
                className.startsWith("R2$") ||
                className.startsWith("R.") ||
                className.startsWith("R2.") ||
                className.startsWith("BuildConfig")
    }

    /**
     * 需要排除某些库
     * @param classFilePath
     * @return
     */
    fun isNeedIgnore(classFilePath:String):Boolean {
        return classFilePath.contains("com/sensorsdata/analytics/android/sdk") ||
                classFilePath.contains("androidx") ||
                classFilePath.contains("com/google/android") ||
                classFilePath.contains("com/example") ||
                classFilePath.contains("android/support") ||
                classFilePath.contains("android/arch") ||
                classFilePath.contains("com/tencent/smtt") ||
                classFilePath.contains("com/cwj/sdklib")
    }

}
