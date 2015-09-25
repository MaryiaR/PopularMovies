package com.udacity.mrasulava.popularmovies.data;

import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.mrasulava.popularmovies";

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        // Columns
        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_RATING = "rating";

        public static final String COLUMN_VIDEO_ID = "video_id";

        public static final String COLUMN_RELEASE_DATE = "release_date";

    }

    public static final class TrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "trailers";

        public static final String CONTENT_URI_PATH = TABLE_NAME;

        // Columns
        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_VIDEO_KEY = "key";

    }

    public static final class ReviewEntry implements BaseColumns {

        public static final String TABLE_NAME = "reviews";

        public static final String CONTENT_URI_PATH = TABLE_NAME;

        // Columns
        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_CONTENT = "content";

        public static final String COLUMN_MOVIE = "movie";

    }

}
