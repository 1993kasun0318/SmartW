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

package gdg.androidtitlan.firebase.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import gdg.androidtitlan.firebase.FireBase;
import gdg.androidtitlan.firebase.R;
import gdg.androidtitlan.firebase.chat.ChatActivity;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

  @BindView(R.id.label_mail) EditText mailLabel;
  @BindView(R.id.label_password) EditText passwordLabel;

  private ProgressDialog progress;
  private Firebase firebase;

  private LoginContract.UserActionListener presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    initializeFirebase();
    initializePresenter();
  }

  @OnClick(R.id.button_login) void login() {
    UserCredential userCredential = getUserCredential();
    presenter.login(userCredential);
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.authStateListener();
  }

  @Override public void showProgress(boolean state) {
    setProgressState(state);
  }

  @Override public void fireBaseCreateUser(final UserCredential credential) {
    firebase.createUser(credential.getMail(), credential.getPassword(),
        new Firebase.ResultHandler() {

          @Override public void onSuccess() {
            presenter.auth(credential);
          }

          @Override public void onError(FirebaseError firebaseError) {
            presenter.authError(credential, firebaseError);
          }
        });
  }

  @Override public void fireBaseAuthWithPassword(UserCredential credential) {
    firebase.authWithPassword(credential.getMail(), credential.getPassword(), null);
  }

  @Override public void fireBaseAuthStateListener() {
    firebase.addAuthStateListener(new Firebase.AuthStateListener() {
      @Override public void onAuthStateChanged(AuthData authData) {
        if (authData != null) presenter.authStateChanged(authData);
      }
    });
  }

  @Override public void launchChatActivity(String mail) {
    Intent intent = ChatActivity.provideIntent(this, mail);
    startActivity(intent);
    finish();
  }

  private void setProgressState(Boolean state) {

    if (state) {
      setProgress(true);
    } else {
      progress.dismiss();
    }
  }

  private UserCredential getUserCredential() {
    final String mail = mailLabel.getText().toString();
    final String password = passwordLabel.getText().toString();
    return new UserCredential(mail, password);
  }

  private void initializeFirebase() {
    firebase = FireBase.getInstance(this);
  }

  private void initializePresenter() {
    if (presenter == null) {
      presenter = new LoginPresenter(this);
    }
  }

  private void setProgress(Boolean state) {
    progress = ProgressDialog.show(this, null, getString(R.string.login_progress_dialog), state);
  }
}
