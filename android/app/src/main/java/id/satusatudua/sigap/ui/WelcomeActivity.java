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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.ui.adapter.WelcomePagerAdapter;
import id.satusatudua.sigap.ui.fragment.WelcomeFragment;
import id.zelory.benih.BenihActivity;

/**
 * Created on : November 29, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class WelcomeActivity extends BenihActivity implements ViewPager.OnPageChangeListener {

    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.icon_page_1) View iconPage1;
    @Bind(R.id.icon_page_2) View iconPage2;
    @Bind(R.id.icon_page_3) View iconPage3;
    @Bind(R.id.icon_page_4) View iconPage4;

    private int pos = 0;

    @Override
    protected int getActivityView() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {

        List<WelcomeFragment> fragments = Arrays.asList(WelcomeFragment.newInstance(1), WelcomeFragment.newInstance(2),
                                                        WelcomeFragment.newInstance(3), WelcomeFragment.newInstance(4));
        WelcomePagerAdapter pagerAdapter = new WelcomePagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
    }

    @OnClick(R.id.login)
    public void login() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @OnClick(R.id.register)
    public void register() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (pos != position) {
            pos = position;
            resolveIcon();
        }
    }

    private void resolveIcon() {
        iconPage1.setBackgroundResource(pos == 0 ? R.drawable.circle_primary : R.drawable.circle_red_white);
        iconPage2.setBackgroundResource(pos == 1 ? R.drawable.circle_primary : R.drawable.circle_red_white);
        iconPage3.setBackgroundResource(pos == 2 ? R.drawable.circle_primary : R.drawable.circle_red_white);
        iconPage4.setBackgroundResource(pos == 3 ? R.drawable.circle_primary : R.drawable.circle_red_white);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
