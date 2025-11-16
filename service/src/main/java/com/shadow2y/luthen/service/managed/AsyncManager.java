package com.shadow2y.luthen.service.managed;

import com.shadow2y.commons.executor.intf.AsyncExecutorConfig;
import io.dropwizard.lifecycle.Managed;

public class AsyncManager extends com.shadow2y.commons.executor.AsyncExecutorManager implements Managed {

        public AsyncManager(AsyncExecutorConfig config) {
            super(config);
        }

}
