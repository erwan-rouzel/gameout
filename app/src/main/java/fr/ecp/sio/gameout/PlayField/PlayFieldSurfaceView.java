package fr.ecp.sio.gameout.PlayField;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import fr.ecp.sio.gameout.LocationManager;
import fr.ecp.sio.gameout.TimeKeeper;
import fr.ecp.sio.gameout.model.HVPoint;

/**
 * Created by od on 10/31/2015.
 * Zone graphique où est affiché le jeu en cour.
 * C'est dans cette surface que sont dessinés les raquettes, les balles, le score
 */
public class PlayFieldSurfaceView extends SurfaceView
{
    // TODO vérifier la prise en charge des changements paysage <--> portrait
    private int mCanvasWidth = 11;
    private int mCanvasHeight = 22;

    private PlayFieldThread mPft;

    public PlayFieldSurfaceView(Context context)
    {
        super(context);
        aLaCreation();
    }

    public PlayFieldSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        aLaCreation();
    }

    private void aLaCreation()
    {
        SurfaceHolder  mSurfaceHolder = getHolder();


        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas(null);
                canvas.drawColor(Color.RED);
                holder.unlockCanvasAndPost(canvas);
                init_mPft();
            }

            private void adaptToCanvasSize(int pWidth, int pHeight) {// TODO Make sure to avoid changing size while using the canvas
                mCanvasWidth = pWidth;
                mCanvasHeight = pHeight;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder,
                                       int format, int width, int height) {// TODO gérer les changements d'orientation
                adaptToCanvasSize(width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {// TODO Auto-generated method stub

            }
        });
    }

    public void startGameThread() {
        mPft.start();
    }

    private void init_mPft()
    {
        CurPfp.pfp.nb_team = 1;
        CurPfp.pfp.nb_player [0] = 1;
        CurPfp.pfp.xPosPadLocalExt = 23;
        CurPfp.pfp.yPosPadLocalExt = 32;
        //TODO supprimer les 4 lignes d'init si dessous, c'est au serveur de le faire.
        /*
        CurPfp.pfp.xPosPad   [0][0] = 2;
        CurPfp.pfp.yPosPad   [0][0] = 2;
        CurPfp.pfp.xRadPad   [0][0] = HVPoint.WIDTH_REF/16;
        CurPfp.pfp.yRadPad   [0][0] = HVPoint.WIDTH_REF/80;
        */
        mPft = new PlayFieldThread(this);
    }

    private void drawRect(Canvas pCanvas,
                          int xP, int yP, // Centre rectangle
                          int xR, int yR, // Rayon rectangle
                          Paint paint, // Couleur rectangle
                          boolean dirOfView) // Pour inverser haut/bas selon l'équipe en face à face
    {
        int h1,h2,v1,v2,h,v;
        int rh,rv;
        int wRef = HVPoint.WIDTH_REF;
        int hRef = HVPoint.WIDTH_REF;

        mCanvasWidth  = pCanvas.getWidth();
        mCanvasHeight = pCanvas.getHeight();

        h = (xP*mCanvasWidth)/wRef;
        v = (yP*mCanvasHeight)/wRef;

        rh = (xR*mCanvasWidth)/wRef;
        h1 = h - rh;
        h2 = h + rh;

        rv = (yR*mCanvasHeight)/wRef;
        v1 = v - rv;
        v2 = v + rv;

        if (!dirOfView )
        {
            v1 = mCanvasHeight-v1;
            v2 = mCanvasHeight-v2;
        }

        pCanvas.drawRect(h1, v1, h2, v2, paint);
    }

    private void drawBall(Canvas pCanvas, boolean dirOfView)
    {
        Paint paint = new Paint();
        paint.setARGB(255,240,240,16);
        drawRect(pCanvas, CurPfp.pfp.xPosBalSrv, CurPfp.pfp.yPosBalSrv, CurPfp.pfp.xRadBalSrv, CurPfp.pfp.yRadBalSrv, paint, dirOfView);
    }

    private void drawPads(Canvas pCanvas, boolean dirOfView)
    {
        int e,j;
        Paint paint;
        Paint paint0 = new Paint();
        paint0.setARGB(255,128,128,128);
        Paint paint1 = new Paint();
        paint1.setARGB(255,200,200,200);
        Paint paintE = new Paint();
        paintE.setARGB(255,255,64,64);

        for (e=0; e<CurPfp.pfp.nb_team; e++)
            for (j=0; j<CurPfp.pfp.nb_player[e]; j++)
            {
                switch (CurPfp.pfp.statePadSrv[e][j])
                {
                    case 0:
                        paint = paint0;
                        break;
                    case 1 :
                        paint = paint1;
                        break;
                    default :
                        paint = paintE;
                }
                drawRect (pCanvas, CurPfp.pfp.xPosPadSrv[e][j], CurPfp.pfp.yPosPadSrv[e][j], CurPfp.pfp.xRadPadSrv[e][j], CurPfp.pfp.yRadPadSrv[e][j], paint, dirOfView);
            }
    }

    public void maj_visu(boolean dirOfView) // dirOfView pour retourner la visu
    {
        TimeKeeper.addEvent(1);
        TimeKeeper.duratStartEvent(1);
        Canvas lCanvas;
        CurPfp.pfp.extrapolateLocal();
        LocationManager locationManager = LocationManager.getInstance();
        locationManager.setPosition(
                new HVPoint((short) CurPfp.pfp.xPosPadLocalExt, (short) CurPfp.pfp.yPosPadLocalExt)
        );

        TimeKeeper.duratEndEvent(1);
        TimeKeeper.duratStartEvent(2);
        if(CurPfp.pfp.isGameStarted) {
            TimeKeeper.addEvent(2);
            CurPfp.pfp.syncGameState();
        }
        TimeKeeper.duratEndEvent(2);
        TimeKeeper.duratStartEvent(3);

        lCanvas = getHolder().lockCanvas();

        if (lCanvas != null) {
            TimeKeeper.addEvent(3);
            lCanvas.drawColor(0xFF101080); // Efface toute la zone
            drawBall(lCanvas, dirOfView);  // Dessine la balle
            drawPads(lCanvas, dirOfView);  // Dessine la raquette
        }

        getHolder().unlockCanvasAndPost(lCanvas);
        TimeKeeper.duratEndEvent(3);
    }
}
