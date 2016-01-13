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

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
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

    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    private HelpingPagerAdapter helpingPagerAdapter;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_helping;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        helpingPagerAdapter = new HelpingPagerAdapter(getSupportFragmentManager(), getFragments());
        viewPager.setAdapter(helpingPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private List<BenihFragment> getFragments() {
        return Arrays.asList(new ChatFragment(), new ImportantContactFragment());
    }

    @OnClick(R.id.button_danger)
    public void danger() {
        Toast.makeText(this, "Danger", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_map)
    public void openMap() {
        Toast.makeText(this, "Open Map", Toast.LENGTH_SHORT).show();
    }
}
