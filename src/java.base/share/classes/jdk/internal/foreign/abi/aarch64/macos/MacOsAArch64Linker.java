/*
 * Copyright (c) 2021, 2022, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021, Arm Limited. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package jdk.internal.foreign.abi.aarch64.macos;

import java.lang.foreign.Linker;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.VaList;

import jdk.internal.foreign.SystemLookup;
import jdk.internal.foreign.abi.SharedUtils;
import jdk.internal.foreign.abi.aarch64.CallArranger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * ABI implementation for macOS on Apple silicon. Based on AAPCS with
 * changes to va_list and passing arguments on the stack.
 */
public final class MacOsAArch64Linker implements Linker {
    private static MacOsAArch64Linker instance;

    static final long ADDRESS_SIZE = 64; // bits

    public static MacOsAArch64Linker getInstance() {
        if (instance == null) {
            instance = new MacOsAArch64Linker();
        }
        return instance;
    }

    @Override
    public final MethodHandle downcallHandle(FunctionDescriptor function) {
        Objects.requireNonNull(function);
        MethodType type = SharedUtils.inferMethodType(function, false);
        MethodHandle handle = CallArranger.MACOS.arrangeDowncall(type, function);
        handle = SharedUtils.maybeInsertAllocator(handle);
        return SharedUtils.wrapDowncall(handle, function);
    }

    @Override
    public final MemorySegment upcallStub(MethodHandle target, FunctionDescriptor function, MemorySession session) {
        Objects.requireNonNull(session);
        Objects.requireNonNull(target);
        Objects.requireNonNull(function);
        MethodType type = SharedUtils.inferMethodType(function, true);
        if (!type.equals(target.type())) {
            throw new IllegalArgumentException("Wrong method handle type: " + target.type());
        }
        return CallArranger.MACOS.arrangeUpcall(target, target.type(), function, session);
    }

    public static VaList newVaList(Consumer<VaList.Builder> actions, MemorySession session) {
        MacOsAArch64VaList.Builder builder = MacOsAArch64VaList.builder(session);
        actions.accept(builder);
        return builder.build();
    }

    public static VaList newVaListOfAddress(MemoryAddress ma, MemorySession session) {
        return MacOsAArch64VaList.ofAddress(ma, session);
    }

    public static VaList emptyVaList() {
        return MacOsAArch64VaList.empty();
    }

    @Override
    public SystemLookup defaultLookup() {
        return SystemLookup.getInstance();
    }
}
