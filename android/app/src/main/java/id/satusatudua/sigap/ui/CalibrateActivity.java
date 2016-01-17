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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : January 17, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class CalibrateActivity extends BenihActivity {

    @Bind(R.id.spinner) Spinner spinner;
    @Bind(R.id.test_message) TextView testMessage;
    @Bind(R.id.shaking_image) ImageView shakingImage;
    @Bind(R.id.status) TextView status;

    private int currentCount;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_calibrate;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {

        int pos = CacheManager.pluck().getShakeCount() - 2;
        testMessage.setText("Goyangkan ponsel sebanyak " + (pos + 2) + " kali.");
        spinner.setSelection(pos);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CacheManager.pluck().setShakeCount(position + 2);
                testMessage.setText("Goyangkan ponsel sebanyak " + (position + 2) + " kali.");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
