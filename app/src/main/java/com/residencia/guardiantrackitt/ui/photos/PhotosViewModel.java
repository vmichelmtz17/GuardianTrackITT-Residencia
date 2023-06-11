package com.residencia.guardiantrackitt.ui.photos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PhotosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PhotosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}