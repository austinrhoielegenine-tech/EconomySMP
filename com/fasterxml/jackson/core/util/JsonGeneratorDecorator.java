package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public interface JsonGeneratorDecorator {
   JsonGenerator decorate(JsonFactory var1, JsonGenerator var2);
}
