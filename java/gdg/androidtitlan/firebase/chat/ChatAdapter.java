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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gdg.androidtitlan.firebase.R;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

  private static final int MESSAGE_SENT = 1;
  private static final int MESSAGE_RECEIVED = 2;
  private List<Chat> chatList;
  private String mId;

  public ChatAdapter(List<Chat> chatList, String mId) {
    this.chatList = chatList;
    this.mId = mId;
  }

  @Override public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    if (viewType == MESSAGE_SENT) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.chat_message_sent, parent, false);
    } else {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.chat_message_received, parent, false);
    }

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    Chat chat = chatList.get(position);
    holder.txtMessage.setText(chat.getMessage());
  }

  @Override public int getItemCount() {
    return chatList.size();
  }

  @Override public int getItemViewType(int position) {
    if (chatList.get(position).getAuthor().equals(mId)) return MESSAGE_SENT;

    return MESSAGE_RECEIVED;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    TextView txtMessage;

    public ViewHolder(View view) {
      super(view);
      txtMessage = (TextView) view.findViewById(R.id.txt_message);
    }
  }
}
