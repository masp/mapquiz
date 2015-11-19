package com.asp.mapquiz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.asp.mapquiz.R;
import com.asp.mapquiz.game.Mode;
import com.asp.mapquiz.menu.Menu;
import com.asp.mapquiz.menu.MenuChoice;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    private static final String TAG = "MenuFragment";

    /**
     * Create menu fragment that handles the UI changes for the menu
     *
     * @param context Needed because Fragment hasn't been initialized yet
     * @param menus Menus in order of start to finish
     * @return
     */
    public static MenuFragment createFragment(Context context, MenuCompletionListener listener,
                                              Menu.MenuBuilder... menus) {
        MenuFragment fragment = new MenuFragment();

        fragment.mListener = listener;
        fragment.mMenus = new ArrayList<>();
        for (Menu.MenuBuilder menu : menus) {
            fragment.mMenus.add(menu.build(context, fragment.mMenuListener));
        }
        fragment.mCurrentMenuIndex = 0; // start at root menu

        return fragment;
    }

    private FrameLayout mContainer;

    private final MenuChoice mMenuListener = new MenuChoice() {
        @Override
        public void onSelect() {
            boolean movedOn = nextMenu();
            if (!movedOn) mListener.onComplete();
        }
    };
    private MenuCompletionListener mListener;

    private List<Menu> mMenus;
    private int mCurrentMenuIndex;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mContainer = (FrameLayout) inflater.inflate(R.layout.menu_fragment, null);
        mContainer.addView(mMenus.get(mCurrentMenuIndex));
        return mContainer;
    }

    public boolean nextMenu() {
        if (mCurrentMenuIndex + 1 == mMenus.size()) return false;
        mCurrentMenuIndex++;
        updateMenuView();
        return true;
    }

    public boolean prevMenu() {
        if (mCurrentMenuIndex == 0) return false;
        mCurrentMenuIndex--;
        updateMenuView();
        return true;
    }

    public void updateMenuView() {
        mContainer.removeAllViewsInLayout();
        mContainer.addView(mMenus.get(mCurrentMenuIndex));
    }

    public interface MenuCompletionListener {
        void onComplete();
    }
}
