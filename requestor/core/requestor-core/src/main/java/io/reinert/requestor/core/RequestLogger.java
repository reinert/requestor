/*
 * Copyright 2022 Danilo Reinert
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reinert.requestor.core.header.Header;

/**
 * Logger class for requests.
 *
 * @author Danilo Reinert
 */
public class RequestLogger {

    public enum Part {
        OPTIONS,
        HEADERS,
        PAYLOAD
    }

    private static final Logger logger = Logger.getLogger("io.reinert.requestor");

    private Set<Part> parts = Collections.emptySet();
    private Level level = Level.FINE;
    private TimeUnit unit = TimeUnit.SECONDS;
    private String indent = " ";
    private String separator = ",";
    private String partSeparator = "\n";
    private String unset = "null";
    private boolean active = true;

    public Set<Part> getParts() {
        return parts;
    }

    public void setParts(Part... parts) {
        this.parts = new HashSet<Part>(Arrays.asList(parts));
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public String getIndent() {
        return indent;
    }

    public void setIndent(String indent) {
        this.indent = indent;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getPartSeparator() {
        return partSeparator;
    }

    public void setPartSeparator(String partSeparator) {
        this.partSeparator = partSeparator;
    }

    public String getUnset() {
        return unset;
    }

    public void setUnset(String unset) {
        this.unset = unset;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void log(SerializedRequest request) {
        if (!active) return;

        final StringBuilder sb = getLogBuilder(request);

        if (parts.contains(Part.OPTIONS)) {
            appendPart(sb, Part.OPTIONS);
            appendAuth(preItem(sb), request).append(separator);
            appendCharset(preItem(sb), request).append(separator);
            appendContentType(preItem(sb), request).append(separator);
            appendDelay(preItem(sb), request).append(separator);
            appendPolling(preItem(sb), request).append(separator);
            appendRetryPolicy(preItem(sb), request).append(separator);
            appendSkip(preItem(sb), request).append(separator);
            appendTimeout(preItem(sb), request);
        }

        if (parts.contains(Part.HEADERS)) {
            appendPart(sb, Part.HEADERS);
            if (request.getHeaders().isEmpty()) {
                preItem(sb).append(unset);
            } else {
                for (Header header : request.getHeaders()) {
                    preItem(sb).append(header.toString()).append(separator);
                }
            }
        }

        if (parts.contains(Part.PAYLOAD)) {
            appendPart(sb, Part.PAYLOAD);
            preItem(sb).append(request.getPayload().isEmpty() ? unset : request.getPayload());
        }

        logger.log(level, sb.toString());
    }

    private StringBuilder appendPart(StringBuilder sb, Part part) {
        return indent(sb.append(partSeparator), 2).append('[').append(part).append(']');
    }

    private StringBuilder preItem(StringBuilder sb) {
        return indent(sb, 1);
    }

    private StringBuilder indent(StringBuilder sb, int times) {
        for (int i = 0; i < times; i++) sb.append(indent);
        return sb;
    }

    private StringBuilder sep(StringBuilder sb) {
        return sb.append(separator);
    }

    private StringBuilder getLogBuilder(SerializedRequest request) {
        return new StringBuilder(request.getMethod() + " " + request.getUri());
    }

    private StringBuilder appendDelay(StringBuilder sb, SerializedRequest request) {
        return sb.append("delay").append('=')
                .append(unit.convert(request.getDelay(), TimeUnit.MILLISECONDS))
                .append(toSIAbbreviation(unit));
    }

    private StringBuilder appendTimeout(StringBuilder sb, SerializedRequest request) {
        return sb.append("timeout").append('=')
                .append(unit.convert(request.getTimeout(), TimeUnit.MILLISECONDS))
                .append(toSIAbbreviation(unit));
    }

    private StringBuilder appendContentType(StringBuilder sb, SerializedRequest request) {
        return sb.append("contentType").append('=')
                .append(request.getContentType() != null ? request.getContentType() : unset);
    }

    private StringBuilder appendRetryPolicy(StringBuilder sb, SerializedRequest request) {
        return sb.append("retryPolicy").append('=')
                .append(request.getRetryPolicy() != null ? request.getRetryPolicy().toString() : unset);
    }

    private StringBuilder appendAuth(StringBuilder sb, SerializedRequest request) {
        return sb.append("auth").append('=')
                .append(request.getAuth() != null ? request.getAuth().toString() : unset);
    }

    private StringBuilder appendSkip(StringBuilder sb, SerializedRequest request) {
        return sb.append("skip").append('=').append(Arrays.toString(request.getSkippedProcesses().toArray()));
    }

    private StringBuilder appendCharset(StringBuilder sb, SerializedRequest request) {
        return sb.append("charset").append('=').append(request.getCharset());
    }

    private StringBuilder appendPolling(StringBuilder sb, SerializedRequest request) {
        sb.append("polling").append('=');

        if (!request.isPolling()) {
            return sb.append(unset);
        }

        return sb.append(request.getPollingStrategy())
                .append(" each ")
                .append(unit.convert(request.getPollingInterval(), TimeUnit.MILLISECONDS))
                .append(toSIAbbreviation(unit))
                .append(" up to ")
                .append(request.getPollingLimit())
                .append('x');
    }

    private String toSIAbbreviation(final TimeUnit timeUnit) {
        if (timeUnit == null) {
            return "";
        }
        switch (timeUnit) {
            case DAYS:
                return "d";
            case HOURS:
                return "h";
            case MINUTES:
                return "min";
            case SECONDS:
                return "s";
            case MILLISECONDS:
                return "ms";
            case MICROSECONDS:
                return "\u03BCs"; // lower-greek-mu
            case NANOSECONDS:
                return "ns";
            default:
                return timeUnit.name();
        }
    }

}
