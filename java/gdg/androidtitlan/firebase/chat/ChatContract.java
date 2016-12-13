package gdg.androidtitlan.firebase.chat;

import com.firebase.client.DataSnapshot;

/**
 * 15/05/16.
 */
public class ChatContract {

  public interface View {

    void sendMessage();

    void fireBaseOnChildAdded(DataSnapshot dataSnapshot, String s);
  }

  interface UserActionListener {

    void send();

    void childAdded(DataSnapshot dataSnapshot, String s);
  }
}
