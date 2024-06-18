package com.example.musichub.api;

import com.example.musichub.model.chart.chart_home.ChartHome;
import com.example.musichub.model.chart.new_release.NewRelease;
import com.example.musichub.model.chart.top100.Top100;
import com.example.musichub.model.chart.weekchart.WeekChart;
import com.example.musichub.model.new_release.NewReleaseAlbum;
import com.example.musichub.model.new_release.NewReleaseSong;
import com.example.musichub.model.playlist.Playlist;
import com.example.musichub.model.search.search_suggestion.SearchSuggestions;
import com.example.musichub.model.song.Lyric;
import com.example.musichub.model.song.SongAudio;
import com.example.musichub.model.song.SongDetail;
import com.example.musichub.model.song_of_artist.SongOfArtist;
import com.example.musichub.model.user_active_radio.UserActiveRadio;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    String pathChartHome = "/api/v2/page/get/chart-home?";
    String pathHome = "/api/v2/page/get/home?";
    String pathChartNewRelease = "/api/v2/page/get/newrelease-chart?";
    String pathNewRelease = "/api/v2/chart/get/new-release?";
    String pathWeekChart = "/api/v2/page/get/week-chart?";
    String pathSectionBottom = "/api/v2/playlist/get/section-bottom?";
    String pathTop100 = "/api/v2/page/get/top-100?";
    String pathDetailSong = "/api/v2/song/get/info?";
    String pathAudioSong = "/api/v2/song/get/streaming?";
    String pathArtist = "/api/v2/page/get/artist?";
    String pathSongListOfArtist = "/api/v2/song/get/list?";
    String pathPlaylist = "/api/v2/page/get/playlist?";
    String pathAlbum = "/api/v2/page/get/album?";
    String pathLyric = "/api/v2/lyric/get/lyric?";

    //search
    String pathSearch = "/api/v2/search/multi?";
    String pathSearchType = "/api/v2/search?";
    String pathSearchSuggestion = "/v1/web/ac-suggestions?";
    String pathSearchRecommend = "/api/v2/app/get/recommend-keyword?";


    //radio
    String pathUserActiveRadio = "/api/v2/livestream/get/active-user?";
    String pathInfoRadio = "/api/v2/livestream/get/info?";

    //hub
    String pathHub = "/api/v2/page/get/hub-detail?";


    //HOME
    @GET(pathChartHome)
    Call<ChartHome> CHART_HOME_CALL(@Query("sig") String sig,
                                    @Query("ctime") String ctime,
                                    @Query("version") String version,
                                    @Query("apiKey") String apiKey);

    @GET(pathHome)
    Call<ResponseBody> HOME_CALL(@Query("page") String page,
                                 @Query("count") String count,
                                 @Query("sig") String sig,
                                 @Query("ctime") String ctime,
                                 @Query("version") String version,
                                 @Query("apiKey") String apiKey);

    @GET(pathChartNewRelease)
    Call<NewRelease> CHART_NEW_RELEASE_CALL(@Query("sig") String sig,
                                            @Query("ctime") String ctime,
                                            @Query("version") String version,
                                            @Query("apiKey") String apiKey);

    @GET(pathNewRelease)
    Call<NewReleaseSong> NEW_RELEASE_SONG_CALL(@Query("type") String type,
                                               @Query("sig") String sig,
                                               @Query("ctime") String ctime,
                                               @Query("version") String version,
                                               @Query("apiKey") String apiKey);

    @GET(pathNewRelease)
    Call<NewReleaseAlbum> NEW_RELEASE_ALBUM_CALL(@Query("type") String type,
                                                 @Query("sig") String sig,
                                                 @Query("ctime") String ctime,
                                                 @Query("version") String version,
                                                 @Query("apiKey") String apiKey);

    @GET(pathWeekChart)
    Call<WeekChart> WEEK_CHART_CALL(@Query("id") String id,
                                    @Query("week") String week,
                                    @Query("year") String year,
                                    @Query("sig") String sig,
                                    @Query("ctime") String ctime,
                                    @Query("version") String version,
                                    @Query("apiKey") String apiKey);

    @GET(pathSectionBottom)
    Call<ResponseBody> SECTION_BOTTOM_CALL(@Query("id") String id,
                                           @Query("sig") String sig,
                                           @Query("ctime") String ctime,
                                           @Query("version") String version,
                                           @Query("apiKey") String apiKey);

    @GET(pathTop100)
    Call<Top100> TOP100_CALL(@Query("sig") String sig,
                             @Query("ctime") String ctime,
                             @Query("version") String version,
                             @Query("apiKey") String apiKey);


    //SONG
    @GET(pathDetailSong)
    Call<SongDetail> SONG_DETAIL_CALL(@Query("id") String id,
                                      @Query("sig") String sig,
                                      @Query("ctime") String ctime,
                                      @Query("version") String version,
                                      @Query("apiKey") String apiKey);

    @GET(pathAudioSong)
    Call<SongAudio> SONG_AUDIO_CALL(@Query("id") String id,
                                    @Query("sig") String sig,
                                    @Query("ctime") String ctime,
                                    @Query("version") String version,
                                    @Query("apiKey") String apiKey);

    @GET(pathLyric)
    Call<Lyric> LYRIC_CALL(@Query("id") String id,
                           @Query("sig") String sig,
                           @Query("ctime") String ctime,
                           @Query("version") String version,
                           @Query("apiKey") String apiKey);

    @GET(pathArtist)
    Call<ResponseBody> ARTISTS_CALL(@Query("alias") String alias,
                                    @Query("sig") String sig,
                                    @Query("ctime") String ctime,
                                    @Query("version") String version,
                                    @Query("apiKey") String apiKey);

    @GET(pathSongListOfArtist)
    Call<SongOfArtist> SONG_LIST_OF_ARTIST_CALL(@Query("id") String id,
                                                @Query("type") String type,
                                                @Query("page") String page,
                                                @Query("count") String count,
                                                @Query("sort") String sort,
                                                @Query("sectionId") String sectionId,
                                                @Query("sig") String sig,
                                                @Query("ctime") String ctime,
                                                @Query("version") String version,
                                                @Query("apiKey") String apiKey);

    @GET(pathPlaylist)
    Call<Playlist> PLAYLIST_CALL(@Query("id") String id,
                                 @Query("sig") String sig,
                                 @Query("ctime") String ctime,
                                 @Query("version") String version,
                                 @Query("apiKey") String apiKey);

    @GET(pathAlbum)
    Call<com.example.musichub.model.Album.Album> ALBUM_CALL(@Query("id") String id,
                                                            @Query("sig") String sig,
                                                            @Query("ctime") String ctime,
                                                            @Query("version") String version,
                                                            @Query("apiKey") String apiKey);

    @GET(pathSearch)
    Call<SearchSuggestions> SEARCH_CALL(@Query("q") String q,
                                        @Query("sig") String sig,
                                        @Query("ctime") String ctime,
                                        @Query("version") String version,
                                        @Query("apiKey") String apiKey);

    @GET(pathSearchSuggestion)
    Call<ResponseBody> SEARCH_SUGGESTIONS_CALL(@Query("num") String num,
                                               @Query("query") String query,
                                               @Query("language") String language,
                                               @Query("sig") String sig,
                                               @Query("ctime") String ctime,
                                               @Query("version") String version,
                                               @Query("apiKey") String apiKey);

    @GET(pathSearchType)
    Call<SearchSuggestions> SEARCH_TYPE_CALL(@Query("q") String q,
                                             @Query("type") String type,
                                             @Query("count") String count,
                                             @Query("page") String page,
                                             @Query("sig") String sig,
                                             @Query("ctime") String ctime,
                                             @Query("version") String version,
                                             @Query("apiKey") String apiKey);

    @GET(pathSearchRecommend)
    Call<SearchSuggestions> SEARCH_RECOMMEND_CALL(@Query("q") String q,
                                                  @Query("sig") String sig,
                                                  @Query("ctime") String ctime,
                                                  @Query("version") String version,
                                                  @Query("apiKey") String apiKey);

    @GET(pathUserActiveRadio)
    Call<UserActiveRadio> USER_ACTIVE_RADIO_CALL(@Query("ids") String ids,
                                                 @Query("sig") String sig,
                                                 @Query("ctime") String ctime,
                                                 @Query("version") String version,
                                                 @Query("apiKey") String apiKey);

    @GET(pathInfoRadio)
    Call<ResponseBody> INFO_RADIO_CALL(@Query("id") String id,
                                       @Query("sig") String sig,
                                       @Query("ctime") String ctime,
                                       @Query("version") String version,
                                       @Query("apiKey") String apiKey);


    @GET(pathHub)
    Call<ResponseBody> HUB_DETAIL_CALL(@Query("id") String id,
                                       @Query("sig") String sig,
                                       @Query("ctime") String ctime,
                                       @Query("version") String version,
                                       @Query("apiKey") String apiKey);

    // Method để thiết lập cookie
    void setCookie(String cookie);
}
