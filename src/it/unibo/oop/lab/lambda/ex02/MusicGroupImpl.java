package it.unibo.oop.lab.lambda.ex02;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        List<String> songname = new LinkedList<>();
        songs.stream().sorted((o1, o2) -> o1.getSongName().compareTo(o2.getSongName()))
                                 .forEach(x -> songname.add(x.getSongName()));
        return songname.stream();
    }

    @Override
    public Stream<String> albumNames() {
        List<String> albumName = new LinkedList<>();
        albums.keySet().stream().sorted((o1, o2) -> o1.compareTo(o2)).forEach(x -> albumName.add(x));
        return albumName.stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        List<String> albumInYear = new LinkedList<>();
        albums.entrySet().stream().filter(x -> x.getValue().equals(year))
              .forEach(x -> albumInYear.add(x.getKey()));
        System.out.println(albumInYear);
        return albumInYear.stream();
    }

    @Override
    public int countSongs(final String albumName) {
        List<String> numberSongs = new LinkedList<>();
        songs.stream().filter(x -> x.getAlbumName().equals(Optional.of(albumName)))
                      .forEach(x -> numberSongs.add(x.songName));
        System.out.println(numberSongs);
        return numberSongs.size();
    }

    @Override
    public int countSongsInNoAlbum() {
        List<String> numberSongsNoAlbum = new LinkedList<>();
        songs.stream().filter(x -> x.getAlbumName().equals(Optional.empty()))
                      .forEach(x -> numberSongsNoAlbum.add(x.songName));
        System.out.println(numberSongsNoAlbum);
        return numberSongsNoAlbum.size();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return songs.stream().filter(x -> x.getAlbumName().equals(Optional.of(albumName)))
             .mapToDouble(x -> x.getDuration()).average();
    }

    @Override
    public Optional<String> longestSong() {
        return songs.stream().collect(Collectors.maxBy((o1, o2) -> Double.compare(o1.getDuration(), o2.getDuration())))
                    .map(x -> x.getSongName());
    } 
    
    @Override
    public Optional<String> longestAlbum() {
        return songs.stream().filter(x -> x.getAlbumName().isPresent())
                    .collect(Collectors.groupingBy((x) -> x.getAlbumName(), Collectors.summingDouble((x) -> x.getDuration())))
                    .entrySet()
                    .stream()
                    .collect(Collectors.maxBy((o1, o2) -> Double.compare(o1.getValue(), o2.getValue())))
                    .map(x -> x.getKey().get());
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
