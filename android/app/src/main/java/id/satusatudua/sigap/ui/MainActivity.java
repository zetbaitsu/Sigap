package id.satusatudua.sigap.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.ui.adapter.MainPagerAdapter;
import id.satusatudua.sigap.ui.fragment.HistoriesFragment;
import id.satusatudua.sigap.ui.fragment.ImportantContactFragment;
import id.satusatudua.sigap.ui.fragment.SettingFragment;
import id.satusatudua.sigap.ui.fragment.TrustedsFragment;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.fragment.BenihFragment;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */

public class MainActivity extends BenihActivity implements TabLayout.OnTabSelectedListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.view_pager) ViewPager viewPager;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        setUpViewPager();
        setUpTabLayout();
    }

    private void setUpViewPager() {
        List<BenihFragment> fragments = new ArrayList<>();
        fragments.add(ImportantContactFragment.newInstance(true));
        fragments.add(new TrustedsFragment());
        fragments.add(new HistoriesFragment());
        fragments.add(new SettingFragment());

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    private void setUpTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_contact);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_trusted_grey);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_case_grey);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_setting_grey);
        tabLayout.setOnTabSelectedListener(this);
    }

    @OnClick(R.id.button_profile)
    public void openProfile() {

    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                setTitle("Kontak Darurat");
                tab.setIcon(R.drawable.ic_contact);
                break;
            case 1:
                setTitle("Daftar Terpercaya");
                tab.setIcon(R.drawable.ic_trusted);
                break;
            case 2:
                setTitle("Riwayat Aktifitas");
                tab.setIcon(R.drawable.ic_case);
                break;
            case 3:
                setTitle("Pengaturan");
                tab.setIcon(R.drawable.ic_setting);
                break;
        }
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                tab.setIcon(R.drawable.ic_contact_grey);
                break;
            case 1:
                tab.setIcon(R.drawable.ic_trusted_grey);
                break;
            case 2:
                tab.setIcon(R.drawable.ic_case_grey);
                break;
            case 3:
                tab.setIcon(R.drawable.ic_setting_grey);
                break;
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
