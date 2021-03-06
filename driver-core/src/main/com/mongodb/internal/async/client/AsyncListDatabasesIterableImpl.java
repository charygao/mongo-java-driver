/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.internal.async.client;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.internal.async.AsyncBatchCursor;
import com.mongodb.internal.operation.AsyncOperations;
import com.mongodb.internal.operation.AsyncReadOperation;
import com.mongodb.lang.Nullable;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

import static com.mongodb.assertions.Assertions.notNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

final class AsyncListDatabasesIterableImpl<TResult> extends AsyncMongoIterableImpl<TResult> implements AsyncListDatabasesIterable<TResult> {
    private AsyncOperations<BsonDocument> operations;
    private final Class<TResult> resultClass;

    private long maxTimeMS;
    private Bson filter;
    private Boolean nameOnly;
    private Boolean authorizedDatabasesOnly;

    AsyncListDatabasesIterableImpl(@Nullable final AsyncClientSession clientSession, final Class<TResult> resultClass,
                                   final CodecRegistry codecRegistry, final ReadPreference readPreference,
                                   final OperationExecutor executor, final boolean retryReads) {
        super(clientSession, executor, ReadConcern.DEFAULT, readPreference, retryReads); // TODO: read concern?
        this.operations = new AsyncOperations<BsonDocument>(BsonDocument.class, readPreference, codecRegistry, retryReads);
        this.resultClass = notNull("clazz", resultClass);
    }

    @Override
    public AsyncListDatabasesIterable<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        notNull("timeUnit", timeUnit);
        this.maxTimeMS = MILLISECONDS.convert(maxTime, timeUnit);
        return this;
    }


    @Override
    public AsyncListDatabasesIterable<TResult> batchSize(final int batchSize) {
        super.batchSize(batchSize);
        return this;
    }

    @Override
    public AsyncListDatabasesIterable<TResult> filter(@Nullable final Bson filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public AsyncListDatabasesIterable<TResult> nameOnly(@Nullable final Boolean nameOnly) {
        this.nameOnly = nameOnly;
        return this;
    }

    @Override
    public AsyncListDatabasesIterable<TResult> authorizedDatabasesOnly(@Nullable final Boolean authorizedDatabasesOnly) {
        this.authorizedDatabasesOnly = authorizedDatabasesOnly;
        return this;
    }

    @Override
    AsyncReadOperation<AsyncBatchCursor<TResult>> asAsyncReadOperation() {
        return operations.listDatabases(resultClass, filter, nameOnly, maxTimeMS, authorizedDatabasesOnly);
    }
}
