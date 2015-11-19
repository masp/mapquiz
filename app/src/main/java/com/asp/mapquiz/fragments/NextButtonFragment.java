package com.asp.mapquiz.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.asp.mapquiz.R;

public class NextButtonFragment extends Fragment {
    public interface OnNextPressedListener {
        void onPressed();
    }

    private boolean mIsCorrect;

    public static NextButtonFragment createFragment(boolean isCorrect, OnNextPressedListener listener) {
        NextButtonFragment fragment = new NextButtonFragment();
        fragment.mListener = listener;
        fragment.mIsCorrect = isCorrect;
        return fragment;
    }

    private OnNextPressedListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.next_button_fragment, null, false);

        Button nextButton = (Button) v.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPressed();
            }
        });

        return v;
    }
}
