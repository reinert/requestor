/*
 * Copyright 2021 Danilo Reinert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reinert.requestor.core;

/**
 * A factory of delay sequences.
 *
 * @author Danilo Reinert
 */
public class DelaySequence {

    public static int[] fixed(int... seconds) {
        final int[] sequence = new int[seconds.length];
        for (int i = 0; i < seconds.length; i++) sequence[i] =  seconds[i] * 1000;
        return sequence;
    }

    public static int[] arithmetic(int seconds, int limit) {
        final int delay = seconds * 1000;
        final int[] sequence = new int[limit];
        for (int i = 0; i < limit; i++) sequence[i] = delay;
        return sequence;
    }

    public static int[] geometric(int initialSeconds, int ratio, int limit) {
        int delay = initialSeconds;
        final int[] sequence = new int[limit];
        for (int i = 0; i < limit; i++) {
            sequence[i] = delay * 1000;
            delay *= ratio;
        }
        return sequence;
    }
}
