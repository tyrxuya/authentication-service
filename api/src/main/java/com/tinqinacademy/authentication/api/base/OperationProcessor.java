package com.tinqinacademy.authentication.api.base;

import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import io.vavr.control.Either;

public interface OperationProcessor<S extends OperationInput, T extends OperationOutput> {
    Either<ErrorOutput, T> process(S input);
}
