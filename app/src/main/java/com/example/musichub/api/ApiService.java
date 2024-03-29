package com.example.musichub.api;

import com.example.musichub.model.artist.ArtistDetail;
import com.example.musichub.model.chart_home.ChartHome;
import com.example.musichub.model.search.Search;
import com.example.musichub.model.song.Lyric;
import com.example.musichub.model.song.SongAudio;
import com.example.musichub.model.song.SongDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    String pathChartHome = "/api/v2/page/get/chart-home?";
    String pathDetailSong = "/api/v2/song/get/info?";
    String pathAudioSong = "/api/v2/song/get/streaming?";
    String pathArtist = "/api/v2/page/get/artist?";
    String pathSearch = "/api/v2/search/multi?";
    String pathLyric = "/api/v2/lyric/get/lyric?";

    //HOME
    @GET(pathChartHome)
    Call<ChartHome> CHART_HOME_CALL(@Query("sig") String sig,
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

    @GET(pathSearch)
    Call<Search> SEARCH_CALL(@Query("q") String q,
                             @Query("sig") String sig,
                             @Query("ctime") String ctime,
                             @Query("version") String version,
                             @Query("apiKey") String apiKey);

    // Method để thiết lập cookie
    void setCookie(String cookie);
}
