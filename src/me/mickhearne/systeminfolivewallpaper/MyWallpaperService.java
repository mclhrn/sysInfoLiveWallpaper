package me.mickhearne.systeminfolivewallpaper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class MyWallpaperService extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
		return new MyWallpaperEngine();
	}


	private class MyWallpaperEngine extends Engine {
		
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};
		private List<MyPoint> circles;
		private Paint paint = new Paint();
		private int width;
		int height;
		private boolean visible = true;
		private int maxNumber;
		private boolean touchEnabled;

		public MyWallpaperEngine() {
			
			// Grab Preferences here 
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyWallpaperService.this);
			maxNumber = Integer.valueOf(prefs.getString("numberOfCircles", "4"));
			touchEnabled = prefs.getBoolean("touch", false);
			circles = new ArrayList<MyPoint>();
			paint.setAntiAlias(true);
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(10f);
			handler.post(drawRunner);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			this.width = width;
			this.height = height;
			super.onSurfaceChanged(holder, format, width, height);
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (touchEnabled) {

				float x = event.getX();
				float y = event.getY();
				SurfaceHolder holder = getSurfaceHolder();
				Canvas canvas = null;
				try {
					canvas = holder.lockCanvas();
					if (canvas != null) {
						canvas.drawColor(Color.BLACK);
						circles.clear();
						circles.add(new MyPoint(
								String.valueOf(circles.size() + 1), x, y));
						drawText(canvas, circles);

					}
				} finally {
					if (canvas != null)
						holder.unlockCanvasAndPost(canvas);
				}
				super.onTouchEvent(event);
			}
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					if (circles.size() >= maxNumber) {
						circles.clear();
					}
					drawText(canvas, circles);
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}
			handler.removeCallbacks(drawRunner);
			if (visible) {
				handler.postDelayed(drawRunner, 1000);
			}
		}

		
		
		
		
		// Change below method to take a list of preference strings
		
		
		
		// Surface view requires that all elements are drawn completely
		private void drawText(Canvas canvas, List<MyPoint> circles) {
			canvas.drawColor(Color.RED);
				
			// Text

			// Grab container width here and pass to function to return text size
			
			
			Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTextSize(92* getResources().getDisplayMetrics().density);
            paint.setAntiAlias(true);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
			
            String text = "Wallpaper";
	        canvas.drawText(text, 4, 320, paint);
	        
	        text = "Customized";
            paint.setTextSize(54* getResources().getDisplayMetrics().density);
	        paint.setTypeface(Typeface.DEFAULT);
	        canvas.drawText(text, 4, 670, paint);
		}
	}
}