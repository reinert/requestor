package io.reinert.requestor;

/**
 * Represents a hypermedia link according to <a href="https://tools.ietf.org/html/rfc5988">RFC 5988</a>.
 *
 * @author Danilo Reinert
 */
public interface Link {

    String getAnchor();

    String getHrefLang();

    String getMedia();

    String getRel();

    String getRev();

    String getTitle();

    String getType();

    String getUri();
}
