package com.tinqinacademy.authentication.core.converters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public abstract class AbstractConverter<S, T> implements Converter<S, T> {
    @Override
    public T convert(S source) {
        log.info("Start convert from {} to {}",
                source.getClass().getSimpleName(),
                getTargetClass().getSimpleName());

        T result = doConvert(source);

        log.info("End convert result: {}", result);

        return result;
    }

    protected abstract Class<T> getTargetClass();

    protected abstract T doConvert(S source);
}
