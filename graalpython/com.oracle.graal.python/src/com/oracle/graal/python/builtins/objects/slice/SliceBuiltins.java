/*
 * Copyright (c) 2017, 2019, Oracle and/or its affiliates.
 * Copyright (c) 2014, Regents of the University of California
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.graal.python.builtins.objects.slice;

import static com.oracle.graal.python.nodes.SpecialMethodNames.__REPR__;

import java.util.List;

import com.oracle.graal.python.builtins.Builtin;
import com.oracle.graal.python.builtins.CoreFunctions;
import com.oracle.graal.python.builtins.PythonBuiltinClassType;
import com.oracle.graal.python.builtins.PythonBuiltins;
import com.oracle.graal.python.builtins.objects.PNone;
import com.oracle.graal.python.builtins.objects.tuple.PTuple;
import com.oracle.graal.python.nodes.SpecialMethodNames;
import com.oracle.graal.python.nodes.function.PythonBuiltinBaseNode;
import com.oracle.graal.python.nodes.function.PythonBuiltinNode;
import com.oracle.graal.python.nodes.function.builtins.PythonBinaryBuiltinNode;
import com.oracle.graal.python.nodes.function.builtins.PythonUnaryBuiltinNode;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.Specialization;

@CoreFunctions(extendClasses = PythonBuiltinClassType.PSlice)
public class SliceBuiltins extends PythonBuiltins {

    @Override
    protected List<? extends NodeFactory<? extends PythonBuiltinBaseNode>> getNodeFactories() {
        return SliceBuiltinsFactory.getFactories();
    }

    @Builtin(name = __REPR__, minNumOfPositionalArgs = 1)
    @GenerateNodeFactory
    public abstract static class ReprNode extends PythonBuiltinNode {
        @Specialization
        @TruffleBoundary
        public String repr(PSlice self) {
            return self.toString();
        }
    }

    @Builtin(name = SpecialMethodNames.__EQ__, minNumOfPositionalArgs = 2)
    @GenerateNodeFactory
    abstract static class EqNode extends PythonBuiltinNode {
        @Specialization
        boolean doPRange(PSlice left, PSlice right) {
            return left.equals(right);
        }
    }

    @Builtin(name = "start", minNumOfPositionalArgs = 1, isGetter = true)
    @GenerateNodeFactory
    @ImportStatic(PSlice.class)
    abstract static class StartNode extends PythonUnaryBuiltinNode {

        @Specialization(guards = "self.getStart() != MISSING_INDEX")
        protected int get(PSlice self) {
            return self.getStart();
        }

        @Specialization(guards = "self.getStart() == MISSING_INDEX")
        protected Object getNone(@SuppressWarnings("unused") PSlice self) {
            return PNone.NONE;
        }
    }

    @Builtin(name = "stop", minNumOfPositionalArgs = 1, isGetter = true)
    @GenerateNodeFactory
    @ImportStatic(PSlice.class)
    abstract static class StopNode extends PythonUnaryBuiltinNode {

        @Specialization(guards = "self.getStop() != MISSING_INDEX")
        protected int get(PSlice self) {
            return self.getStop();
        }

        @Specialization(guards = "self.getStop() == MISSING_INDEX")
        protected Object getNone(@SuppressWarnings("unused") PSlice self) {
            return PNone.NONE;
        }
    }

    @Builtin(name = "step", minNumOfPositionalArgs = 1, isGetter = true)
    @GenerateNodeFactory
    @ImportStatic(PSlice.class)
    abstract static class StepNode extends PythonUnaryBuiltinNode {

        @Specialization(guards = "self.getStep() != MISSING_INDEX")
        protected int get(PSlice self) {
            return self.getStep();
        }

        @Specialization(guards = "self.getStep() == MISSING_INDEX")
        protected Object getNone(@SuppressWarnings("unused") PSlice self) {
            return PNone.NONE;
        }
    }

    @Builtin(name = "indices", minNumOfPositionalArgs = 2)
    @GenerateNodeFactory
    @ImportStatic(PSlice.class)
    abstract static class IndicesNode extends PythonBinaryBuiltinNode {

        @Specialization()
        protected PTuple get(PSlice self, int length) {
            PSlice.SliceInfo sliceInfo = self.computeIndices(length);

            return factory().createTuple(new Object[]{sliceInfo.start, sliceInfo.stop, sliceInfo.step});
        }
    }
}
