/*
 * Copyright (c) 2018, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

package jdk.jfr.jvm;

import static jdk.test.lib.Asserts.assertGreaterThan;
import static jdk.test.lib.Asserts.assertNE;

import jdk.jfr.internal.JVM;
import jdk.jfr.internal.JVMSupport;
import jdk.jfr.internal.Type;

/**
 * @test TestClassId
 * @key jfr
 * @requires vm.hasJFR
 * @library /test/lib
 * @modules jdk.jfr/jdk.jfr.internal
 * @run main/othervm jdk.jfr.jvm.TestClassId
 */
public class TestClassId {

    public static void main(String... args) {
        assertClassIds();
        JVMSupport.createJFR();
        assertClassIds();
        JVMSupport.destroyJFR();
    }

    private static void assertClassIds() {
        long doubleClassId = Type.getTypeId(Double.class);
        assertGreaterThan(doubleClassId, 0L, "Class id must be greater than 0");

        long floatClassId = Type.getTypeId(Float.class);
        assertNE(doubleClassId, floatClassId, "Different classes must have different class ids");
    }
}
