package com.gmail.takenokoii78.nbt;

import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

@NullMarked
public class NbtFile {
    private final Path path;

    public NbtFile(Path path) {
        this.path = path;

        if (path.toFile().exists() && !path.toFile().isFile()) {
            throw new IllegalArgumentException("パス '" + path + "' はファイルパスとして無効です");
        }
    }

    public NbtFile(String path) {
        this(Path.of(path));
    }

    public NbtFile(File file) {
        this(file.toPath());
    }

    public Path getPath() {
        return path;
    }

    public boolean exists() {
        return path.toFile().exists();
    }

    public void create() throws IllegalStateException {
        if (exists()) throw new IllegalStateException("既にファイルは存在します");
        else try {
            Files.createFile(path);
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルの作成に失敗しました", e);
        }
    }

    public void delete() throws IllegalStateException {
        if (exists()) try {
            Files.delete(path);
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルの削除に失敗しました", e);
        }
        else throw new IllegalStateException("ファイルが存在しません");
    }

    public long getSize() throws IllegalStateException {
        if (exists()) try {
            return Files.size(path);
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルサイズの取得に失敗しました", e);
        }
        else throw new IllegalStateException("ファイルが存在しません");
    }

    public MojangsonCompound readAsRaw() throws NbtReadException {
        return NbtReader.raw(path.toFile());
    }

    public void writeAsRaw(MojangsonCompound compound) throws NbtWriteException {
        NbtWriter.raw(path.toFile(), compound);
    }

    public void editAsRaw(Function<MojangsonCompound, MojangsonCompound> function) throws NbtReadException, NbtWriteException {
        writeAsRaw(function.apply(readAsRaw()));
    }

    public MojangsonCompound readAsCompressed() throws NbtReadException {
        return NbtReader.decompress(path.toFile());
    }

    public void writeAsCompressed(MojangsonCompound compound) throws NbtWriteException {
        NbtWriter.compress(path.toFile(), compound);
    }

    public void editAsCompressed(Function<MojangsonCompound, MojangsonCompound> function) throws NbtReadException, NbtWriteException {
        writeAsCompressed(function.apply(readAsCompressed()));
    }

    public boolean isCompressed() {
        return NbtReader.isCompressed(path.toFile());
    }

    public MojangsonCompound readAuto() throws NbtReadException {
        if (isCompressed()) {
            return readAsCompressed();
        }
        else {
            return readAsRaw();
        }
    }

    public void editAuto(Function<MojangsonCompound, MojangsonCompound> function) throws NbtReadException, NbtWriteException {
        if (isCompressed()) {
            editAsCompressed(function);
        }
        else {
            editAsRaw(function);
        }
    }
}
