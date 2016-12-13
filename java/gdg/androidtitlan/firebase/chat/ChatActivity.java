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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import gdg.androidtitlan.firebase.R;

public class ChatActivity extends AppCompatActivity {

  private final static String USER_MAIL_EXTRA = "user_mail_extra";

  @BindView(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    setupToolbar();
  }

  private void setupToolbar() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayShowTitleEnabled(true);
      actionBar.setTitle(getString(R.string.app_name) + " - " + getMail());
    }
  }

  private String getMail() {
    return getIntent().getStringExtra(USER_MAIL_EXTRA);
  }

  public static Intent provideIntent(Context context, String userName) {
    Intent intent = new Intent(context, ChatActivity.class);
    intent.putExtra(USER_MAIL_EXTRA, userName);
    return intent;
  }
}
