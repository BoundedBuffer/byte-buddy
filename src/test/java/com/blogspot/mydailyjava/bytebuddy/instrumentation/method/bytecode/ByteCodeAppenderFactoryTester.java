package com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode;

import com.blogspot.mydailyjava.bytebuddy.instrumentation.method.MethodDescription;
import com.blogspot.mydailyjava.bytebuddy.instrumentation.type.InstrumentedType;
import com.blogspot.mydailyjava.bytebuddy.instrumentation.type.loading.ByteArrayClassLoader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Random;

public class ByteCodeAppenderFactoryTester {

    private static final String CONSTRUCTOR_INTERNAL_NAME = "<init>";
    private static final String DEFAULT_CONSTRUCTOR_DESCRIPTOR = "()V";

    private static final int ASM_MANUAL = 0;
    private static final String TEST_PACKAGE = "com.blogspot.mydailyjava.bytebuddy.test";

    private final ByteCodeAppender appender;
    private final Class<?> superClass;
    private final Random random;

    public ByteCodeAppenderFactoryTester(ByteCodeAppender.Factory factory,
                                         InstrumentedType typeDescription,
                                         Class<?> superClass) {
        appender = factory.make(typeDescription);
        this.superClass = superClass;
        random = new Random();
    }

    public Class<?> applyTo(MethodDescription appenderArgument,
                            MethodDescription signatureDescription) throws ClassNotFoundException {
        ClassWriter classWriter = new ClassWriter(ASM_MANUAL);
        String typeName = String.format("%s.Test%d", TEST_PACKAGE, Math.abs(random.nextInt()));
        classWriter.visit(Opcodes.V1_6,
                Opcodes.ACC_PUBLIC,
                toInternalName(typeName),
                null, Type.getInternalName(superClass), null);
        MethodVisitor defaultConstructor = classWriter.visitMethod(Opcodes.ACC_PUBLIC,
                CONSTRUCTOR_INTERNAL_NAME,
                DEFAULT_CONSTRUCTOR_DESCRIPTOR,
                null,
                null);
        defaultConstructor.visitCode();
        defaultConstructor.visitVarInsn(Opcodes.ALOAD, 0);
        defaultConstructor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                Type.getInternalName(superClass),
                CONSTRUCTOR_INTERNAL_NAME,
                DEFAULT_CONSTRUCTOR_DESCRIPTOR);
        defaultConstructor.visitInsn(Opcodes.RETURN);
        defaultConstructor.visitMaxs(1, 1);
        defaultConstructor.visitEnd();
        MethodVisitor methodVisitor = classWriter.visitMethod(signatureDescription.getModifiers(),
                signatureDescription.getInternalName(),
                signatureDescription.getDescriptor(),
                null,
                null);
        methodVisitor.visitCode();
        ByteCodeAppender.Size size = appender.apply(methodVisitor, appenderArgument);
        methodVisitor.visitMaxs(size.getOperandStackSize(), size.getLocalVariableSize());
        methodVisitor.visitEnd();
        classWriter.visitEnd();
        return new ByteArrayClassLoader(getClass().getClassLoader(), typeName, classWriter.toByteArray()).loadClass(typeName);
    }

    private static String toInternalName(String typeName) {
        return typeName.replace('.', '/');
    }
}