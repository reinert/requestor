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
package io.reinert.requestor.uri;

/**
 * Represents a URI.
 */
public class Uri implements Comparable<Uri> {

    private String scheme;
    private String user;
    private String password;
    private String host;
    private Integer port;
    private String path;
    private String query;
    private String fragment;
    private String uriString;

    public Uri(String scheme, String user, String password, String host, Integer port, String path, String query,
               String fragment) {
        // TODO: validate
        this.scheme = scheme;
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
    }

    public String getScheme() {
        return scheme;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getFragment() {
        return fragment;
    }

    @Override
    public String toString() {
        if (uriString == null) {
            StringBuilder uri = new StringBuilder();

            if (scheme != null) {
                uri.append(scheme).append("://");
            }

            if (user != null) {
                uri.append(user);
                if (password != null) {
                    uri.append(':').append(password);
                }
                uri.append('@');
            }

            if (host != null) {
                uri.append(host);
            }

            if (port != null) {
                uri.append(':').append(port);
            }

            uri.append('/');

            if (path != null) {
                uri.append(path);
            }

            if (query != null) {
                uri.append('?').append(query);
            }

            if (fragment != null) {
                uri.append('#').append(fragment);
            }

            uriString = uri.toString();
        }

        return uriString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Uri)) {
            return false;
        }

        final Uri uri = (Uri) o;

        if (fragment != null ? !fragment.equals(uri.fragment) : uri.fragment != null) {
            return false;
        }
        if (host != null ? !host.equals(uri.host) : uri.host != null) {
            return false;
        }
        if (password != null ? !password.equals(uri.password) : uri.password != null) {
            return false;
        }
        if (path != null ? !path.equals(uri.path) : uri.path != null) {
            return false;
        }
        if (port != null ? !port.equals(uri.port) : uri.port != null) {
            return false;
        }
        if (query != null ? !query.equals(uri.query) : uri.query != null) {
            return false;
        }
        if (scheme != null ? !scheme.equals(uri.scheme) : uri.scheme != null) {
            return false;
        }
        if (user != null ? !user.equals(uri.user) : uri.user != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = scheme != null ? scheme.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (fragment != null ? fragment.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Uri that) {
        if (this.scheme.compareTo(that.scheme) < 0) {
            return -1;
        } else if (this.scheme.compareTo(that.scheme) > 0) {
            return 1;
        }

        if (this.host.compareTo(that.host) < 0) {
            return -1;
        } else if (this.host.compareTo(that.host) > 0) {
            return 1;
        }

        if (this.port.compareTo(that.port) < 0) {
            return -1;
        } else if (this.port.compareTo(that.port) > 0) {
            return 1;
        }

        if (this.path.compareTo(that.path) < 0) {
            return -1;
        } else if (this.path.compareTo(that.path) > 0) {
            return 1;
        }

        if (this.query.compareTo(that.query) < 0) {
            return -1;
        } else if (this.query.compareTo(that.query) > 0) {
            return 1;
        }

        if (this.fragment.compareTo(that.fragment) < 0) {
            return -1;
        } else if (this.fragment.compareTo(that.fragment) > 0) {
            return 1;
        }

        if (this.user.compareTo(that.user) < 0) {
            return -1;
        } else if (this.user.compareTo(that.user) > 0) {
            return 1;
        }

        if (this.password.compareTo(that.password) < 0) {
            return -1;
        } else if (this.password.compareTo(that.password) > 0) {
            return 1;
        }

        return 0;
    }
}
