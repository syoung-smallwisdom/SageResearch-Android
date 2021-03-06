/*
 * BSD 3-Clause License
 *
 * Copyright 2018  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.mobile_ui.inject;

import org.sagebionetworks.research.mobile_ui.inject.subcomponents.ShowActiveUIStepFragmentSubcomponent;
import org.sagebionetworks.research.mobile_ui.inject.subcomponents.ShowCompletionStepFragmentSubcomponent;
import org.sagebionetworks.research.mobile_ui.inject.subcomponents.ShowCountdownStepFragmentSubcomponent;
import org.sagebionetworks.research.mobile_ui.inject.subcomponents.ShowFormUIStepFragmentSubcomponent;
import org.sagebionetworks.research.mobile_ui.inject.subcomponents.ShowStepFragmentSubcomponent;
import org.sagebionetworks.research.mobile_ui.inject.subcomponents.ShowUIStepFragmentSubcomponent;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowActiveUIStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowCompletionStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowCountdownStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowUIStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowFormUIStepFragment;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = {ShowStepFragmentSubcomponent.class, ShowCompletionStepFragmentSubcomponent.class,
        ShowUIStepFragmentSubcomponent.class, ShowFormUIStepFragmentSubcomponent.class,
        ShowActiveUIStepFragmentSubcomponent.class, ShowCountdownStepFragmentSubcomponent.class})
public abstract class ShowStepModule {
    @Binds
    @IntoMap
    @FragmentKey(ShowActiveUIStepFragment.class)
    abstract AndroidInjector.Factory<? extends android.support.v4.app.Fragment>
    bindShowActiveUIStepFragmentInjectorFactory(ShowActiveUIStepFragmentSubcomponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(ShowStepFragment.class)
    abstract AndroidInjector.Factory<? extends android.support.v4.app.Fragment>
    bindShowStepFragmentInjectorFactory(ShowStepFragmentSubcomponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(ShowUIStepFragment.class)
    abstract AndroidInjector.Factory<? extends android.support.v4.app.Fragment>
    bindShowUIStepFragmentInjectorFactory(ShowUIStepFragmentSubcomponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(ShowCompletionStepFragment.class)
    abstract AndroidInjector.Factory<? extends android.support.v4.app.Fragment>
    bindShowCompletionStepFragmentInjectorFactory(ShowCompletionStepFragmentSubcomponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(ShowFormUIStepFragment.class)
    abstract AndroidInjector.Factory<? extends android.support.v4.app.Fragment>
    bindShowFormUIStepFragmentInjectorFactory(ShowFormUIStepFragmentSubcomponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(ShowCountdownStepFragment.class)
    abstract AndroidInjector.Factory<? extends android.support.v4.app.Fragment>
    bindShowCountdownStepFragmentInjectorFactory(ShowCountdownStepFragmentSubcomponent.Builder builder);
}
