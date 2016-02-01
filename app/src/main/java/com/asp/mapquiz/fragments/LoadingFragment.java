package com.asp.mapquiz.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.asp.mapquiz.R;

public class LoadingFragment extends Fragment {
    public interface LoadingListener {
        void onUserFail();
    }

    private LoadingListener mListener;

    public static LoadingFragment createFragment(LoadingListener listener) {
        LoadingFragment fragment = new LoadingFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.loading_fragment, null, false);

        Button loadingButton = (Button) v.findViewById(R.id.loading_button);
        loadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Snackbar bar = Snackbar.make(parent, R.string.try_again_loading_map, Snackbar.LENGTH_SHORT);
                bar.setAction(R.string.not_loading, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.onUserFail();
                            bar.dismiss();
                        }
                    });
                bar.show();
            }
        });

        return v;
    }
}
