package com.example.musichub.api;

import com.example.musichub.model.Album.Album;
import com.example.musichub.model.artist.ArtistDetail;
import com.example.musichub.model.chart.chart_home.ChartHome;
import com.example.musichub.model.chart.new_release.NewRelease;
import com.example.musichub.model.chart.top100.Top100;
import com.example.musichub.model.new_release.NewReleaseAlbum;
import com.example.musichub.model.new_release.NewReleaseSong;
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
    String pathChartNewRelease = "/api/v2/page/get/newrelease-chart?";
    String pathNewRelease = "/api/v2/chart/get/new-release?";
    String pathTop100 = "/api/v2/page/get/top-100?";
    String pathDetailSong = "/api/v2/song/get/info?";
    String pathAudioSong = "/api/v2/song/get/streaming?";
    String pathArtist = "/api/v2/page/get/artist?";
    String pathPlaylist = "/api/v2/page/get/playlist?";
    String pathAlbum = "/api/v2/page/get/album?";
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

    @GET(pathPlaylist)
    Call<Playlist> PLAYLIST_CALL(@Query("id") String id,
                                 @Query("sig") String sig,
                                 @Query("ctime") String ctime,
                                 @Query("version") String version,
                                 @Query("apiKey") String apiKey);

    @GET(pathAlbum)
    Call<Album> ALBUM_CALL(@Query("id") String id,
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

    @GET(pathSearchType)
    Call<Search> SEARCH_TYPE_CALL(@Query("q") String q,
                                  @Query("type") String type,
                                  @Query("count") String count,
                                  @Query("page") String page,
                                  @Query("sig") String sig,
                                  @Query("ctime") String ctime,
                                  @Query("version") String version,
                                  @Query("apiKey") String apiKey);

    @GET(pathSearchRecommend)
    Call<Search> SEARCH_RECOMMEND_CALL(@Query("q") String q,
                                       @Query("sig") String sig,
                                       @Query("ctime") String ctime,
                                       @Query("version") String version,
                                       @Query("apiKey") String apiKey);


    // Method để thiết lập cookie
    void setCookie(String cookie);
}
