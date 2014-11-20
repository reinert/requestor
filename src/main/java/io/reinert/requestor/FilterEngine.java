package io.reinert.requestor;

public class FilterEngine {

    private final FilterManager filterManager;

    public FilterEngine(FilterManager filterManager) {
        this.filterManager = filterManager;
    }

    public <R extends Request & RequestBuilder> void applyRequestFilters(R request) {
        for (RequestFilter filter : filterManager.getRequestFilters()) {
            filter.filter(request);
        }
    }

    public void applyResponseFilters(Request request, Response response) {
        for (ResponseFilter filter : filterManager.getResponseFilters()) {
            filter.filter(request, response);
        }
    }
}
