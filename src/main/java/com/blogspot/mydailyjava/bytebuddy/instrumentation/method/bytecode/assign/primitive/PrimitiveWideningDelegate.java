package com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode.assign.primitive;

import com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode.TypeSize;
import com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode.assign.Assignment;
import com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode.assign.IllegalAssignment;
import com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode.assign.LegalTrivialAssignment;
import com.blogspot.mydailyjava.bytebuddy.instrumentation.type.TypeDescription;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public enum PrimitiveWideningDelegate {

    BOOLEAN(LegalTrivialAssignment.INSTANCE,                                                // to boolean
            IllegalAssignment.INSTANCE,                                                     // to byte
            IllegalAssignment.INSTANCE,                                                     // to short
            IllegalAssignment.INSTANCE,                                                     // to character
            IllegalAssignment.INSTANCE,                                                     // to integer
            IllegalAssignment.INSTANCE,                                                     // to long
            IllegalAssignment.INSTANCE,                                                     // to float
            IllegalAssignment.INSTANCE),                                                    // to double

    BYTE(IllegalAssignment.INSTANCE,                                                        // to boolean
            LegalTrivialAssignment.INSTANCE,                                                // to byte
            LegalTrivialAssignment.INSTANCE,                                                // to short
            IllegalAssignment.INSTANCE,                                                     // to character
            LegalTrivialAssignment.INSTANCE,                                                // to integer
            new WideningAssignment(Opcodes.I2L, TypeSize.SINGLE.toIncreasingSize()),        // to long
            new WideningAssignment(Opcodes.I2F, TypeSize.ZERO.toIncreasingSize()),          // to float
            new WideningAssignment(Opcodes.I2L, TypeSize.SINGLE.toIncreasingSize())),       // to double

    SHORT(IllegalAssignment.INSTANCE,                                                       // to boolean
            IllegalAssignment.INSTANCE,                                                     // to byte
            LegalTrivialAssignment.INSTANCE,                                                // to short
            IllegalAssignment.INSTANCE,                                                     // to character
            LegalTrivialAssignment.INSTANCE,                                                // to integer
            new WideningAssignment(Opcodes.I2L, TypeSize.SINGLE.toIncreasingSize()),        // to long
            new WideningAssignment(Opcodes.I2F, TypeSize.ZERO.toIncreasingSize()),          // to float
            new WideningAssignment(Opcodes.I2D, TypeSize.SINGLE.toIncreasingSize())),       // to double

    CHARACTER(IllegalAssignment.INSTANCE,                                                   // to boolean
            IllegalAssignment.INSTANCE,                                                     // to byte
            IllegalAssignment.INSTANCE,                                                     // to short
            LegalTrivialAssignment.INSTANCE,                                                // to character
            LegalTrivialAssignment.INSTANCE,                                                // to integer
            new WideningAssignment(Opcodes.I2L, TypeSize.SINGLE.toIncreasingSize()),        // to long
            new WideningAssignment(Opcodes.I2F, TypeSize.ZERO.toIncreasingSize()),          // to float
            new WideningAssignment(Opcodes.I2D, TypeSize.SINGLE.toIncreasingSize())),       // to double

    INTEGER(IllegalAssignment.INSTANCE,                                                     // to boolean
            IllegalAssignment.INSTANCE,                                                     // to byte
            IllegalAssignment.INSTANCE,                                                     // to short
            IllegalAssignment.INSTANCE,                                                     // to character
            LegalTrivialAssignment.INSTANCE,                                                // to integer
            new WideningAssignment(Opcodes.I2L, TypeSize.SINGLE.toIncreasingSize()),        // to long
            new WideningAssignment(Opcodes.I2F, TypeSize.ZERO.toIncreasingSize()),          // to float
            new WideningAssignment(Opcodes.I2D, TypeSize.SINGLE.toIncreasingSize())),       // to double

    LONG(IllegalAssignment.INSTANCE,                                                        // to boolean
            IllegalAssignment.INSTANCE,                                                     // to byte
            IllegalAssignment.INSTANCE,                                                     // to short
            IllegalAssignment.INSTANCE,                                                     // to character
            IllegalAssignment.INSTANCE,                                                     // to integer
            LegalTrivialAssignment.INSTANCE,                                                // to long
            new WideningAssignment(Opcodes.L2F, TypeSize.SINGLE.toDecreasingSize()),        // to float
            new WideningAssignment(Opcodes.L2D, TypeSize.ZERO.toIncreasingSize())),         // to double

    FLOAT(IllegalAssignment.INSTANCE,                                                       // to boolean
            IllegalAssignment.INSTANCE,                                                     // to byte
            IllegalAssignment.INSTANCE,                                                     // to short
            IllegalAssignment.INSTANCE,                                                     // to character
            IllegalAssignment.INSTANCE,                                                     // to integer
            IllegalAssignment.INSTANCE,                                                     // to long
            LegalTrivialAssignment.INSTANCE,                                                // to float
            new WideningAssignment(Opcodes.F2D, TypeSize.SINGLE.toIncreasingSize())),       // to double

    DOUBLE(IllegalAssignment.INSTANCE,                                                      // to boolean
            IllegalAssignment.INSTANCE,                                                     // to byte
            IllegalAssignment.INSTANCE,                                                     // to short
            IllegalAssignment.INSTANCE,                                                     // to character
            IllegalAssignment.INSTANCE,                                                     // to integer
            IllegalAssignment.INSTANCE,                                                     // to long
            IllegalAssignment.INSTANCE,                                                     // to float
            LegalTrivialAssignment.INSTANCE);                                               // to double

    private static class WideningAssignment implements Assignment {

        private final int conversionInstruction;
        private final Size size;

        public WideningAssignment(int conversionInstruction, Size size) {
            this.conversionInstruction = conversionInstruction;
            this.size = size;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Size apply(MethodVisitor methodVisitor) {
            methodVisitor.visitInsn(conversionInstruction);
            return size;
        }
    }

    public static PrimitiveWideningDelegate forPrimitive(TypeDescription typeDescription) {
        if (typeDescription.represents(boolean.class)) {
            return BOOLEAN;
        } else if (typeDescription.represents(byte.class)) {
            return BYTE;
        } else if (typeDescription.represents(short.class)) {
            return SHORT;
        } else if (typeDescription.represents(char.class)) {
            return CHARACTER;
        } else if (typeDescription.represents(int.class)) {
            return INTEGER;
        } else if (typeDescription.represents(long.class)) {
            return LONG;
        } else if (typeDescription.represents(float.class)) {
            return FLOAT;
        } else if (typeDescription.represents(double.class)) {
            return DOUBLE;
        } else {
            throw new IllegalArgumentException("Not a primitive, non-void type: " + typeDescription);
        }
    }

    private final Assignment toBooleanAssignment;
    private final Assignment toByteAssignment;
    private final Assignment toShortAssignment;
    private final Assignment toCharacterAssignment;
    private final Assignment toIntegerAssignment;
    private final Assignment toLongAssignment;
    private final Assignment toFloatAssignment;
    private final Assignment toDoubleAssignment;

    private PrimitiveWideningDelegate(Assignment toBooleanAssignment, Assignment toByteAssignment,
                                      Assignment toShortAssignment, Assignment toCharacterAssignment,
                                      Assignment toIntegerAssignment, Assignment toLongAssignment,
                                      Assignment toFloatAssignment, Assignment toDoubleAssignment) {
        this.toBooleanAssignment = toBooleanAssignment;
        this.toByteAssignment = toByteAssignment;
        this.toShortAssignment = toShortAssignment;
        this.toCharacterAssignment = toCharacterAssignment;
        this.toIntegerAssignment = toIntegerAssignment;
        this.toLongAssignment = toLongAssignment;
        this.toFloatAssignment = toFloatAssignment;
        this.toDoubleAssignment = toDoubleAssignment;
    }

    public Assignment widenTo(TypeDescription typeDescription) {
        if (typeDescription.represents(boolean.class)) {
            return toBooleanAssignment;
        } else if (typeDescription.represents(byte.class)) {
            return toByteAssignment;
        } else if (typeDescription.represents(short.class)) {
            return toShortAssignment;
        } else if (typeDescription.represents(char.class)) {
            return toCharacterAssignment;
        } else if (typeDescription.represents(int.class)) {
            return toIntegerAssignment;
        } else if (typeDescription.represents(long.class)) {
            return toLongAssignment;
        } else if (typeDescription.represents(float.class)) {
            return toFloatAssignment;
        } else if (typeDescription.represents(double.class)) {
            return toDoubleAssignment;
        } else {
            throw new IllegalArgumentException("Not a primitive non-void type: " + typeDescription);
        }
    }
}