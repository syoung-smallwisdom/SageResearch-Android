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

package org.sagebionetworks.research.mobile_ui.recorder.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import org.sagebionetworks.research.domain.async.RecorderType;
import org.sagebionetworks.research.mobile_ui.recorder.Recorder;
import org.sagebionetworks.research.mobile_ui.recorder.RecorderActionType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The RecorderService handles the recorders that are needed for the task. Recorders can do things such as record
 * audio, phones motion, etc. Every recorder runs on it's own thread.
 *
 * This service supports both being started and bound to. Intents passed to the service should have the following
 * extras
 *      RECORDER_ACTION_KEY -> one of the constants in RecorderActionType corresponding to the action that should be
 *                             performed.
 *
 *      TASK_ID_KEY -> the UUID of the task.
 *
 *      RECORDER_ID_KEY -> the identifier of the recorder to perform the action on.
 *
 *      If starting a recorder:
 *      RECORDER_TYPE_KEY -> one of the constants in RecorderType corresponding to the type of recorder to create
 *                           if one is not already created.
 */
public class RecorderService extends Service {
    public static final String RECORDER_TYPE_KEY = "RECORDER_TYPE";
    public static final String TASK_ID_KEY = "TASK_ID";
    public static final String RECORDER_ID_KEY = "RECORDER_ID";
    public static final String RECORDER_ACTION_KEY = "RECORDER_ACTION";

    protected IBinder serviceBinder;
    // Maps Task Id to a Map of Recorder Id to Recorder.
    protected Map<UUID, Map<String, Recorder>> recorderMapping;

    public RecorderService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.serviceBinder = new RecorderBinder();
        this.recorderMapping = new HashMap<>();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return this.serviceBinder;
    }

    /**
     * Starts the recorder with the given identifier, or a recorder with this id and type and starts it.
     * @param taskIdentifier The identifier of the task the recorder belongs to.
     * @param recorderIdentifier The identifier of the recorder to start.
     * @param recorderType The type of th recorder to start.
     */
    public void startRecorder(@NonNull UUID taskIdentifier, @NonNull String recorderIdentifier,
            @RecorderType String recorderType) {
        Recorder recorder = this.getRecorder(taskIdentifier, recorderIdentifier);
        if (recorder == null) {
            recorder = createRecorder(taskIdentifier, recorderIdentifier, recorderType);
        }

        recorder.start();
    }

    /**
     * Stops the recorder with the given identifier.
     * @param taskIdentifier The identifier of the task the recorder belongs to.
     * @param recorderIdentifier The identifier of the recorder to stop.
     */
    public void stopRecorder(@NonNull UUID taskIdentifier, @NonNull String recorderIdentifier) {
        Recorder recorder = this.getRecorder(taskIdentifier, recorderIdentifier);
        if (recorder == null) {
            throw new IllegalArgumentException("Cannot stop recorder that isn't started.");
        }

        recorder.stop();
    }

    /**
     * Cancels the recorder with the given identifier.
     * @param taskIdentifier The identifier of the task the recorder belongs to.
     * @param recorderIdentifier The identifier of the recorder to cancel.
     */
    public void cancelRecorder(@NonNull UUID taskIdentifier, @NonNull String recorderIdentifier) {
        Recorder recorder = this.getRecorder(taskIdentifier, recorderIdentifier);
        if (recorder == null) {
            throw new IllegalArgumentException("Cannot cancel recorder that isn't started.");
        }

        recorder.cancel();
    }

    private Recorder getRecorder(@NonNull UUID taskIdentifier, @NonNull String recorderIdentifier) {
        Map<String, Recorder> taskRecorderMap = this.recorderMapping.get(taskIdentifier);
        if (taskRecorderMap == null) {
            return null;
        }

        return taskRecorderMap.get(recorderIdentifier);
    }


    /**
     * Starts the command defined by the Intent with the given startId. The Intent passed to this method should have
     * the Recorder to perform the action on, and a @RecorderActionType String that describes what action to perform
     * on the Recorder as part of it's extra's
     * @param intent the Intent describing the command to start, should have a Recorder and ActionType as part of the
     *               extra's
     * @param flags the flags for this command.
     * @param startId the id of the command to start.
     * @return An int which indicates what semantics the system should use for the service's current started state.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        @RecorderActionType String actionType = intent.getStringExtra(RECORDER_ACTION_KEY);
        UUID taskIdentifier = (UUID)intent.getSerializableExtra(TASK_ID_KEY);
        String recorderIdentifier = intent.getStringExtra(RECORDER_ID_KEY);
        switch (actionType) {
            case RecorderActionType.START:
                @RecorderType String recorderType = intent.getStringExtra(RECORDER_TYPE_KEY);
                this.startRecorder(taskIdentifier, recorderIdentifier, recorderType);
                break;
            case RecorderActionType.STOP:
                this.stopRecorder(taskIdentifier, recorderIdentifier);
                break;
            case RecorderActionType.CANCEL:
                this.cancelRecorder(taskIdentifier, recorderIdentifier);
                break;
        }

        return START_STICKY;
    }

    /**
     * Returns an immutable map containing the active recorders keyed by their identifiers.
     * @return an immutable map containing the active recorders keyed by their identifiers.
     */
    public ImmutableMap<String, Recorder> getActiveRecorders(@NonNull UUID taskIdentifier) {
        return ImmutableMap.copyOf(this.recorderMapping.get(taskIdentifier));
    }

    public Recorder createRecorder(@NonNull UUID taskIdentifier, @NonNull String recorderIdentifier,
            @RecorderType String type) {
        // TODO rkolmos 06/20/2018 call a recorder factory here.
        Recorder recorder = new Recorder() {
            @Override
            public String getIdentifier() {
                return null;
            }

            @Override
            public void start() {

            }



            @Override
            public void stop() {

            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isRecording() {
                return false;
            }

            @Nullable
            @Override
            public String getStartStepIdentifier() {
                return null;
            }

            @Nullable
            @Override
            public String getStopStepIdentifier() {
                return null;
            }
        };

        if (!this.recorderMapping.containsKey(taskIdentifier)) {
            this.recorderMapping.put(taskIdentifier, new HashMap<>());
        }

        Map<String, Recorder> taskRecorderMapping = this.recorderMapping.get(taskIdentifier);
        taskRecorderMapping.put(recorderIdentifier, recorder);
        return recorder;
    }

    public class RecorderInstantiationException extends Exception {
        @NonNull
        private final String recorderId;
        @NonNull
        @RecorderType
        private final String recorderType;
        @Nullable
        private final String message;

        public RecorderInstantiationException(@NonNull String recorderId, @NonNull @RecorderType String recorderType,
                @Nullable String message) {
            this.recorderId = recorderId;
            this.recorderType = recorderType;
            this.message = message;
        }

        public RecorderInstantiationException(@NonNull String recorderId, @NonNull @RecorderType String recorderType) {
            this(recorderId, recorderType, null);
        }

        public String toString() {
            if (message != null) {
                return message;
            } else {
                return "Failed to instantiate recorder " + this.recorderId + " of type " + this.recorderType;
            }
        }

        @NonNull
        public String getRecorderId() {
            return recorderId;
        }

        @NonNull
        @RecorderType
        public String getRecorderType() {
            return recorderType;
        }

        @Override
        @Nullable
        public String getMessage() {
            return message;
        }
    }

    /**
     * Allows the RecorderService to be bound to.
     */
    public final class RecorderBinder extends Binder {
        public RecorderService getService() {
            return RecorderService.this;
        }
    }
}
