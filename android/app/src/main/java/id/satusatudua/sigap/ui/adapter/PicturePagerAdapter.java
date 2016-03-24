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

package id.satusatudua.sigap.ui.adapter;

import android.support.v4.app.FragmentManager;

import java.util.List;

import id.satusatudua.sigap.ui.fragment.PictureFragment;
import id.zelory.benih.ui.adapter.BenihPagerAdapter;


/**
 * Created on : February 14, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class PicturePagerAdapter extends BenihPagerAdapter<PictureFragment> {
    public PicturePagerAdapter(FragmentManager fm, List<PictureFragment> pictureFragments) {
        super(fm, pictureFragments);
    }

    @Override
    public PictureFragment getItem(int position) {
        return fragments.get(position);
    }
}
