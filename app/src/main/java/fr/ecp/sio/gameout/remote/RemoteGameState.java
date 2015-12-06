package fr.ecp.sio.gameout.remote;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import fr.ecp.sio.gameout.HVPoint;
import fr.ecp.sio.gameout.model.GameSession;
import fr.ecp.sio.gameout.model.GameState;
import fr.ecp.sio.gameout.model.Player;
import fr.ecp.sio.gameout.model.Team;

/**
 * Created by od on 11/20/2015.
 */
public class RemoteGameState extends GameState
{
    private static RemoteGameState instance;

    private RemoteGameState(GameSession session) throws IOException {
        super(session);
        StartSessionTask startSessionTask = new StartSessionTask();
        startSessionTask.execute(session);
    }

    public static synchronized RemoteGameState getInstance(GameSession session) throws IOException {
        if(instance == null) {
            instance = new RemoteGameState(session);
        }

        return instance;
    }

    public static synchronized RemoteGameState getInstance() {
        return instance;
    }

    public void sendPosition(HVPoint position) throws ExecutionException, InterruptedException {
        SendPositionTask serverTask = new SendPositionTask();
        serverTask.execute(position);
    }

    public void getNewState() // Fonction bloquante en attente des nouvelles position.
    {

    }

    private static class StartSessionTask extends AsyncTask<GameSession, Void, String> {
        @Override
        protected String doInBackground(GameSession... params) {
            try {
                return GameoutClient.getInstance().startGameSession(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class SendPositionTask extends AsyncTask<HVPoint, Void, String> {

        @Override
        protected String doInBackground(HVPoint... params) {
            String responseFromServer = "";

            GameoutClient client = null;
            try {
                client = GameoutClient.getInstance();

                GameState gameState = new GameState(client.getGameSession());
                Team team = new Team((byte)0, gameState, 2);

                HVPoint position = params[0];
                Player player = new Player((byte)0, team);
                player.x = (short)position.H;
                player.y = (short)position.V;
                player.vx = (short)1;
                player.vx = (short)1;

                byte[] responseBytes = client.sendPosition(player);
                GameoutClientHelper.updateGameState(responseBytes);
                responseFromServer = "";
            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseFromServer;
        }
    }
}
