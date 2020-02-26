import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

class CountriesServiceSolved implements CountriesService {

    @Override
    public Single<String> countryNameInCapitals(Country country) {

        return Single.just(country)
                .map(c -> c.getName().toUpperCase());
    }

    public Single<Integer> countCountries(List<Country> countries) {

        return Observable.fromIterable(countries)
                .map(c -> 1)
                .reduce(0, Integer::sum);
    }

    public Observable<Long> listPopulationOfEachCountry(List<Country> countries) {

        return Observable.fromIterable(countries)
                .map(Country::getPopulation);
    }

    @Override
    public Observable<String> listNameOfEachCountry(List<Country> countries) {

        return Observable.fromIterable(countries)
                .map(Country::getName);
    }

    @Override
    public Observable<Country> listOnly3rdAnd4thCountry(List<Country> countries) {

        return Observable.fromIterable(countries)
                .skip(2)
                .take(2);
    }

    @Override
    public Single<Boolean> isAllCountriesPopulationMoreThanOneMillion(List<Country> countries) {

        return Observable.fromIterable(countries)
                .all(country -> country.getPopulation() > 1_000_000);
    }

    @Override
    public Observable<Country> listPopulationMoreThanOneMillion(List<Country> countries) {

        return Observable.fromIterable(countries)
                .filter(country -> country.getPopulation() > 1_000_000);
    }

    @Override
    public Observable<Country> listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(final FutureTask<List<Country>> countriesFromNetwork) {
        return Observable.fromFuture(countriesFromNetwork, Schedulers.io())
                .flatMap(Observable::fromIterable)
                .filter(country -> country.getPopulation() > 1_000_000)
                .timeout(700, TimeUnit.MILLISECONDS, Observable.empty());
    }

    @Override
    public Observable<String> getCurrencyUsdIfNotFound(String countryName, List<Country> countries) {

        return Observable.fromIterable(countries)
                .filter(country -> country.getName().equals(countryName))
                .map(Country::getCurrency)
                .switchIfEmpty(Observable.just("USD"));
    }

    @Override
    public Observable<Long> sumPopulationOfCountries(List<Country> countries) {

        return Observable.fromIterable(countries)
                .map(Country::getPopulation)
                .reduce(0L, Long::sum)
                .toObservable();
    }

    @Override
    public Single<Map<String, Long>> mapCountriesToNamePopulation(List<Country> countries) {
        return Observable.fromIterable(countries)
                .doOnNext(country -> System.out.println(country.getName() + ", " + country.getPopulation()))
                .toMap(Country::getName, Country::getPopulation);
    }

    @Override
    public Observable<Long> sumPopulationOfCountries(Observable<Country> countryObservable1,
                                                     Observable<Country> countryObservable2) {
        return Observable.merge(countryObservable1, countryObservable2)
                .map(Country::getPopulation)
                .reduce(0L, Long::sum)
                .toObservable();
    }

    @Override
    public Single<Boolean> areEmittingSameSequences(Observable<Country> countryObservable1,
                                                    Observable<Country> countryObservable2) {

        return Observable.sequenceEqual(countryObservable1, countryObservable2);
    }
}
