/*
 * Copyright (c) 2015 SatuSatuDua.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.satusatudua.sigap.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.Message;
import id.satusatudua.sigap.ui.adapter.PicturePagerAdapter;
import id.satusatudua.sigap.ui.fragment.PictureFragment;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : February 14, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class PictureActivity extends BenihActivity {
    private static final String KEY_MESSAGES = "extra_messages";
    private static final String KEY_POSITION = "extra_position";

    @Bind(R.id.pager) ViewPager viewPager;

    private List<Message> messages;
    private int position;

    public static Intent generateIntent(Context context, List<Message> messages, int position) {
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putParcelableArrayListExtra(KEY_MESSAGES, (ArrayList<Message>) messages);
        intent.putExtra(KEY_POSITION, position);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_picture;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        resolveData(savedInstanceState);

        viewPager.setAdapter(new PicturePagerAdapter(getSupportFragmentManager(), generateFragments()));
        viewPager.setCurrentItem(position);
    }

    private void resolveData(Bundle savedInstanceState) {
        messages = getIntent().getParcelableArrayListExtra(KEY_MESSAGES);
        if (messages == null && savedInstanceState != null) {
            messages = savedInstanceState.getParcelableArrayList(KEY_MESSAGES);
        }

        if (messages == null) {
            finish();
            return;
        }

        position = getIntent().getIntExtra(KEY_POSITION, 0);
        if (position == 0 && savedInstanceState != null) {
            position = savedInstanceState.getInt(KEY_POSITION, 0);
        }
    }

    private List<PictureFragment> generateFragments() {
        List<PictureFragment> fragments = new ArrayList<>();
        int size = messages.size();
        for (int i = 0; i < size; i++) {
            PictureFragment fragment = new PictureFragment();
            fragment.setData(messages.get(i));
            fragments.add(fragment);
        }

        return fragments;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_MESSAGES, (ArrayList<Message>) messages);
        outState.putInt(KEY_POSITION, position);
    }
}
