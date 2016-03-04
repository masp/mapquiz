package com.asp.placequiz.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.asp.placequiz.R;

public class NextButtonFragment extends Fragment {
    public interface OnNextPressedListener {
        void onNextPressed();

        void onDonePressed();
    }

    private boolean mIsCorrect;
    private boolean mShowDoneButton;

    public static NextButtonFragment createFragment(boolean showDoneButton, boolean isCorrect,
                                                    OnNextPressedListener listener) {
        NextButtonFragment fragment = new NextButtonFragment();
        fragment.mListener = listener;
        fragment.mIsCorrect = isCorrect;
        fragment.mShowDoneButton = showDoneButton;
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
                mListener.onNextPressed();
            }
        });

        Button doneButton = (Button) v.findViewById(R.id.done_button);
        doneButton.setVisibility(mShowDoneButton ? View.VISIBLE : View.GONE);
        if (!mShowDoneButton) {
            int size = (int) getResources().getDimension(R.dimen.loading_button_size);
            nextButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDonePressed();
            }
        });

        return v;
    }
}
