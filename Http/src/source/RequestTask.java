package source;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hyc on 16-11-9.
 */
public class RequestTask {

    private ExecutorService _executors = Executors.newCachedThreadPool();


    public RequestTask() {

    }

    public void addTask(Request request) {
        DefaultTask task = new DefaultTask(request);
        _executors.execute(task);

    }


    private static class DefaultTask implements Runnable {

        private final Request _request;

        public DefaultTask(Request request) {
            _request = request;
        }

        @Override
        public void run() {

        }
    }
}
