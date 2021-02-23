package com.cwj.plugin.asm

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*


/**
 * NewCustomMethodVisitor2 插入方法开始、结束计时字节码
 *
 * 注意：此方法仅作为技术实现测试，具体的统计值存在问题，不要采用。
 *
 * @author  wenjia.Cheng  cwj1714@163.com
 * @date    2021/2/20
 */
class NewCustomMethodVisitor2(
    mv: MethodVisitor,
    val methodName: String,
    val className: String,
    val localVar: String
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
            // 为局部变量 赋值
            mv.visitVarInsn(ALOAD, 0)
            mv.visitLdcInsn(className + "&" + methodName)
            mv.visitFieldInsn(PUTFIELD, className, localVar, "Ljava/lang/String;")
            // 获取指定的局部 的值
            mv.visitVarInsn(ALOAD, 0)
            mv.visitFieldInsn(GETFIELD, className, localVar, "Ljava/lang/String;")
            mv.visitMethodInsn(
                INVOKESTATIC,
                "com/cwj/sdklib/ComputeTargetCost",
                "startTime",
                "(Ljava/lang/String;)V",
                false
            )
        }
    }


    override fun visitInsn(opcode: Int) {
        if (!isIgnore) {
            if (opcode >= IRETURN && opcode <= RETURN || opcode == ATHROW) {
                mv.visitVarInsn(ALOAD, 0)
                mv.visitLdcInsn(className + "&" + methodName)
                mv.visitFieldInsn(PUTFIELD, className, localVar, "Ljava/lang/String;")
                // 获取指定的局部 的值
                mv.visitVarInsn(ALOAD, 0)
                mv.visitFieldInsn(GETFIELD, className, localVar, "Ljava/lang/String;")
                mv.visitMethodInsn(
                    INVOKESTATIC,
                    "com/cwj/sdklib/ComputeTargetCost",
                    "stopTime",
                    "(Ljava/lang/String;)V",
                    false
                )
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