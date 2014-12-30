/*
 * Copyright 2014 Danilo Reinert
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
package io.reinert.requestor;

/**
 * Progress data of requests.
 *
 * @author Danilo Reinert
 */
public interface RequestProgress {

    boolean isLengthComputable();

    /**
     * Returns the loaded amount of the request.
     * <p/>
     *
     * If length is not computable, then 0 is returned.
     *
     * @return The loaded amount if available, 0 otherwise
     */
    double getLoaded();

    /**
     * Returns the total amount of the request.
     * <p/>
     *
     * If length is not computable, then 0 is returned.
     *
     * @return The total amount if available, 0 otherwise
     */
    double getTotal();

    /**
     * Returns the completed amount of the request in a interval [0,1].
     * This value is calculated by dividing the loaded amount by the total.
     * <p/>
     *
     * If length is not computable, then 0 is returned.
     *
     * @return The completed amount if available, 0 otherwise
     */
    double getCompletedFraction();

    /**
     * Returns the completed amount of the request in a interval [0,1] multiplied by the given factor.
     * This value is calculated by dividing the loaded amount by the total and then multiplying by the factor.
     * <p/>
     *
     * If length is not computable, then 0 is returned.
     *
     * @return The completed amount if available, 0 otherwise
     */
    double getCompletedFraction(double multiplyingFactor);
}
