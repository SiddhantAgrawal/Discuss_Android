package com.discuss.ui.category;

import com.discuss.data.DataRetriever;
import com.discuss.datatypes.PersonCategoryPreference;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class CategorySelectorPresenterImpl implements CategorySelectorPresenter {
    private final DataRetriever dataRetriever;
    private List<PersonCategoryPreference> personCategoryPreferences;
    private Single<List<PersonCategoryPreference>> questionObservable;

    @Inject
    public CategorySelectorPresenterImpl(DataRetriever dataRetriever) {
        this.dataRetriever = dataRetriever;
    }

    private void checkPreConditions() {
        if (null == dataRetriever || null == personCategoryPreferences) {
            init(onCompleted);
        }
    }

    private final Action1<List<PersonCategoryPreference>> onNextQuestionsList = new Action1<List<PersonCategoryPreference>>() {
        @Override
        public void call(List<PersonCategoryPreference> fetchedQuestions) {
            personCategoryPreferences.addAll(fetchedQuestions);
        }
    };

    private final Action1<Throwable> onError = throwable -> {};

    private final Action0 onCompleted = () -> {};


    @Override
    public void init(Action0 onCompletedAction) {
        personCategoryPreferences = new CopyOnWriteArrayList<>(); /* update operations are in bulk and not to often to degrade the performance  */
        questionObservable = dataRetriever.   /* hot observable */
                getUserCategoryPreference(""). /* TODO(Deepak): add proper values */
                doOnSuccess(preferences -> personCategoryPreferences.addAll(preferences)).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {});
        return Observable.just(true);
    }

    @Override
    public Observable<PersonCategoryPreference> get(int position) {
        return Observable.just(personCategoryPreferences.get(position));

    }

    @Override
    public int size() {
        return (null == personCategoryPreferences) ? 0 : personCategoryPreferences.size();
    }
}
