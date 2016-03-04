package com.asp.placequiz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.asp.placequiz.R;
import com.asp.placequiz.menu.Menu;
import com.asp.placequiz.menu.MenuChoice;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    private static final String TAG = "MenuFragment";

    /**
     * Create menu fragment that handles the UI changes for the menu
     *
     * @param context Needed because Fragment hasn't been initialized yet
     * @return
     */
    public static MenuFragment createFragment(Context context, Menu.MenuBuilder rootMenu,
                                              MenuCompletionListener listener) {
        MenuFragment fragment = new MenuFragment();

        fragment.mListener = listener;
        fragment.addMenu(context, rootMenu);
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

    private List<Menu> mMenus = new ArrayList<>();;
    private int mCurrentMenuIndex;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mContainer = (FrameLayout) inflater.inflate(R.layout.menu_fragment, null);
        if (mMenus.size() == 0) return mContainer;
        mContainer.addView(mMenus.get(mCurrentMenuIndex));
        return mContainer;
    }

    public MenuFragment addMenu(Context context, Menu.MenuBuilder menu) {
        mMenus.add(menu.build(context, mMenuListener));
        return this;
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
