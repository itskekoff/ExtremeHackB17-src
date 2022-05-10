package org.apache.http;

import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

public interface Header {
    public String getName();

    public String getValue();

    public HeaderElement[] getElements() throws ParseException;
}

