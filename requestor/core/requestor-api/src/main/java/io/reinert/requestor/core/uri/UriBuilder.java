/*
 * Copyright 2014-2021 Danilo Reinert
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
package io.reinert.requestor.core.uri;

import java.util.Map;

/**
 * Utility class for building URIs from their components.
 * <p></p>
 * It is aware of templates.
 * <p></p>
 */
public abstract class UriBuilder {

    /**
     * Create a new instance representing a relative URI initialized from a URI path.
     *
     * @param path a URI path that will be used to initialize the UriBuilder, may contain URI template parameters.
     *
     * @return a new UriBuilder
     *
     * @throws IllegalArgumentException if path is null
     */
    public static UriBuilder fromPath(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        final UriBuilder builder = newInstance();
        builder.path(path);

        return builder;
    }

    /**
     * Create a new instance initialized from an existing URI.
     *
     * @param uri a URI that will be used to initialize the UriBuilder.
     *
     * @return a new UriBuilder
     *
     * @throws IllegalArgumentException if uri is null
     */
    public static UriBuilder fromUri(Uri uri) throws IllegalArgumentException {
        if (uri == null) {
            throw new IllegalArgumentException("Uri cannot be null");
        }

        UriBuilder builder = newInstance();
        builder.uri(uri);

        return builder;
    }

    /**
     * Create a new instance initialized from an existing URI.
     *
     * @param uri a URI that will be used to initialize the UriBuilder, URI template parameters are ignored.
     *
     * @return a new UriBuilder
     *
     * @throws IllegalArgumentException if uri is null
     */
    public static UriBuilder fromUri(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Uri cannot be null");
        }

        final UriBuilder builder = newInstance();
        builder.uri(Uri.create(uri));

        return builder;
    }

    public static UriBuilder newInstance() {
        return new UriBuilderImpl();
    }

    /**
     * Create a copy of the UriBuilder preserving its state.
     *
     * This is a more efficient means of creating a copy than constructing a new UriBuilder from a URI returned by the
     * build(Object...) method.
     *
     * @return  a copy of the UriBuilder.
     */
    public abstract UriBuilder clone();

    /**
     * Set the URI user of user-info part.
     *
     * @param user the URI user. A null value will unset userInfo (both user and password) component of the URI.
     *
     * @return the updated UriBuilder
     */
    public abstract UriBuilder user(String user);

    /**
     * Set the URI password of user-info part.
     *
     * @param password the URI user's password. A null value will unset password component of the user-info.
     *
     * @return the updated UriBuilder
     */
    public abstract UriBuilder password(String password);

    /**
     * Set the URI scheme.
     *
     * @param scheme the URI scheme. A null value will unset the URI scheme.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if scheme is invalid
     */
    public abstract UriBuilder scheme(String scheme) throws IllegalArgumentException;

    /**
     * Set the URI host.
     *
     * @param host the URI host. A null value will unset the host component of the URI.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if host is invalid.
     */
    public abstract UriBuilder host(String host) throws IllegalArgumentException;

    /**
     * Set the URI port.
     *
     * @param port the URI port, a negative value will unset an explicit port.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if port is invalid
     */
    public abstract UriBuilder port(int port) throws IllegalArgumentException;

    /**
     * Set the URI path. This method will overwrite any existing path and associated matrix parameters. Existing '/'
     * characters are preserved thus a single value can represent multiple URI path segments.
     *
     * @param path the path. A null value will unset the path component of the URI.
     *
     * @return the updated UriBuilder
     */
    public abstract UriBuilder path(String path);

    /**
     * Append path segments to the existing path. When constructing the final path, a '/' separator will be inserted
     * between the existing path and the first path segment if necessary and each supplied segment will also be
     * separated by '/'. Existing '/' characters are encoded thus a single value can only represent a single URI path
     * segment.
     *
     * @param segments the path segment values
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if segments or any element of segments is null
     */
    public abstract UriBuilder segment(Object... segments) throws IllegalArgumentException;

    /**
     * Append a matrix parameter to the existing set of matrix parameters of the current final segment of the URI path.
     * If multiple values are supplied the parameter will be added once per value. Note that the matrix parameters are
     * tied to a particular path segment; subsequent addition of path segments will not affect their position in the URI
     * path.
     *
     * @param name   the matrix parameter name
     * @param values the matrix parameter value(s), each object will be converted to a {@code String} using its {@code
     *               toString()} method.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if name or values is null
     * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
     */
    public abstract UriBuilder matrixParam(String name, Object... values) throws IllegalArgumentException;

    /**
     * Append a query parameter to the existing set of query parameters. If multiple values are supplied the parameter
     * will be added once per value.
     *
     * @param name   the query parameter name
     * @param values the query parameter value(s), each object will be converted to a {@code String} using its {@code
     *               toString()} method.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if name or values is null
     */
    public abstract UriBuilder queryParam(String name, Object... values) throws IllegalArgumentException;

    /**
     * Set the URI fragment.
     *
     * @param fragment the URI fragment. A null value will remove any existing fragment.
     *
     * @return the updated UriBuilder
     */
    public abstract UriBuilder fragment(String fragment);

    /**
     * Copies the non-null components of the supplied URI to the UriBuilder replacing any existing values for those
     * components.
     *
     * @param uri the URI to copy components from
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if uri is null
     */
    public abstract UriBuilder uri(Uri uri) throws IllegalArgumentException;

    /**
     * Build a URI, using the supplied values in order to replace any URI
     * template parameters. Values are converted to <code>String</code> using
     * their <code>toString</code> method and are then encoded to match the
     * rules of the URI component to which they pertain. All '%' characters
     * in the stringified values will be encoded.
     * The state of the builder is unaffected; this method may be called
     * multiple times on the same builder instance.
     * <p></p>
     *
     * All instances of the same template parameter
     * will be replaced by the same value that corresponds to the position of the
     * first instance of the template parameter. e.g. the template "{a}/{b}/{a}"
     * with values {"x", "y", "z"} will result in the the URI "x/y/x", <i>not</i>
     * "x/y/z".
     *
     * @param values a list of URI template parameter values
     *
     * @return the URI built from the UriBuilder
     *
     * @throws IllegalArgumentException if there are any URI template parameters
     * without a supplied value, or if a value is null.
     *
     * @throws UriBuilderException if a URI cannot be constructed based on the
     * current state of the builder.
     */
    public abstract Uri build(Object... values)
            throws IllegalArgumentException, UriBuilderException;

    /**
     * Build a URI, any URI template parameters will be replaced by the value
     * in the supplied map. Values are converted to <code>String</code> using
     * their <code>toString</code> method and are then encoded to match the
     * rules of the URI component to which they pertain. All '%' characters
     * in the stringified values will be encoded.
     * The state of the builder is unaffected; this method may be called
     * multiple times on the same builder instance.
     *
     * @param values a map of URI template parameter names and values
     *
     * @return the URI built from the UriBuilder
     *
     * @throws IllegalArgumentException if there are any URI template parameters
     * without a supplied value, or if a value is null.
     *
     * @throws UriBuilderException if a URI cannot be constructed based on the
     * current state of the builder.
     */
    public abstract Uri build(Map<String, ?> values);
}
