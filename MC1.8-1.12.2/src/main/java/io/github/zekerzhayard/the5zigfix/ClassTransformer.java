package io.github.zekerzhayard.the5zigfix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformer implements IClassTransformer {
    private static Logger logger = LogManager.getLogger("The5zigFix");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.gui.GuiMainMenu")) {
            ClassTransformer.logger.info("Found the class: " + transformedName);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            new ClassReader(basicClass).accept(new ClassVisitor(Opcodes.ASM5, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
                    if (name.equals("<clinit>") && desc.equals("()V")) {
                        ClassTransformer.logger.info("Found the method: " + name + desc);
                        return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                            @Override
                            public void visitCode() {
                                if (this.mv != null) {
                                    super.visitCode();
                                    this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "BytecodeHook", "onDisplayScreen", "()V", false);
                                }
                            }
                        };
                    }
                    return methodVisitor;
                }
            }, ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        return basicClass;
    }
}
