package jp.jagfukuoka.provider;

import java.util.List;

import jp.jagfukuoka.TemperatureData;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.googlecode.chartdroid.core.ColumnSchema;

public class DataContentProvider extends ContentProvider {


//NISHIGUCHI	static final String TAG = Market.TAG;

	// This must be the same as what as specified as the Content Provider authority
	// in the manifest file.
	static final String AUTHORITY = "jp.jagfukuoka.provider";

	public static final Uri PROVIDER_URI = new Uri.Builder()
		.scheme(ContentResolver.SCHEME_CONTENT)
		.authority(AUTHORITY).build();


	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public String getType(Uri uri) {
		return ColumnSchema.PlotData.CONTENT_TYPE_PLOT_DATA;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		if (ColumnSchema.Aspect.DATASET_ASPECT_AXES.equals( uri.getQueryParameter(ColumnSchema.DATASET_ASPECT_PARAMETER) )) {

			MatrixCursor c = new MatrixCursor(new String[] {
					BaseColumns._ID,
					ColumnSchema.Aspect.Axes.COLUMN_AXIS_LABEL});

			int row_index = 0;
			for (int i=0; i<TemperatureData.DEMO_AXES_LABELS.length; i++) {

				c.newRow().add( row_index ).add( TemperatureData.DEMO_AXES_LABELS[i] );
				row_index++;
			}

			return c;
		} else if (ColumnSchema.Aspect.DATASET_ASPECT_SERIES.equals( uri.getQueryParameter(ColumnSchema.DATASET_ASPECT_PARAMETER) )) {

			// TODO: Define more columns for color, line style, marker shape, etc.
			MatrixCursor c = new MatrixCursor(new String[] {
					BaseColumns._ID,
					ColumnSchema.Aspect.Series.COLUMN_SERIES_LABEL});

			int row_index = 0;
			for (int i=0; i<TemperatureData.DEMO_TITLES.length; i++) {

				c.newRow().add( row_index ).add( TemperatureData.DEMO_TITLES[i] );
				row_index++;
			}

			return c;

		} else {
			// Fetch the actual data

			MatrixCursor c = new MatrixCursor(new String[] {
					BaseColumns._ID,
					ColumnSchema.Aspect.Data.COLUMN_AXIS_INDEX,
					ColumnSchema.Aspect.Data.COLUMN_SERIES_INDEX,
					ColumnSchema.Aspect.Data.COLUMN_DATUM_VALUE,
					ColumnSchema.Aspect.Data.COLUMN_DATUM_LABEL
			});

			int row_index = 0;

			// Add x-axis data
			for (int i=0; i<TemperatureData.DEMO_X_AXIS_DATA.size(); i++) {


				//                c.newRow().add( X_AXIS_INDEX ).add( i ).add( TemperatureData.DEMO_X_AXIS_DATA[i] ).add( null );
				c.newRow()
				.add( row_index )
				.add( ColumnSchema.X_AXIS_INDEX )
				.add( 0 )   // Only create data for the first series.
				.add( TemperatureData.DEMO_X_AXIS_DATA.get(i) )
				.add( null );

				row_index++;
			}

			// Add y-axis data
			for (int i=0; i<TemperatureData.DEMO_SERIES_LIST.length; i++) {
				for (int j=0; j<TemperatureData.DEMO_SERIES_LIST[i].size(); j++) {

					//                    c.newRow().add( Y_AXIS_INDEX ).add( i ).add( TemperatureData.DEMO_SERIES_LIST[i][j] ).add( null );
					c.newRow()
					.add( row_index )
					.add( ColumnSchema.Y_AXIS_INDEX )
					.add( i )
					.add( TemperatureData.DEMO_SERIES_LIST[i].get(j) )
					.add( null );

					row_index++;
				}
			}

			return c;
		}
	}

	@Override
	public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}


	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		for(int i=1; i<=28; i++){
			TemperatureData.DEMO_X_AXIS_DATA.add(new Integer(i*5));
			TemperatureData.DEMO_SERIES_LIST[0].add(new Integer(contentvalues.getAsInteger(""+i*5)));
		}
		String userId;
		userId = contentvalues.getAsString("userId");
		if(userId != null){
			TemperatureData.DEMO_TITLES[0] = userId;
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String s, String[] as) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}
}
