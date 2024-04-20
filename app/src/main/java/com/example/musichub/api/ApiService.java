package com.example.musichub.api;

import com.example.musichub.model.artist.ArtistDetail;
import com.example.musichub.model.chart.chart_home.ChartHome;
import com.example.musichub.model.chart.new_release.NewRelease;
import com.example.musichub.model.chart.top100.Top100;
import com.example.musichub.model.playlist.Playlist;
import com.example.musichub.model.search.Search;
import com.example.musichub.model.song.Lyric;
import com.example.musichub.model.song.SongAudio;
import com.example.musichub.model.song.SongDetail;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    String pathChartHome = "/api/v2/page/get/chart-home?";
    String pathHome = "/api/v2/page/get/home?";
    String pathNewRelease = "/api/v2/page/get/newrelease-chart?";
    String pathTop100 = "/api/v2/page/get/top-100?";
    String pathDetailSong = "/api/v2/song/get/info?";
    String pathAudioSong = "/api/v2/song/get/streaming?";
    String pathArtist = "/api/v2/page/get/artist?";
    String pathPlaylist = "/api/v2/page/get/playlist?";

    String pathLyric = "/api/v2/lyric/get/lyric?";

    //search
    String pathSearch = "/api/v2/search/multi?";
    String pathSearchType = "/api/v2/search?";
    String pathSearchRecommend = "/api/v2/app/get/recommend-keyword?";


    //HOME
    @GET(pathChartHome)
    Call<ChartHome> CHART_HOME_CALL(@Query("sig") String sig,
                                    @Query("ctime") String ctime,
                                    @Query("version") String version,
                                    @Query("apiKey") String apiKey);

    @GET(pathHome)
    Call<ResponseBody> HOME_CALL(@Query("sig") String sig,
                                 @Query("ctime") String ctime,
                                 @Query("version") String version,
                                 @Query("apiKey") String apiKey);

    @GET(pathNewRelease)
    Call<NewRelease> NEW_RELEASE_CALL(@Query("sig") String sig,
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
    Call<ArtistDetail> ARTISTS_CALL(@Query("alias") String alias,
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


    @GET(pathSearch)
    Call<Search> SEARCH_CALL(@Query("q") String q,
                             @Query("sig") String sig,
                             @Query("ctime") String ctime,
                             @Query("version") String version,
                             @Query("apiKey") String apiKey);

    @GET(pathSearchRecommend)
    Call<Search> SEARCH_RECOMMEND_CALL(@Query("sig") String sig,
                                       @Query("ctime") String ctime,
                                       @Query("version") String version,
                                       @Query("apiKey") String apiKey);


    // Method để thiết lập cookie
    void setCookie(String cookie);
}
