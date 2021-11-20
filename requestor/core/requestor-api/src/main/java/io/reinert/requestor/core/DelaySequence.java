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

    /**
     * Converts the given seconds array to a milliseconds array.
     *
     * @param seconds An array of delays in seconds
     *
     * @return The given array converted to milliseconds
     */
    public static int[] fixed(int... seconds) {
        final int[] sequence = new int[seconds.length];
        for (int i = 0; i < seconds.length; i++) sequence[i] =  seconds[i] * 1000;
        return sequence;
    }

    /**
     * Generates a milliseconds arithmetic sequence according to the specified configuration.
     * <p></p>
     * In an Arithmetic Sequence the difference between one term and the next is a constant.
     * <p></p>
     * Given <code>a = initialSeconds</code> and <code>d = commonDifference</code> the result array will be
     * {a * 1000, a+d * 1000, a+2d * 1000, ...} until it reaches the <code>limit</code> number of elements.
     *
     * @param initialSeconds    The initial value of the arithmetic sequence
     * @param commonDifference  The common difference between each value in the sequence
     * @param limit             The number of elements in the sequence
     *
     * @return  An array of milliseconds according to the specified arithmetic sequence
     */
    public static int[] arithmetic(int initialSeconds, int commonDifference, int limit) {
        int delay = initialSeconds;
        final int[] sequence = new int[limit];
        for (int i = 0; i < limit; i++) {
            sequence[i] = delay * 1000;
            delay += commonDifference;
        }
        return sequence;
    }

    /**
     * Generates a milliseconds geometric sequence according to the specified configuration.
     * <p></p>
     * In a Geometric Sequence each term is found by multiplying the previous term by a constant.
     * <p></p>
     * Given <code>a = initialSeconds</code> and <code>r = ratio</code> the result array will be
     * {a * 1000, ar * 1000, ar² * 1000, ...} until it reaches the <code>limit</code> number of elements.
     *
     * @param initialSeconds    The initial value of the geometric sequence
     * @param ratio  The common difference between each value in the sequence
     * @param limit             The number of elements in the sequence
     *
     * @return  An array of milliseconds according to the specified geometric sequence
     */
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
