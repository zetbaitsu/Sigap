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

package id.satusatudua.sigap.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.api.CloudImage;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.CandidateHelper;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.Message;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.ChatPresenter;
import id.satusatudua.sigap.ui.FeedbackCaseActivity;
import id.satusatudua.sigap.ui.HelpingActivity;
import id.satusatudua.sigap.ui.PictureActivity;
import id.satusatudua.sigap.ui.ProfileActivity;
import id.satusatudua.sigap.ui.TombolActivity;
import id.satusatudua.sigap.ui.adapter.ChatAdapter;
import id.satusatudua.sigap.ui.adapter.HelperAdapter;
import id.satusatudua.sigap.util.MapUtils;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihRecyclerView;
import id.zelory.benih.util.BenihScheduler;
import id.zelory.benih.util.BenihWorker;
import timber.log.Timber;

/**
 * Created on : January 13, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ChatFragment extends BenihFragment implements ChatPresenter.View {
    private static final String KEY_CASE = "extra_case";
    private static final String KEY_REPORTER = "extra_reporter";
    private static final String KEY_DISABLE_CHAT = "extra_disable_chat";
    private static final int REQUEST_PICTURE = 25;
    private static final int REQUEST_TAKE_PICTURE = 24;
    private static final int REQUEST_ATTACH_FILE = 93;
    private static final int REQUEST_PICK_LOCATION = 95;

    @Bind(R.id.list_message) BenihRecyclerView listMessage;
    @Bind(R.id.list_helper) BenihRecyclerView listHelper;
    @Bind(R.id.field_message) EditText messageField;
    @Bind(R.id.button_send) ImageView buttonSend;
    @Bind(R.id.root_input) LinearLayout rootInput;

    private ChatAdapter chatAdapter;
    private HelperAdapter helperAdapter;
    private Case theCase;
    private User reporter;
    private ChatPresenter chatPresenter;
    private ProgressDialog progressDialog;
    private Vibrator vibrator;

    public static ChatFragment newInstance(Case theCase, User reporter) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_CASE, theCase);
        bundle.putParcelable(KEY_REPORTER, reporter);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    public static ChatFragment newInstance(Case theCase, User reporter, boolean disableChat) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_CASE, theCase);
        bundle.putParcelable(KEY_REPORTER, reporter);
        bundle.putBoolean(KEY_DISABLE_CHAT, disableChat);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {
        resolveTheCase(savedInstanceState);
        resolveReporter(savedInstanceState);

        vibrator = ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE));

        boolean disableChat = getArguments().getBoolean(KEY_DISABLE_CHAT, false);
        rootInput.setVisibility(disableChat ? View.GONE : View.VISIBLE);
        listHelper.setVisibility(disableChat ? View.GONE : View.VISIBLE);

        chatAdapter = new ChatAdapter(getActivity());
        listMessage.setUpAsBottomList();
        listMessage.setAdapter(chatAdapter);
        chatAdapter.setOnItemClickListener((view, position) -> onItemChatClicked(chatAdapter.getData().get(position)));

        helperAdapter = new HelperAdapter(getActivity());
        listHelper.setUpAsHorizontalList();
        helperAdapter.add(transformReporter());
        listHelper.setAdapter(helperAdapter);
        helperAdapter.setOnItemClickListener((view, position) -> onItemHelperClicked(helperAdapter.getData().get(position)));

        chatPresenter = new ChatPresenter(this, theCase, reporter);
        if (savedInstanceState == null) {
            chatPresenter.loadHelper();
        } else {
            chatPresenter.loadState(savedInstanceState);
        }
    }

    private void onItemChatClicked(Message message) {
        if (theCase.getStatus() != Case.Status.DITUTUP) {
            if (message.getContent().startsWith("[INITIAL]") && message.getContent().endsWith("[/INITIAL]")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                                           Uri.parse("http://maps.google.com/maps?daddr="
                                                             + theCase.getLatitude() + ","
                                                             + theCase.getLongitude()));
                startActivity(intent);
            } else if (message.getContent().startsWith("[CLOSED]") && message.getContent().endsWith("[/CLOSED]")) {
                startActivity(FeedbackCaseActivity.generateIntent(getActivity(), theCase));
            }
        }

        if (message.isPicture()) {
            openPicture(message);
        } else if (message.isAttachment()) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(message.getUrl())));
        } else if (message.isLocation()) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                                     Uri.parse("http://maps.google.com/maps?daddr="
                                                       + message.getLatLng().latitude + ","
                                                       + message.getLatLng().longitude)));
        }
    }

    private void openPicture(Message message) {
        showLoading();
        List<Message> messages = new ArrayList<>();
        final int[] position = {0};
        int size = chatAdapter.getData().size();

        BenihWorker.pluck().doInNewThread(() -> {
            for (int i = 0; i < size; i++) {
                if (chatAdapter.getData().get(i).isPicture()) {
                    messages.add(chatAdapter.getData().get(i));
                    if (message.equals(chatAdapter.getData().get(i))) {
                        position[0] = messages.size() - 1;
                    }
                }
            }
        }).compose(bindToLifecycle()).subscribe(o -> {
            startActivity(PictureActivity.generateIntent(getActivity(), messages, position[0]));
            dismissLoading();
        }, throwable -> {
            Timber.e(throwable.getMessage());
            showError("Failed to open picture!");
            dismissLoading();
        });
    }

    private CandidateHelper transformReporter() {
        CandidateHelper reporter = new CandidateHelper();
        reporter.setCandidate(this.reporter);
        reporter.setCandidateId(this.reporter.getUserId());
        reporter.setStatus(CandidateHelper.Status.MENOLONG);
        return reporter;
    }

    private void resolveTheCase(Bundle savedInstanceState) {
        theCase = getArguments().getParcelable(KEY_CASE);

        if (theCase == null && savedInstanceState != null) {
            theCase = savedInstanceState.getParcelable(KEY_CASE);
        }

        if (theCase == null) {
            Intent intent = new Intent(getActivity(), TombolActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void resolveReporter(Bundle savedInstanceState) {
        reporter = getArguments().getParcelable(KEY_REPORTER);

        if (reporter == null && savedInstanceState != null) {
            reporter = savedInstanceState.getParcelable(KEY_REPORTER);
        }

        if (reporter == null) {
            Intent intent = new Intent(getActivity(), TombolActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @OnTextChanged(R.id.field_message)
    public void fieldMessageChanged(CharSequence message) {
        if (message == null || "".equals(message.toString())) {
            buttonSend.setImageResource(R.drawable.ic_like);
        } else {
            buttonSend.setImageResource(R.drawable.ic_send);
        }
    }

    private void onItemHelperClicked(CandidateHelper candidateHelper) {
        startActivity(ProfileActivity.generateIntent(getActivity(), candidateHelper.getCandidate()));
    }

    @OnClick(R.id.button_send)
    public void sendMessage() {
        if (theCase.getStatus() != Case.Status.DITUTUP) {
            String content = messageField.getText().toString();
            if (!content.isEmpty()) {
                messageField.setText("");
                chatPresenter.sendMessage(content);
            } else {
                chatPresenter.sendMessage("\uD83D\uDC4D");
            }
        } else {
            HelpingActivity activity = (HelpingActivity) getActivity();
            activity.onCaseClosed(theCase);
        }
    }

    public void sendDangerMessage() {
        chatPresenter.sendMessage("[DANGER]BAHAYA!!!! BAHAYA!!! BAHAYA!!! SEGERA HUBUNGI PIHAK BERWAJIB!!!!![/DANGER]");
    }

    @OnClick(R.id.button_add_image)
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICTURE);
    }

    @OnClick(R.id.button_take_picture)
    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                showError("Failed to write temporary picture!");
            }

            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intent, REQUEST_TAKE_PICTURE);
            }
        }
    }

    @OnClick(R.id.button_add_file)
    public void attachFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_ATTACH_FILE);
    }

    @OnClick(R.id.button_add_location)
    public void pickLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(getActivity()), REQUEST_PICK_LOCATION);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            showError("Failed to access location!");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                showLoading();
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                CloudImage.pluck().upload(inputStream, null, false)
                        .compose(bindToLifecycle())
                        .subscribe(url -> {
                            chatPresenter.sendMessage(Message.generatePictureMessage(url));
                            dismissLoading();
                        }, throwable -> {
                            throwable.printStackTrace();
                            Timber.e(throwable.getMessage());
                            showError("Failed to upload picture!");
                            dismissLoading();
                        });
            } catch (FileNotFoundException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            try {
                showLoading();
                InputStream inputStream = getActivity().getContentResolver()
                        .openInputStream(Uri.parse(CacheManager.pluck().getLastPicturePath()));
                CloudImage.pluck().upload(inputStream, null, false)
                        .compose(bindToLifecycle())
                        .subscribe(url -> {
                            chatPresenter.sendMessage(Message.generatePictureMessage(url));
                            dismissLoading();
                        }, throwable -> {
                            throwable.printStackTrace();
                            Timber.e(throwable.getMessage());
                            showError("Failed to upload picture!");
                            dismissLoading();
                        });
            } catch (Exception e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
                dismissLoading();
            }
        } else if (requestCode == REQUEST_ATTACH_FILE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                showError("Failed to open file!");
                return;
            }
            try {
                showLoading();
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                String fileName = getFileName(data.getData());
                CloudImage.pluck().upload(inputStream, fileName, false)
                        .compose(bindToLifecycle())
                        .subscribe(url -> {
                            chatPresenter.sendMessage(Message.generateAttachmentMessage(fileName, url));
                            dismissLoading();
                        }, throwable -> {
                            throwable.printStackTrace();
                            Timber.e(throwable.getMessage());
                            showError("Failed to upload file!");
                            dismissLoading();
                        });
            } catch (FileNotFoundException e) {
                showError("Failed to read file data!");
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_PICK_LOCATION && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                showError("Failed to pick location!");
                return;
            }
            Place place = PlacePicker.getPlace(data, getActivity());
            String address = place.getAddress().toString();
            if (address.trim().isEmpty()) {
                showLoading();
                MapUtils.getAddress(place.getLatLng().latitude, place.getLatLng().longitude)
                        .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.NEW_THREAD))
                        .compose(bindToLifecycle())
                        .subscribe(s -> {
                            chatPresenter
                                    .sendMessage(Message.generateLocationMessage(place.getName() + " - " + s, place.getLatLng()));
                            dismissLoading();
                        }, throwable -> {
                            throwable.printStackTrace();
                            showError("Failed to get address!");
                            dismissLoading();
                        });
            } else {
                chatPresenter
                        .sendMessage(Message.generateLocationMessage(place.getName() + " - " + address, place.getLatLng()));
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        CacheManager.pluck().cacheLastPicturePath("file:" + image.getAbsolutePath());
        return image;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        chatPresenter.saveState(outState);
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CASE, theCase);
        outState.putParcelable(KEY_REPORTER, reporter);
    }

    @Override
    public void showHelpers(List<CandidateHelper> helpers) {
        helperAdapter.addOrUpdate(helpers);
    }

    @Override
    public void onHelperStatusChanged(CandidateHelper candidateHelper) {
        helperAdapter.addOrUpdate(candidateHelper);
    }

    @Override
    public void onSendingMessage(Message message) {
        chatAdapter.add(message);
        listMessage.post(() -> listMessage.smoothScrollToPosition(chatAdapter.getItemCount() - 1));
    }

    @Override
    public void onSuccessSendMessage(Message message) {
        chatAdapter.addOrUpdate(message);
    }

    @Override
    public void onFailedSendMessage(Message message) {
        chatAdapter.remove(message);
    }

    @Override
    public void onNewMessage(Message message) {
        chatAdapter.addOrUpdate(message);
        listMessage.post(() -> listMessage.smoothScrollToPosition(chatAdapter.getItemCount() - 1));
        if (!message.isFromMe() && message.getContent().startsWith("[DANGER]") && message.getContent().endsWith("[/DANGER]")) {
            vibrator.vibrate(1000);
        }
    }

    @Override
    public void showError(String errorMessage) {
        try {
            Snackbar snackbar = Snackbar.make(listMessage, errorMessage, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundResource(R.color.colorAccent);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Silahkan tunggu...");
        }
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }
}
