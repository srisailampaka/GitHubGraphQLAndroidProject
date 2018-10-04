package com.github.graphql.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Global executor pools for the whole application.
 */
class AppExecutors(private val diskIO: Executor, private val networkIO: Executor, private val main: Executor) {

    fun disk(): Executor {
        return diskIO
    }

    fun network(): Executor {
        return networkIO
    }

    fun ui(): Executor {
        return main
    }


    companion object {
        var executor: AppExecutors? = null
        fun getInstance(): AppExecutors {
            if (executor == null) {
                executor = AppExecutors(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(ExecutorProperties.NETWORK_IO_POOL_SIZE), getMainThreadExecutor())
            }
            return executor as AppExecutors
        }

        private fun getMainThreadExecutor(): Executor {
            return object : Executor {
                private val mainThreadHandler = Handler(Looper.getMainLooper())

                override fun execute(command: Runnable) {
                    mainThreadHandler.post(command)
                }
            }
        }

        private object ExecutorProperties {
            const val NETWORK_IO_POOL_SIZE = 3
        }
    }

}

