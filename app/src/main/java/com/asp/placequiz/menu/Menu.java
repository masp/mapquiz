package com.asp.placequiz.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.asp.placequiz.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class Menu extends LinearLayout {
    private Map<Integer, MenuChoice> mOptions;
    private MenuChoice mMenuListener;

    private Menu(Context context, Map<Integer, MenuChoice> options, MenuChoice menuListener) {
        super(context);
        mOptions = options;
        mMenuListener = menuListener;

        onCreate();
    }

    private void onCreate() {
        inflate(getContext(), R.layout.menu, null);
        setOrientation(VERTICAL);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int optionTitle : mOptions.keySet()) {
            Button option = (Button) inflater.inflate(R.layout.menu_button, null);
            option.setText(optionTitle);
            option.setTag(mOptions.get(optionTitle));
            option.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    MenuChoice choice = (MenuChoice) view.getTag();
                    choice.onSelect();

                    mMenuListener.onSelect();
                }
            });
            addView(option);
        }
    }

    public static class MenuBuilder {
        private Map<Integer, MenuChoice> choices = new LinkedHashMap<>();

        public MenuBuilder addChoice(int resName, MenuChoice choice) {
            if (!choices.containsKey(resName)) choices.put(resName, choice);
            return this;
        }

        public Menu build(Context context, MenuChoice menuListener) {
            return new Menu(context, choices, menuListener);
        }
    }

}
