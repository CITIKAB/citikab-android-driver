/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.trioangle.goferdriver.service;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class WorkerUtils {
    private static final String TAG = WorkerUtils.class.getSimpleName();
    static WorkManager mWorkManager = WorkManager.getInstance();

    public WorkerUtils() {
    }

    public static void startWorkManager(String name, String tag, Class classfile) {
        WorkContinuation continuation;
        OneTimeWorkRequest uploadWorkRequest;

        uploadWorkRequest =
                new OneTimeWorkRequest.Builder(classfile)
                        .addTag(tag)
                        .build();

        // Add WorkRequest to Cleanup temporary images
        continuation = mWorkManager
                .beginUniqueWork(name,
                        ExistingWorkPolicy.REPLACE,
                        uploadWorkRequest);

        // Actually start the work
        continuation.enqueue();

        /*PeriodicWorkRequest periodicWorkRequest;

        periodicWorkRequest =
                new PeriodicWorkRequest.Builder(classfile,30,TimeUnit.SECONDS)
                        .addTag(tag)
                        //.setInitialDelay(duration, TimeUnit.SECONDS)
                        .build();

        mWorkManager.enqueueUniquePeriodicWork(tag,ExistingPeriodicWorkPolicy.REPLACE,periodicWorkRequest);*/

    }

    /**
     * Start or stop work manager
     *
     * @param name
     * @param tag
     * @param duration
     */
    public static void startWorkManager(String name, String tag, long duration, Class classfile, boolean isBackground) {
        WorkContinuation continuation;
        OneTimeWorkRequest uploadWorkRequest;
        PeriodicWorkRequest periodicWorkRequest;
        if (isBackground && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Constraints constraints = new Constraints.Builder()
                    //.setRequiredNetworkType(NetworkType.CONNECTED)
                    //.setRequiresDeviceIdle(isBackground)
                    .build();

            uploadWorkRequest =
                    new OneTimeWorkRequest.Builder(classfile)
                            .addTag(tag)
                            .setConstraints(constraints)
                            .setInitialDelay(duration, TimeUnit.SECONDS)
                            .build();

            /*periodicWorkRequest =
                    new PeriodicWorkRequest.Builder(classfile,30,TimeUnit.SECONDS)
                            .addTag(tag)
                            .setConstraints(constraints)
                            //.setInitialDelay(duration, TimeUnit.SECONDS)
                            .build();*/
        } else {
            uploadWorkRequest =
                    new OneTimeWorkRequest.Builder(classfile)
                            .addTag(tag)
                            .setInitialDelay(duration, TimeUnit.SECONDS)
                            .build();

            /*periodicWorkRequest =
                    new PeriodicWorkRequest.Builder(classfile,30,TimeUnit.SECONDS)
                            .addTag(tag)
                            //.setInitialDelay(duration, TimeUnit.SECONDS)
                            .build();*/
        }


        continuation = mWorkManager
                .beginUniqueWork(name,
                        ExistingWorkPolicy.REPLACE,
                        uploadWorkRequest);

        // Actually start the work
        continuation.enqueue();
        //mWorkManager.enqueueUniquePeriodicWork(tag,ExistingPeriodicWorkPolicy.REPLACE,periodicWorkRequest);
        isWorkRunning(tag);
    }


    /**
     * Cancel work using the work's unique id
     */
    public static void cancelWorkByUUID() {
        String workerUuid = "";//session.getStringDataByKey(SessionManager.KEY_WORKER_UUID);
        //Log.e("Canceling Worker UUID", workerUuid);
        if (workerUuid != null && workerUuid.length() > 0) {
            mWorkManager.cancelWorkById(UUID.fromString(workerUuid));
            mWorkManager.pruneWork();
        }
    }

    /**
     * Cancel work using the work's unique name
     */
    public static void cancelWorkByTag(String workerTag) {
        mWorkManager.cancelAllWorkByTag(workerTag);
    }


    /**
     * Check is work is running or not
     */
    public static boolean isWorkRunning(String workerTag) {
        ListenableFuture<List<WorkInfo>> statuses = mWorkManager.getWorkInfosByTag(workerTag);
        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }
            return running;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}