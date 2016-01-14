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
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.ui.adapter.HelpingPagerAdapter;
import id.satusatudua.sigap.ui.fragment.ChatFragment;
import id.satusatudua.sigap.ui.fragment.ImportantContactFragment;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.fragment.BenihFragment;

/**
 * Created on : January 13, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class HelpingActivity extends BenihActivity {
    private static final String KEY_CASE = "extra_case";
    private static final String KEY_REPORTER = "extra_reporter";

    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.tab_layout) TabLayout tabLayout;

    private Case theCase;
    private User reporter;

    public static Intent generateIntent(Context context, Case theCase, User reporter) {
        Intent intent = new Intent(context, HelpingActivity.class);
        intent.putExtra(KEY_CASE, theCase);
        intent.putExtra(KEY_REPORTER, reporter);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_helping;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        resolveTheCase(savedInstanceState);
        resolveReporter(savedInstanceState);

        HelpingPagerAdapter helpingPagerAdapter = new HelpingPagerAdapter(getSupportFragmentManager(), getFragments());
        viewPager.setAdapter(helpingPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void resolveTheCase(Bundle savedInstanceState) {
        theCase = getIntent().getParcelableExtra(KEY_CASE);

        if (theCase == null && savedInstanceState != null) {
            theCase = savedInstanceState.getParcelable(KEY_CASE);
        }

        if (theCase == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void resolveReporter(Bundle savedInstanceState) {
        reporter = getIntent().getParcelableExtra(KEY_REPORTER);

        if (reporter == null && savedInstanceState != null) {
            reporter = savedInstanceState.getParcelable(KEY_REPORTER);
        }

        if (reporter == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private List<BenihFragment> getFragments() {
        return Arrays.asList(ChatFragment.newInstance(theCase, reporter), new ImportantContactFragment());
    }

    @OnClick(R.id.button_danger)
    public void danger() {
        Toast.makeText(this, "Danger", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_map)
    public void openMap() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                                   Uri.parse("http://maps.google.com/maps?daddr="
                                                     + theCase.getLatitude() + ","
                                                     + theCase.getLongitude()));
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CASE, theCase);
        outState.putParcelable(KEY_REPORTER, reporter);
    }
}
