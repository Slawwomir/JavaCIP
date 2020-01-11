package Chapter6;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class QuoteTask implements Callable<TravelQuote> {
    private final TravelCompany company;
    private final TravelInfo travelInfo;

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public QuoteTask(TravelCompany company, TravelInfo travelInfo) {
        this.company = company;
        this.travelInfo = travelInfo;
    }

    public TravelQuote call() throws Exception {
        return company.solicitQuote(travelInfo);
    }

    public List<TravelQuote> getRankedTravelQuotes(
            TravelInfo travelInfo,
            Set<TravelCompany> travelCompanies,
            Comparator<TravelQuote> ranking,
            long time,
            TimeUnit unit
    ) throws InterruptedException {
        List<QuoteTask> tasks = travelCompanies.stream()
                .map(company -> new QuoteTask(company, travelInfo))
                .collect(Collectors.toList());

        List<Future<TravelQuote>> futures = executor.invokeAll(tasks, time, unit);

        List<TravelQuote> quotes = new ArrayList<>();
        Iterator<QuoteTask> iterator = tasks.iterator();

        for (Future<TravelQuote> f : futures) {
            QuoteTask task = iterator.next();
            try {
                quotes.add(f.get());
            } catch (ExecutionException e) {
                quotes.add(task.getFailureQuote(e.getCause()));
            } catch (CancellationException e) {
                quotes.add(task.getTimeoutQuote(e));
            }
        }

        quotes.sort(ranking);
        return quotes;
    }
}

interface TravelCompany {
    TravelQuote solicitQuote(TravelInfo travelInfo);
}

interface TravelInfo {

}

interface TravelQuote {

}

