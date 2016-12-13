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

import android.support.annotation.NonNull;
import com.firebase.client.AuthData;
import com.firebase.client.FirebaseError;

public class LoginPresenter implements LoginContract.UserActionListener {

  private static final String FIRE_BASE_MAIL = "email";
  private LoginContract.View view;

  public LoginPresenter(@NonNull LoginContract.View view) {
    this.view = view;
  }

  @Override public void login(UserCredential credential) {
    view.showProgress(true);
    view.fireBaseCreateUser(credential);
  }

  @Override public void auth(UserCredential credential) {
    view.fireBaseAuthWithPassword(credential);
    view.showProgress(false);
  }

  @Override public void authError(UserCredential credential, FirebaseError firebaseError) {
    view.fireBaseAuthWithPassword(credential);
    view.showProgress(false);
  }

  @Override public void authStateListener() {
    view.fireBaseAuthStateListener();
  }

  @Override public void authStateChanged(AuthData authData) {
    String userName = ((String) authData.getProviderData().get(FIRE_BASE_MAIL));
    view.launchChatActivity(userName);
  }
}
