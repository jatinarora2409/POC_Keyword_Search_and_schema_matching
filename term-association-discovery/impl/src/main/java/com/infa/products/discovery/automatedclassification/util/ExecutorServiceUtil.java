package com.infa.products.discovery.automatedclassification.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorServiceUtil implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceUtil.class);
    private ExecutorService executorService;
    private List<Future> futures;

    private ExecutorServiceUtil(int numOfThreads) {
        executorService = Executors.newFixedThreadPool(numOfThreads);
        futures = new LinkedList<>();
    }

    public static ExecutorServiceUtil newInstance(int numOfThreads) {
        return new ExecutorServiceUtil(numOfThreads);
    }

    public Future submit(Runnable runnableTask) {
        checkIfAlreadyClosed();
        Future future = executorService.submit(runnableTask);
        futures.add(future);
        return future;
    }

    public <T> Future<T> submit(Callable<T> callableTask) {
        checkIfAlreadyClosed();
        Future<T> future = executorService.submit(callableTask);
        futures.add(future);
        return future;
    }

    public List<Future> getFutures() {
        checkIfAlreadyClosed();
        return futures;
    }

    public boolean isAllTasksDone() {
        checkIfAlreadyClosed();
        for (Future future : futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    public void waitForAllTasksToComplete() throws ExecutionException, InterruptedException {
        checkIfAlreadyClosed();
        for (Future future : futures) {
            future.get();
        }
    }

    public void cancelAllTasks() {
        checkIfAlreadyClosed();
        futures.stream().forEach((future) -> {
            future.cancel(true);
        });
    }

    @Override
    public void close() {
        try {
            //todo: should wait or cancel all tasks?
            waitForAllTasksToComplete();
        } catch (ExecutionException e) {
            LOGGER.error("Execution Exception occurred when waiting for all tasks to complete before closing.", e);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted Exception occurred when waiting for all tasks to complete before closing.", e);
        }
        clearFutures();
        futures = null;
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    public void clearFutures() {
        futures.clear();
    }

    private void checkIfAlreadyClosed() {
        if (executorService == null || executorService.isShutdown() || futures == null) {
            throw new IllegalStateException("The Service could already be closed.");
        }
    }
}
