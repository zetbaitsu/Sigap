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

package id.satusatudua.sigap.ui.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.CandidateHelper;
import id.satusatudua.sigap.data.model.GuardCandidate;
import id.zelory.benih.ui.adapter.viewholder.BenihItemViewHolder;

import static id.zelory.benih.ui.adapter.BenihRecyclerAdapter.OnItemClickListener;
import static id.zelory.benih.ui.adapter.BenihRecyclerAdapter.OnLongItemClickListener;

/**
 * Created on : January 09, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class GuardViewHolder extends BenihItemViewHolder<GuardCandidate> {

    @Bind(R.id.name) TextView name;
    private boolean reporter;

    public GuardViewHolder(View itemView, OnItemClickListener itemClickListener, OnLongItemClickListener longItemClickListener) {
        super(itemView, itemClickListener, longItemClickListener);
    }

    public void setReporter(boolean reporter) {
        this.reporter = reporter;
    }

    @Override
    public void bind(GuardCandidate guardCandidate) {
        name.setText(guardCandidate.getUser().getName());
        if (reporter) {
            name.setBackgroundResource(R.color.colorPrimary);
        } else if (guardCandidate.getGuardingStatus().equals(GuardCandidate.GuardingStatus.MENUNGGU)) {
            name.setBackgroundResource(R.color.secondary_text);
        } else if (guardCandidate.getGuardingStatus().equals(GuardCandidate.GuardingStatus.MENOLONG)) {
            name.setBackgroundResource(R.color.colorAccent);
        }
    }
}
