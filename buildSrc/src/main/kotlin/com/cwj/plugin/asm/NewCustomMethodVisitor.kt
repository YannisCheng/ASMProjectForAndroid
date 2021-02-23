package com.cwj.plugin.asm

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*


/**
 *
 * @author  wenjia.Cheng  cwj1714@163.com
 * @date    2021/2/20
 */
class NewCustomMethodVisitor(
    mv: MethodVisitor,
    private val methodName: String,
    private val className: String,
    private val localVar: String,
    private val superApplicationStr: String
) : MethodVisitor(ASM6, mv) {

    var isIgnore = false;

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        println("visible is : $visible, descriptor is $descriptor")
        descriptor?.let {
            isIgnore = descriptor.equals("Lcom/cwj/sdklib/IgnoreTraceMethodCostMethod;")
            println("isIgnore is $isIgnore")
        }
        return mv.visitAnnotation(descriptor, visible)
    }

    override fun visitCode() {
        super.visitCode()
        if (!isIgnore) {
            // 在方法进入时，重新设置 "类路径+当前方法名"
            mv.visitVarInsn(ALOAD, 0)
            mv.visitLdcInsn("$className&$methodName")
            mv.visitFieldInsn(PUTFIELD, className, localVar, "Ljava/lang/String;")
            mv.visitFieldInsn(
                GETSTATIC,
                "com/cwj/sdklib/MethodCostUtil",
                "INSTANCE",
                "Lcom/cwj/sdklib/MethodCostUtil;"
            )
            // 获取指定的局部 的值
            mv.visitVarInsn(ALOAD, 0)
            mv.visitFieldInsn(GETFIELD, className, localVar, "Ljava/lang/String;")
            if (superApplicationStr != "" && superApplicationStr == "android/app/Application") {
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "com/cwj/sdklib/MethodCostUtil",
                    "recodeStaticApplicationMethodCostStart",
                    "(Ljava/lang/String;)V",
                    false
                )
            } else {
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "com/cwj/sdklib/MethodCostUtil",
                    "recodeStaticMethodCostStart",
                    "(Ljava/lang/String;)V",
                    false
                )
            }
        }
    }


    override fun visitInsn(opcode: Int) {
        if (!isIgnore) {
            if (opcode in IRETURN..RETURN || opcode == ATHROW) {
                // 在方法退出时，重新设置 "类路径+当前方法名"
                mv.visitVarInsn(ALOAD, 0)
                mv.visitLdcInsn("$className&$methodName")
                mv.visitFieldInsn(PUTFIELD, className, localVar, "Ljava/lang/String;")
                // 获取指定的局部 的值
                mv.visitFieldInsn(
                    GETSTATIC,
                    "com/cwj/sdklib/MethodCostUtil",
                    "INSTANCE",
                    "Lcom/cwj/sdklib/MethodCostUtil;"
                )
                mv.visitVarInsn(ALOAD, 0)
                mv.visitFieldInsn(GETFIELD, className, localVar, "Ljava/lang/String;")
                if (superApplicationStr != "" && superApplicationStr == "android/app/Application") {
                    mv.visitMethodInsn(
                        INVOKEVIRTUAL,
                        "com/cwj/sdklib/MethodCostUtil",
                        "recodeStaticApplicationMethodCostEnd",
                        "(Ljava/lang/String;)V",
                        false
                    )
                } else {
                    mv.visitMethodInsn(
                        INVOKEVIRTUAL,
                        "com/cwj/sdklib/MethodCostUtil",
                        "recodeStaticMethodCostEnd",
                        "(Ljava/lang/String;)V",
                        false
                    )
                }
            }
        }
        mv.visitInsn(opcode)
    }

    /**
     * 必须要给出修改后的操作数栈的大小，否则在某些情况下会发生：
     * Error at instruction 1: Insufficient maximum stack size. onClick(Landroid/view/View;)V
     */
    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack + 2, maxLocals)
    }
}