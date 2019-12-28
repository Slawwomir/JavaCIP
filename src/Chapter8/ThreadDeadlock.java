package Chapter8;

import java.util.concurrent.*;

public class ThreadDeadlock {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public class RenderPageTask implements Callable<String> {

        @Override
        public String call() throws Exception {
            Future<String> header, footer;
            header = executorService.submit(new LoadFileTask("header.html"));
            footer = executorService.submit(new LoadFileTask("footer.html"));
            String body = renderBody();
            // will deadlock -- task waiting for result of subtask
            return header.get() + body + footer.get();
        }

        private String renderBody() {
            return null;
        }
    }

    public static class LoadFileTask implements Callable<String> {
        private String value;

        LoadFileTask(String s) {
            this.value = s;
        }

        @Override
        public String call() throws Exception {
            return value;
        }
    }

    public String start() throws Exception {
        RenderPageTask renderPageTask = new RenderPageTask();
        return executorService.submit(renderPageTask).get();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new ThreadDeadlock().start());
    }
}
