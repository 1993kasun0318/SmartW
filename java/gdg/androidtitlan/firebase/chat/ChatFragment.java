/**
 * Copyright 2016 Erik Jhordan Rey.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gdg.androidtitlan.firebase.chat;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import gdg.androidtitlan.firebase.FireBase;
import gdg.androidtitlan.firebase.R;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment implements ChildEventListener, ChatContract.View {

  @BindView(R.id.input_message) EditText messageInput;
  @BindView(R.id.list_chat) RecyclerView chatList;

  private Firebase firebase;
  private List<Chat> chats;
  private String idDevice;
  private ChatAdapter adapter;

  private ChatContract.UserActionListener presenter;

  public ChatFragment() {
    setHasOptionsMenu(true);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initializeFirebase();
    idDevice = getIdDevice();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main, container, false);
    ButterKnife.bind(this, view);
    initializePresenter();
    setupAdapter();
    setupList();

    return view;
  }

  @OnClick(R.id.button_sent) public void submit() {
    presenter.send();
  }

  private void initializeFirebase() {
    firebase = FireBase.getInstance(getActivity());
    firebase.addChildEventListener(this);
  }

  private void setupAdapter() {
    chats = new ArrayList<>();
    adapter = new ChatAdapter(chats, idDevice);
  }

  private void setupList() {
    chatList.setLayoutManager(new LinearLayoutManager(chatList.getContext()));
    chatList.setAdapter(adapter);
  }

  private String getIdDevice() {
    return Settings.Secure.getString(getActivity().getContentResolver(),
        Settings.Secure.ANDROID_ID);
  }

  private void initializePresenter() {
    if (presenter == null) presenter = new ChatPresenter(this);
  }

  @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
    presenter.childAdded(dataSnapshot, s);
  }

  @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {

  }

  @Override public void onChildRemoved(DataSnapshot dataSnapshot) {

  }

  @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {

  }

  @Override public void onCancelled(FirebaseError firebaseError) {

  }

  @Override public void sendMessage() {
    String message = messageInput.getText().toString();
    if (!message.isEmpty()) firebase.push().setValue(new Chat(message, idDevice));

    messageInput.setText("");
  }

  @Override public void fireBaseOnChildAdded(DataSnapshot dataSnapshot, String s) {
    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
      Chat model = dataSnapshot.getValue(Chat.class);
      chats.add(model);
      chatList.scrollToPosition(chats.size() - 1);
      adapter.notifyItemInserted(chats.size() - 1);
    }
  }
}
